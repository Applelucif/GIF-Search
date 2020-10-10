package com.example.gyphyclient.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gyphyclient.data.database.toDataList
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.repository.TrendingRepository
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel : ViewModel() {

    @Inject
    lateinit var repository: TrendingRepository

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
                    repository.isInProgress.postValue(true)
                    if (list.isNotEmpty()) {
                        repository.isError.postValue(false)
                        repository.data.postValue(list.toDataList())
                    } else {
                        repository.searchGif(searchText)
                    }
                    repository.isInProgress.postValue(false)
                })
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