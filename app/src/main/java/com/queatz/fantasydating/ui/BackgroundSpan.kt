package com.queatz.fantasydating.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.LineBackgroundSpan


class BackgroundSpan(private val backgroundColor: Int, private val padding: Float, private val lineWidth: (Int, Int) -> Unit) : LineBackgroundSpan {
    private val bgRect = RectF()

    override fun drawBackground(
        c: Canvas,
        p: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lnum: Int
    ) {
        val t = text.toString().subSequence(start, end).trimEnd()

        if (t.isBlank()) {
            return
        }

        val w = p.measureText(t.toString())

        lineWidth.invoke(lnum, w.toInt())

        bgRect.left = left - padding
        bgRect.right = left + w + padding
        bgRect.top = baseline + p.ascent() - padding * .25f
        bgRect.bottom = baseline + padding

        val paintColor = p.color

        p.color = backgroundColor
        c.drawRoundRect(bgRect, padding * .5f, padding * .5f, p)
        p.color = paintColor
    }
}