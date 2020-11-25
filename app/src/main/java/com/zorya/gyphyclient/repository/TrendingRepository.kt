package com.zorya.gyphyclient.repository

import KEY
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.content.FileProvider
import com.zorya.gyphyclient.GiphyApplication
import com.zorya.gyphyclient.data.database.DataEntity
import com.zorya.gyphyclient.data.database.DataFavoriteEntity
import com.zorya.gyphyclient.data.database.toDataEntityList
import com.zorya.gyphyclient.data.database.toDataFavoriteEntity
import com.zorya.gyphyclient.data.network.GiphyApi
import com.zorya.gyphyclient.di.DaggerAppComponent
import com.zorya.gyphyclient.internal.LIMIT
import com.zorya.gyphyclient.internal.SEARCH_LIMIT
import com.zorya.gyphyclient.model.Data
import com.zorya.gyphyclient.model.Result
import com.thin.downloadmanager.DownloadRequest
import com.thin.downloadmanager.ThinDownloadManager
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import java.io.File
import javax.inject.Inject

class TrendingRepository {

    var offset = 0

    @Inject
    lateinit var giphyApiService: GiphyApi

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun insertData(offset: Int = 0): Disposable {
        var rating = ""
        val preferences =
            PreferenceManager.getDefaultSharedPreferences(GiphyApplication.getAppContext())
        preferences.apply {
            rating = getString("RATING", "").toString()
        }

        return giphyApiService.getTrending(KEY, LIMIT, rating, offset.toString())
            .subscribeOn(Schedulers.io())
            .subscribeWith(subscribeToDatabase())
    }

    fun insertFavoriteData(gif: Data) {
        Thread {
            GiphyApplication.database.dataDao().insertFavoriteData(gif.toDataFavoriteEntity())
        }.start()
    }

    private fun searchGif(searchTerm: String): Single<Result> {
        var rating = ""
        val preferences =
            PreferenceManager.getDefaultSharedPreferences(GiphyApplication.getAppContext())
        preferences.apply {
            rating = getString("RATING", "").toString()
        }
        return giphyApiService.getSeach(KEY, SEARCH_LIMIT, rating, searchTerm)
    }

    fun gifShare(data: Data, context: Context) {
        //TODO решить, что делать с поделиться
        val uriGif: Uri = Uri.parse(data.images.original?.url)
        val request = DownloadRequest(uriGif)
            .setDestinationURI(
                Uri.parse(
                    "file://${context.cacheDir.absolutePath}/image_manager_disk_cache/${data.title.replace(" ", "")}.gif"
                )
            )
        var downloadManager = ThinDownloadManager()
        downloadManager.add(request)
        val file = File(
            "${context.cacheDir.absolutePath}/image_manager_disk_cache",
            "${data.title.replace(" ", "")}.gif"
        )
        val fullPath: Uri = FileProvider.getUriForFile(
            context,
            "${context.applicationContext.packageName}.provider",
            file
        )
        val sendIntent: Intent = Intent().apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            action = Intent.ACTION_SEND
            type = "image/gif"
            putExtra(Intent.EXTRA_STREAM, fullPath)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Поделиться гифкой")
        context.startActivity(shareIntent)
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
                Log.e("insertData()", "TrendingResult error: ${t?.message}")
            }

            override fun onComplete() {
                getTrendingQuery()
            }
        }
    }

    fun getFavoriteQuery(): Flowable<List<DataFavoriteEntity>> {
        return GiphyApplication.database.dataDao()
            .queryFavoriteData()
    }

    fun getTrendingQuery(): Flowable<List<DataEntity>> {
        return GiphyApplication.database.dataDao()
            .queryData()
    }

    fun querySearchData(searchTerm: String): Single<List<Data>> {
        return searchGif(searchTerm).map {
            it.data
        }
    }
}