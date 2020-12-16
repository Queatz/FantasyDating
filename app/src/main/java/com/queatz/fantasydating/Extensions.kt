package com.queatz.fantasydating

import android.animation.Animator
import android.graphics.PointF
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import kotlin.math.max
import kotlin.math.min

var View.visible: Boolean
    inline get() = visibility == View.VISIBLE
    inline set(value) { visibility = if (value) View.VISIBLE else View.GONE }

fun View.appear(callback: (() -> Unit)? = null) {
    if (translationY == 0f && visible) {
        callback?.invoke()
        return
    }

    translationY = height.toFloat()
    visible = true
    animate()
        .setDuration(150)
        .setInterpolator(DecelerateInterpolator())
        .translationY(0f)
        .withEndAction {
            callback?.invoke()
        }
        .start()
}

fun View.disappear(callback: (() -> Unit)? = null) {
    if (!visible) {
        callback?.invoke()
        return
    }

    animate()
        .setDuration(150)
        .setInterpolator(AccelerateInterpolator())
        .translationY(height.toFloat())
        .withEndAction {
            visible = false
            callback?.invoke()
        }
        .start()
}

fun View.fadeIn(slide: Boolean = true, reverse: Boolean = false, speed: Float = 1f, callback: (() -> Unit)? = null) {
    if (translationY == 0f && alpha == 1f && visible) {
        callback?.invoke()
        return
    }

    alpha = 0f

    if (slide) {
        translationY = resources.getDimensionPixelSize(R.dimen.pad2x).toFloat() * (if (reverse) -1 else 1)
    }

    visible = true
    animate()
        .setDuration((150 / speed).toLong())
        .setInterpolator(DecelerateInterpolator())
        .apply {
            if (slide) {
                translationY(0f)
            }
        }
        .alpha(1f)
        .withEndAction {
            callback?.invoke()
        }
        .start()
}

fun View.fadeOut(slide: Boolean = true, reverse: Boolean = false, speed: Float = 1f, callback: (() -> Unit)? = null) {
    if (!visible) {
        callback?.invoke()
        return
    }

    animate()
        .setDuration((150 / speed).toLong())
        .setInterpolator(AccelerateInterpolator())
        .apply {
            if (slide) {
                translationY(resources.getDimensionPixelSize(R.dimen.pad2x).toFloat() * (if (reverse) -1 else 1))
            }
        }
        .alpha(0f)
        .withEndAction {
            visible = false
            callback?.invoke()
        }
        .start()
}

inline infix fun Boolean.then(function: () -> Unit): Boolean {
    if (this) {
        function.invoke()
    }

    return this
}

inline infix fun Boolean.otherwise(function: () -> Unit) = this.not().then(function)

fun Float.clamp(low: Float = 0f, high: Float = 1f) = min(high, max(low, this))
fun Float.mix(value: Float, amount: Float) = this * (1f - amount) + value * amount

fun PointF.mix(value: PointF, amount: Float) = PointF(
    x.mix(value.x, amount),
    y.mix(value.y, amount)
)