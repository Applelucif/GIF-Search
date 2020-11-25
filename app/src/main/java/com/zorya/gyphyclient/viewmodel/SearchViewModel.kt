package com.zorya.gyphyclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zorya.gyphyclient.di.DaggerAppComponent
import com.zorya.gyphyclient.model.Data
import com.zorya.gyphyclient.repository.TrendingRepository
import io.reactivex.Flowable
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
                .distinct()
                .filter { text -> text.isNotBlank() }
                .switchMap { value ->
                    repository.querySearchData(value)
                        .toFlowable()
                        .onErrorResumeNext(
                            Flowable.just(
                                listOf()
                            )
                        )
                }
                .subscribe { list ->
                    isInProgress.postValue(true)
                    if (list.isNotEmpty()) {
                        isError.postValue(false)
                        data.postValue(list)
                    } else {
                        isError.postValue(true)
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