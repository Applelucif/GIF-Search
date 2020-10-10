package com.example.gyphyclient.viewmodel

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.repository.TrendingRepository
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TrendingViewModel : ViewModel() {

    @Inject
    lateinit var repository: TrendingRepository

    private val compositeDisposable by lazy { CompositeDisposable() }
    private val downloadsDisposable by lazy { CompositeDisposable() }

    init {
        DaggerAppComponent.create().inject(this)
        compositeDisposable.add(repository.fetchDataFromDatabase())
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
}
