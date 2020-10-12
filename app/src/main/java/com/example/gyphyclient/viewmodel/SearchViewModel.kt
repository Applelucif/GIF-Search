package com.example.gyphyclient.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gyphyclient.data.database.DataFavoriteEntity
import com.example.gyphyclient.data.database.toDataList
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.repository.TrendingRepository
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel : ViewModel() {

    @Inject
    lateinit var repository: TrendingRepository

    private val _data by lazy { MutableLiveData<List<Data>>() }
    val data: MutableLiveData<List<Data>>
        get() = _data

    val _isInProgress by lazy { MutableLiveData<Boolean>() }
    val isInProgress: MutableLiveData<Boolean>
        get() = _isInProgress

    val _isError by lazy { MutableLiveData<Boolean>() }
    val isError: MutableLiveData<Boolean>
        get() = _isError

    private val compositeDisposable by lazy { CompositeDisposable() }
    private val querySearchProcessor = BehaviorProcessor.create<String>()

    init {
        DaggerAppComponent.create().inject(this)
        compositeDisposable.add(
            querySearchProcessor
                .debounce(timeoutWhileSearch, TimeUnit.MILLISECONDS)
                .switchMap { value ->
                    repository.querySearchData(value).toFlowable()
                }
                .subscribe { (searchText, list) ->
                    //TODO состояния view перенести во viewmodel
                    isInProgress.postValue(true)
                    if (list.isNotEmpty()) {
                        isError.postValue(false)
                        data.postValue(list.toDataList())
                    } else {
                        repository.searchGif(searchText)
                    }
                    isInProgress.postValue(false)
                })
    }

    fun getListSearch(searchTerm: String): Disposable {
        return repository.querySearchData(searchTerm)
        .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { (searchText, dataEntityList) ->
                    _isInProgress.postValue(true)
                    if (dataEntityList != null && dataEntityList.isNotEmpty()) {
                        _isError.postValue(false)
                        repository.setList(dataEntityList.toDataList())
                    } else {
                        repository.searchGif(searchTerm)
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

    fun search(query: String) {
        querySearchProcessor.onNext(query)
    }

    fun getGifFlow(): Flowable<List<Data>> {
        return repository.getGifFlow()
    }

    fun addToFavorite(gif: Data) {
            repository.insertFavoriteData(gif)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        private const val timeoutWhileSearch: Long = 500
    }
}