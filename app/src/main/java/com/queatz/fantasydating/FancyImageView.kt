package com.queatz.fantasydating

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.widget.ImageView
import kotlin.math.max

class FancyImageView : ImageView {
    constructor(context: Context) : super(context) { initialize() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initialize() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { initialize() }

    var scale: Float = 1f
        set(value) {
            field = value
            calc()
        }

    var origin: PointF = PointF(0.5f, 0.125f)
        set(value) {
            field = value
            calc()
        }

    private fun initialize() {
        scaleType = ScaleType.MATRIX
    }

    override fun onDraw(canvas: Canvas?) {
        calc()
        super.onDraw(canvas)
    }

    private fun calc() {
        drawable ?: return

        val dw = drawable.intrinsicWidth.toFloat()
        val dh = drawable.intrinsicHeight.toFloat()
        val vw = measuredWidth.toFloat()
        val vh = measuredHeight.toFloat()

        imageMatrix = Matrix().apply {
            val scale = max(vw / dw, vh / dh) * scale

            preTranslate(dw * -origin.x, dh * -origin.y)
            postScale(scale, scale)
            postTranslate(vw * origin.x, vh * origin.y)
        }
    }
}
