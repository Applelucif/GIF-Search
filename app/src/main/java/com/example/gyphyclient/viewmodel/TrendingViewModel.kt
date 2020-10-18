package com.example.gyphyclient.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gyphyclient.data.database.toDataList
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.repository.TrendingRepository
import com.example.gyphyclient.view.ui.TrendingFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class TrendingViewModel : ViewModel() {

    @Inject
    lateinit var repository: TrendingRepository

    private val compositeDisposable by lazy { CompositeDisposable() }

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
        compositeDisposable.add(getListTrending())
    }

    fun getData(offset: Int) {
        repository.insertData(offset)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun addToFavorite(gif: Data) {
        Thread {
            repository.insertFavoriteData(gif)
        }.start()
    }

    private fun getListTrending(): Disposable {
        return repository.getTrendingQuery()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dataEntityList ->
                    _isInProgress.postValue(true)
                    if (dataEntityList != null && dataEntityList.isNotEmpty()) {
                        _isError.postValue(false)
                        _data.postValue(dataEntityList.toDataList())
                    } else {
                        repository.insertData(repository.offset)
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
}
