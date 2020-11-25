package com.example.gyphyclient.internal

import android.net.Uri
import android.os.Environment
import androidx.databinding.BindingAdapter
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.cache.common.CacheKey
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.request.ImageRequest
import com.thin.downloadmanager.DownloadRequest
import com.thin.downloadmanager.ThinDownloadManager
import java.io.File

@BindingAdapter(*["bind:imageUrl", "bind:urlSmallGif", "bind:imageUrltoShare", "bind:hash", "bind:height", "bind:width"])
fun setImage(
    imageView: SimpleDraweeView,
    url: String,
    urlToSave: String,
    urlSmallGif: String,
    hash: String,
    height: String,
    width: String
) {
    var imageFileName = "$hash.webp"
    var smallImageFileName = "$hash.jpg"
    var storageDir =
        File("${imageView.context.cacheDir.absolutePath}/image_manager_disk_cache")
    val imageFile = File(storageDir, imageFileName)
    val smallimageFile = File(storageDir, smallImageFileName)
    var aspectRatio: Float = (width.toFloat() / height.toFloat())

    if (imageFile.exists()) {
        val uri: Uri = Uri.parse("file://${imageFile.path}")
        val controller = Fresco.newDraweeControllerBuilder()
            .setUri(uri)
            .setAutoPlayAnimations(true)
            .build()
        imageView.apply {
            setAspectRatio(aspectRatio)
            setController(controller)
        }
    } else {
/*        val uriSmallGif: Uri = Uri.parse(urlSmallGif)
        val destUriSmall: Uri = Uri.parse("file://${smallimageFile.path}")
        val request = DownloadRequest(uriSmallGif)
            .setDestinationURI(destUriSmall)
        var downloadManager = ThinDownloadManager()
        downloadManager.add(request)*/

        //TODO работает с URL, а с файлом из памяти - нет. Fresco не может прочитать jpg, который сохранен из gif
        val controller = Fresco.newDraweeControllerBuilder()
            //.setLowResImageRequest(ImageRequest.fromUri(destUriSmall))
            .setImageRequest(ImageRequest.fromUri(url))
            .setOldController(imageView.controller)
            .setAutoPlayAnimations(true)
            .build()
        imageView.apply {
            setAspectRatio(aspectRatio)
            setController(controller)
        }

        val imageRequest = ImageRequest.fromUri(url)
        try {
            val cacheKey: CacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, null)
            val resource = ImagePipelineFactory
                .getInstance()
                .mainFileCache
                .getResource(cacheKey)
            val file = (resource as FileBinaryResource).file
            file.copyTo(imageFile)
        } catch (e: NullPointerException) {
        }
    }
}