package com.example.gyphyclient.internal

import android.graphics.drawable.Drawable
import android.os.FileUtils
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.example.gyphyclient.GiphyApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.*


@BindingAdapter(*["bind:imageUrl", "bind:urlSmallGif", "bind:hash"])
fun setImage(imageView: ImageView, url: String?, urlSmallGif: String, hash: String) {
    var imageFileName = hash + ".gif"
    var storageDir =
        File(imageView.context.cacheDir.absolutePath + "/image_manager_disk_cache")
    val imageFile: File = File(storageDir, imageFileName)


    if (imageFile.exists()) {
        Glide.with(imageView)
            .load(imageFile)
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .asGif()
            .load(url)
            .thumbnail(Glide.with(imageView.context).asGif().load(urlSmallGif))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(object : CustomTarget<GifDrawable>(100, 100) {

                override fun onResourceReady(
                    resource: GifDrawable,
                    transition: Transition<in GifDrawable>?
                ) {
                    try {
                        val fOut: OutputStream = FileOutputStream(imageFile)
                        val byteBuffer = resource.buffer
                        val bytes = ByteArray(byteBuffer.capacity())
                        (byteBuffer.duplicate().clear() as ByteBuffer).get(bytes)
                        fOut.write(bytes, 0, bytes.size)
                        fOut.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Glide.with(imageView)
                        .load(imageFile)
                        .into(imageView)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

}
