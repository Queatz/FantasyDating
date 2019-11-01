package com.queatz.fantasydating

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.style.LineBackgroundSpan
import androidx.core.graphics.toRectF


class BackgroundSpan(private val decent: Boolean, private val backgroundColor: Int, private val padding: Int) : LineBackgroundSpan {
    private val bgRect: Rect = Rect()

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
        p.getTextBounds(text.toString(), start, end, bgRect)

        if (bgRect.width() == 0) {
            return
        }

        bgRect.left -= padding
        bgRect.right += padding
        bgRect.top -= padding
        bgRect.bottom += padding - (if (decent) p.descent().toInt() / 2 else 0)
        bgRect.offset(left, baseline)

        val paintColor = p.color

        p.color = backgroundColor
        c.drawRoundRect(bgRect.toRectF(), padding * .5f, padding * .5f, p)
        p.color = paintColor
    }
}