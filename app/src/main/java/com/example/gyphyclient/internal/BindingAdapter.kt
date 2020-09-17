package com.example.gyphyclient.internal

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import java.io.*
import java.nio.ByteBuffer


@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {

    Glide.with(imageView.context)
        .asGif()
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .signature(ObjectKey(System.currentTimeMillis()))
        .into(object : CustomTarget<GifDrawable>(100, 100) {

            override fun onResourceReady(
                resource: GifDrawable,
                transition: Transition<in GifDrawable>?
            ) {
                var imageFileName = System.currentTimeMillis().toString() + ".gif"

                var storageDir =
                    File(imageView.context.cacheDir.absolutePath + "/image_manager_disk_cache")
                val imageFile: File = File(storageDir, imageFileName)
                try {
                    val fOut: OutputStream = FileOutputStream(imageFile)
                    val byteBuffer = resource.buffer
                    val bytes = ByteArray(byteBuffer.capacity())
                    (byteBuffer.duplicate().clear() as ByteBuffer).get(bytes)
                    fOut.write(bytes,0,bytes.size)
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
