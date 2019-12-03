package com.queatz.fantasydating.ui

import android.animation.TimeAnimator
import android.graphics.PointF
import com.queatz.fantasydating.mix
import com.queatz.on.On
import kotlin.math.abs
import kotlin.math.max

class MoveZoomHandler constructor(private val on: On, private val callback: MoveZoomHandlerCallback) {

    var scale: Float = 1f
    var origin: PointF =
        PointF()

    var targetScale: Float = 1f
    var targetOrigin: PointF =
        PointF()

    private val animationInterval = TimeAnimator().apply {
        setTimeListener { _, _, delta ->
            if (delta > 1000) {
                return@setTimeListener
            }

            scale = scale.mix(targetScale, (5.0 * delta.toDouble() / 1000.0).toFloat())
            origin = origin.mix(targetOrigin, (5.0 * delta.toDouble() / 1000.0).toFloat())

            if (abs(origin.x - targetOrigin.x) < 0.01 &&
                abs(origin.y - targetOrigin.y) < 0.01 &&
                abs(scale - targetScale) < 0.01) {
                scale = targetScale
                origin = targetOrigin
                cancel()
            }

            callback.invoke(scale, origin)
        }
    }

    fun set(scale: Float, origin: PointF) {
        targetScale = max(0.25f, scale)
        targetOrigin = origin

        if (animationInterval.isStarted.not()) {
            animationInterval.start()
        }
    }

    fun reset(scale: Float, origin: PointF) {
        this.targetScale = scale
        this.targetOrigin = origin
        this.scale = scale
        this.origin = origin
        callback.invoke(scale, origin)
    }
}

typealias MoveZoomHandlerCallback = (scale: Float, origin: PointF) -> Unit
