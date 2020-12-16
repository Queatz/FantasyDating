package com.queatz.fantasydating

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition
import com.queatz.on.On

class PhotoFeature constructor(private val on: On) {

    @ExperimentalCoilApi
    private val loader = ImageLoader.Builder(on<ContextFeature>().context).transition(CrossfadeTransition(220, true)).build()

    @ExperimentalCoilApi
    fun load(url: String, imageView: ImageView, @DrawableRes placeholder: Int? = null, successListener: (() -> Unit)? = null) {
        val request = ImageRequest.Builder(on<ContextFeature>().context)
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
        loader.enqueue(request)
    }

    @ExperimentalCoilApi
    fun load(url: String, callback: (Drawable) -> Unit) {
        val request = ImageRequest.Builder(on<ContextFeature>().context)
            .data(url)
            .target({}, {}, { callback(it) })
            .build()
        loader.enqueue(request)
    }

    @ExperimentalCoilApi
    fun preload(url: String) {
        val request = ImageRequest.Builder(on<ContextFeature>().context)
            .data(url)
            .target({}, {}, {})
            .build()

        loader.enqueue(request)
    }
}