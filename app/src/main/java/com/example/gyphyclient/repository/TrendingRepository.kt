package com.example.gyphyclient.repository

import KEY
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gyphyclient.GiphyApplication
import com.example.gyphyclient.data.database.toDataEntityList
import com.example.gyphyclient.data.database.toDataList
import com.example.gyphyclient.data.database.toSearchDataEntityList
import com.example.gyphyclient.data.network.GiphyApi
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.internal.LIMIT
import com.example.gyphyclient.internal.RATING
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.model.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class TrendingRepository {

    var offset = 0

    @Inject
    lateinit var giphyApiService: GiphyApi

    private val _data by lazy { MutableLiveData<List<Data>>() }
    val data: LiveData<List<Data>>
        get() = _data

    private val _isInProgress by lazy { MutableLiveData<Boolean>() }
    val isInProgress: LiveData<Boolean>
        get() = _isInProgress

    private val _isError by lazy { MutableLiveData<Boolean>() }
    val isError: LiveData<Boolean>
        get() = _isError

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun insertData(offset : Int = 0): Disposable {
        return giphyApiService.getTrending(KEY, LIMIT, RATING, offset.toString())
            .subscribeOn(Schedulers.io())
            .subscribeWith(subscribeToDatabase())
    }

    fun insertSearchData(searchTerm: String): Disposable {
        return giphyApiService.getSeach(KEY, LIMIT, RATING, searchTerm)
            .subscribeOn(Schedulers.io())
            .subscribeWith(subscribeToSearchDatabase(searchTerm))
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

    fun fetchDataFromDatabase(): Disposable = getTrendingQuery()

    fun fetchSearchDataFromDataBase(searchTerm: String): Disposable = getSearchingQuery(searchTerm)

    private fun getSearchingQuery(searchTerm: String): Disposable {
        return GiphyApplication.database.dataDao()
            .queryData(searchTerm)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dataEntityList ->
                    _isInProgress.postValue(true)
                    if (dataEntityList != null && dataEntityList.isNotEmpty()) {
                        _isError.postValue(false)
                        _data.postValue(dataEntityList.toDataList())
                    } else {
                        insertSearchData(searchTerm)
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
}