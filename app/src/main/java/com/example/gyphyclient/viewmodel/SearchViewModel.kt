package com.example.gyphyclient.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.repository.TrendingRepository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_search.view.*
import javax.inject.Inject

class SearchViewModel : ViewModel() {

    @Inject
    lateinit var repository: TrendingRepository

    private val compositeDisposable by lazy { CompositeDisposable() }

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun search(searchText:String) {
        compositeDisposable.add(repository.fetchSearchDataFromDataBase(searchText))
        repository.insertSearchData(searchText)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}