package com.example.gyphyclient.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.repository.TrendingRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FavoriteViewModel : ViewModel() {

    @Inject
    lateinit var repository: TrendingRepository

    private val compositeDisposable by lazy { CompositeDisposable() }


    init {
        DaggerAppComponent.create().inject(this)
        compositeDisposable.add(repository.fetchFavoriteDataFromDatabase())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun gifShare(data: Data, context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data.images.original?.url)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Поделиться гифкой")
        context.startActivity(shareIntent)
    }
}