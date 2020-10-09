package com.example.gyphyclient.internal

import android.net.Uri
import androidx.databinding.BindingAdapter
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.cache.common.CacheKey
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.request.ImageRequest
import java.io.File


@BindingAdapter(*["bind:imageUrl", "bind:urlSmallGif", "bind:hash", "bind:height", "bind:width"])
fun setImage(
    imageView: SimpleDraweeView,
    url: String,
    urlSmallGif: String,
    hash: String,
    height: String,
    width: String
) {
    var imageFileName = "$hash.webp"
    var storageDir =
        File(imageView.context.cacheDir.absolutePath + "/image_manager_disk_cache")
    val imageFile: File = File(storageDir, imageFileName)
    var aspectRatio: Float = (width.toFloat() / height.toFloat())

    if (imageFile.exists()) {
        val uri: Uri = Uri.parse("file://" + imageFile.path)
        val controller = Fresco.newDraweeControllerBuilder()
            .setUri(uri)
            .setAutoPlayAnimations(true)
            .build()
        imageView.apply {
            setAspectRatio(aspectRatio)
            setController(controller)
        }
    } else {

        var uri: Uri = Uri.parse("https://media1.giphy.com/media/1gWKFK22fsi1nsK5O2/giphy-preview.gif?cid=7d042737sfn2g3g2nwo76ruetzopaj4bpjwr43s6zd6g0p87&rid=giphy-preview.gif")
        val controller = Fresco.newDraweeControllerBuilder()
            .setLowResImageRequest(ImageRequest.fromUri(uri))
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