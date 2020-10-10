package com.example.gyphyclient.repository

import KEY
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.gyphyclient.GiphyApplication
import com.example.gyphyclient.data.database.*
import com.example.gyphyclient.data.network.GiphyApi
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.internal.LIMIT
import com.example.gyphyclient.internal.RATING
import com.example.gyphyclient.internal.SEARCH_LIMIT
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.model.Result
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class TrendingRepository {

    var offset = 0

    @Inject
    lateinit var giphyApiService: GiphyApi

    private val _data by lazy { MutableLiveData<List<Data>>() }
    val data: MutableLiveData<List<Data>>
        get() = _data

    val _isInProgress by lazy { MutableLiveData<Boolean>() }
    val isInProgress: MutableLiveData<Boolean>
        get() = _isInProgress

    val _isError by lazy { MutableLiveData<Boolean>() }
    val isError: MutableLiveData<Boolean>
        get() = _isError

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun gifShare(data: Data, context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data.images.original?.webp)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Поделиться гифкой")
        context.startActivity(shareIntent)
    }

    fun insertData(offset : Int = 0): Disposable {
        return giphyApiService.getTrending(KEY, LIMIT, RATING, offset.toString())
            .subscribeOn(Schedulers.io())
            .subscribeWith(subscribeToDatabase())
    }

    fun searchGif(searchTerm: String): Disposable {
        return giphyApiService.getSeach(KEY, SEARCH_LIMIT, RATING, searchTerm)
            .subscribeOn(Schedulers.io())
            .subscribeWith(subscribeToSearchDatabase(searchTerm))
    }

    fun insertFavoriteData(gif: Data) {
        Thread {
            GiphyApplication.database.dataDao().insertFavoriteData(gif.toDataFavoriteEntity())
        }.start()
    }


    private fun subscribeToDatabase(): DisposableSubscriber<Result> {
        return object : DisposableSubscriber<Result>() {
            override fun onNext(result: Result?) {
                if (result != null) {
                    val entityList = result.data.toList().toDataEntityList()
                    GiphyApplication.database.apply {
                        dataDao().insertData(entityList)
                    }
                }
            }

            override fun onError(t: Throwable?) {
                _isInProgress.postValue(true)
                Log.e("insertData()", "TrendingResult error: ${t?.message}")
                _isError.postValue(true)
                _isInProgress.postValue(false)
            }

            override fun onComplete() {
                getTrendingQuery()
            }
        }
    }

    private fun subscribeToSearchDatabase(searchTerm: String): DisposableSubscriber<Result> {
        return object : DisposableSubscriber<Result>() {
            override fun onNext(result: Result?) {
                if (result != null) {
                    val entityList = result.data.toList().toSearchDataEntityList(searchTerm)
                    GiphyApplication.database.apply {
                        dataDao().insertSearchData(entityList)
                    }
                }
            }

            override fun onError(t: Throwable?) {
                _isInProgress.postValue(true)
                Log.e("insertSearchData()", "TrendingResult error: ${t?.message}")
                _isError.postValue(true)
                _isInProgress.postValue(false)
            }

            override fun onComplete() {
                getSearchingQuery(searchTerm)
            }
        }
    }

    fun fetchDataFromDatabase(): Disposable = getTrendingQuery()

        //TODO disposable переделать во single
    fun fetchFavoriteDataFromDatabase(): Disposable = getFavoriteQuery()

    private fun getFavoriteQuery(): Disposable {
        return GiphyApplication.database.dataDao()
            .queryFavoriteData()
                //TODO все что ниже перенести во вьюмодел
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dataEntityList ->
                    _isInProgress.postValue(true)
                    if (dataEntityList != null && dataEntityList.isNotEmpty()) {
                        _isError.postValue(false)
                        _data.postValue(dataEntityList.toDataList())
                    }
                    _isInProgress.postValue(false)
                },
                {
                    _isInProgress.postValue(true)
                    Log.e("getFavoriteQuery()", "Database error: ${it.message}")
                    _isError.postValue(true)
                    _isInProgress.postValue(false)
                }
            )
    }

    private fun getTrendingQuery(): Disposable {
        return GiphyApplication.database.dataDao()
            .queryData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dataEntityList ->
                    _isInProgress.postValue(true)
                    if (dataEntityList != null && dataEntityList.isNotEmpty()) {
                        _isError.postValue(false)
                        _data.postValue(dataEntityList.toDataList())
                    } else {
                        insertData(offset)
                    }
                    _isInProgress.postValue(false)
                },
                {
                    _isInProgress.postValue(true)
                    Log.e("getTrendingQuery()", "Database error: ${it.message}")
                    _isError.postValue(true)
                    _isInProgress.postValue(false)
                }
            )
    }

    private val searchedGifProcessor = BehaviorProcessor.create<List<Data>>()

    private fun setList(list: List<Data>) {
        searchedGifProcessor.onNext(list)
    }

    fun getGifFlow(): Flowable<List<Data>> {
        return searchedGifProcessor
    }

    private fun getSearchingQuery(searchTerm: String): Disposable {
        return querySearchData(searchTerm)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dataEntityList ->
                    _isInProgress.postValue(true)
                    if (dataEntityList != null && dataEntityList.second.isNotEmpty()) {
                        _isError.postValue(false)
                        setList(dataEntityList.second.toDataList())
                    } else {
                        searchGif(searchTerm)
                    }
                    _isInProgress.postValue(false)
                },
                {
                    _isInProgress.postValue(true)
                    Log.e("getSearchingQuery()", "Database error: ${it.message}")
                    _isError.postValue(true)
                    _isInProgress.postValue(false)
                }
            )
    }

    fun querySearchData(searchTerm: String): Single<Pair<String, List<DataSearchEntity>>> {
        return GiphyApplication.database.dataDao()
            .queryData(searchTerm)
            .map {
                searchTerm to it
            }
    }
}