package com.example.gyphyclient.repository

import KEY
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.gyphyclient.GiphyApplication
import com.example.gyphyclient.data.database.*
import com.example.gyphyclient.data.network.GiphyApi
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.internal.LIMIT
import com.example.gyphyclient.internal.RATING
import com.example.gyphyclient.internal.SEARCH_LIMIT
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.model.Result
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TrendingRepository {

    var offset = 0

    @Inject
    lateinit var giphyApiService: GiphyApi

    private val downloadsDisposable by lazy { CompositeDisposable() }

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun insertData(offset: Int = 0): Disposable {
        return giphyApiService.getTrending(KEY, LIMIT, RATING, offset.toString())
            .subscribeOn(Schedulers.io())
            .subscribeWith(subscribeToDatabase())
    }

    fun insertFavoriteData(gif: Data) {
        Thread {
            GiphyApplication.database.dataDao().insertFavoriteData(gif.toDataFavoriteEntity())
        }.start()
    }

    fun searchGif(searchTerm: String): Disposable {
        return giphyApiService.getSeach(KEY, SEARCH_LIMIT, RATING, searchTerm)
            .subscribeOn(Schedulers.io())
            .subscribeWith(subscribeToSearchDatabase(searchTerm))
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

    fun gifSave(data: Data, context: Context) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uriGif: Uri = Uri.parse(data.images.original?.webp.toString())
        val aExtDcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val request = DownloadManager
            .Request(uriGif)
            .setTitle(data.title.substringBefore("GIF"))
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.parse("file://" + aExtDcimDir.path + "/${data.title}.gif"))
        val dialog = ProgressDialog(context)
        dialog.setMessage("Идет сохранение гифки, пожалуйста, подождите...")
        dialog.setCancelable(false)
        dialog.max = 100
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.show()
        val downloadId = downloadManager.enqueue(request)

        val progressFlow = Flowable
            .interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val query = DownloadManager.Query()
                query.setFilterById(downloadId)

                val c: Cursor = downloadManager.query(query)
                c.use { c ->
                    if (c.moveToFirst()) {
                        val sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        val downloadedIndex =
                            c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val size = c.getInt(sizeIndex).toLong()
                        val downloaded = c.getInt(downloadedIndex).toLong()
                        var progress = 0.0
                        if (size != -1L)
                            progress = downloaded * 100.0 / size
                        dialog.progress = progress.toInt()

                        if (progress == 100.0) {
                            dialog.cancel()
                            downloadsDisposable.clear()
                        }
                    }
                }
            }
        downloadsDisposable.add(progressFlow)
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
                Log.e("insertSearchData()", "TrendingResult error: ${t?.message}")
            }

            override fun onComplete() {
                getSearchingQuery(searchTerm)
            }
        }
    }

    fun getFavoriteQuery(): Single<List<DataFavoriteEntity>> {
        return GiphyApplication.database.dataDao()
            .queryFavoriteData()
    }

    fun getTrendingQuery(): Single<List<DataEntity>> {
        return GiphyApplication.database.dataDao()
            .queryData()
    }

    private val searchedGifProcessor = BehaviorProcessor.create<List<Data>>()

    fun setList(list: List<Data>) {
        searchedGifProcessor.onNext(list)
    }

    fun getGifFlow(): Flowable<List<Data>> {
        return searchedGifProcessor
    }

    private fun getSearchingQuery(searchTerm: String): Single<Pair<String, List<DataSearchEntity>>> {
        return querySearchData(searchTerm)
    }

    fun querySearchData(searchTerm: String): Single<Pair<String, List<DataSearchEntity>>> {
        return GiphyApplication.database.dataDao()
            .queryData(searchTerm)
            .map {
                searchTerm to it
            }
    }
}