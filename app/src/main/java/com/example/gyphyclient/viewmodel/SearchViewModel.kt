package com.example.gyphyclient.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.gyphyclient.data.database.toDataList
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

    private val compositeDisposable by lazy { CompositeDisposable() }
    private val behaviorProcess = BehaviorProcessor.create<String>()

    init {
            DaggerAppComponent.create().inject(this)
            compositeDisposable.add(
            behaviorProcess
                .debounce(500, TimeUnit.MILLISECONDS)
                .switchMap { value ->
                    repository.querySearchData(value).toFlowable()
                }
                .subscribe { (searchText, list) ->
                    repository.isInProgress.postValue(true)
                    if (list.isNotEmpty()) {
                        repository.isError.postValue(false)
                        repository.data.postValue(list.toDataList())
                    } else {
                        repository.insertSearchData(searchText)
                    }
                    repository.isInProgress.postValue(false)
                })
        }

    fun putString (chtoto: String) {
        behaviorProcess.onNext(chtoto)
    }

    fun addToFavorite (gif: Data) {
        Thread{
            repository.insertFavoriteData(gif)
        }.start()

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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}