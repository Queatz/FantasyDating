package com.queatz.fantasydating

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.request.LoadRequest
import com.queatz.on.On

class PhotoFeature constructor(private val on: On) {

    private val loader = ImageLoader.Builder(on<ContextFeature>().context).crossfade(220).build()

    fun load(url: String, imageView: ImageView, @DrawableRes placeholder: Int? = null, successListener: (() -> Unit)? = null) {
        val request = LoadRequest.Builder(on<ContextFeature>().context)
            .data(url)
            .target(imageView)
            .apply {
                placeholder?.let { placeholder(it) }
                successListener?.let {
                    listener(onSuccess = { _, _ ->
                        successListener()
                    })
                }
            }
            .build()
        loader.execute(request)
    }

    fun load(url: String, callback: (Drawable) -> Unit) {
        val request = LoadRequest.Builder(on<ContextFeature>().context)
            .data(url)
            .target({}, {}, { callback(it) })
            .build()
        loader.execute(request)
    }

    fun preload(url: String) {
        val request = LoadRequest.Builder(on<ContextFeature>().context)
            .data(url)
            .target({}, {}, {})
            .build()

        loader.execute(request)
    }
}