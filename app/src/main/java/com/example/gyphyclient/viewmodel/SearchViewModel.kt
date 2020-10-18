package com.example.gyphyclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.repository.TrendingRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
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
                .subscribe { list ->
                    isInProgress.postValue(true)
                    if (list.isNotEmpty()) {
                        isError.postValue(false)
                        data.postValue(list)
                    } else {
                    }
                    isInProgress.postValue(false)
                })
    }

    fun search(query: String) {
        querySearchProcessor.onNext(query)
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