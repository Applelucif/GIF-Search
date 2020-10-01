package com.example.gyphyclient.internal

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer


@BindingAdapter(*["bind:imageUrl", "bind:urlSmallGif", "bind:hash"])
fun setImage(imageView: ImageView, url: String?, urlSmallGif: String, hash: String) {
    var imageFileName = hash + ".webp"
    var storageDir =
        File(imageView.context.cacheDir.absolutePath + "/image_manager_disk_cache")
    val imageFile: File = File(storageDir, imageFileName)


    if (imageFile.exists()) {
        Glide.with(imageView)
            .load(imageFile)
            .into(imageView)
    } else {
        val fitCenter: Transformation<Bitmap> = FitCenter()
        Glide.with(imageView.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .optionalTransform(fitCenter)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(fitCenter))
            .into(imageView)/*(object : CustomTarget<WebpDrawable>(100, 100) {

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: WebpDrawable,
                    transition: Transition<in WebpDrawable>?
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
            })*/
    }

}
