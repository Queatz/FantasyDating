package com.queatz.fantasydating

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.text.SpannableString
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView


class FancyTextView : TextView {

    private var bold: Boolean = false
    private var textChangeLock: Boolean = false
    private var theme: Resources.Theme? = null

    constructor(context: Context) : super(context) { initialize(
        context
    ) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize(
        context,
        attrs
    ) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initialize(
        context,
        attrs,
        defStyleAttr
    ) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { initialize(context, attrs, defStyleAttr, defStyleRes) }

    private fun initialize(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) {
        bold = true
        var lineSpacing = 1.4f
        theme = context.theme
        val colors = textColors
        setTextAppearance(R.style.Text_Medium)
        setTextColor(colors)

        attrs?.let {
            val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.FancyTextView, defStyleAttr, defStyleRes)
            val textSize = styledAttrs.getDimension(R.styleable.FancyTextView_android_textSize, 0f)
            bold = !styledAttrs.getBoolean(R.styleable.FancyTextView_thin, false)
            lineSpacing = styledAttrs.getFloat(R.styleable.FancyTextView_android_lineSpacingMultiplier, lineSpacing)
            styledAttrs.recycle()

            if (textSize > 0) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
        }

        if (bold) {
            paintFlags = paintFlags or Paint.FAKE_BOLD_TEXT_FLAG
        } else {
            lineSpacing = 1.1f
        }

        setLineSpacing(0f, lineSpacing)

        val pad = resources.getDimensionPixelSize(R.dimen.pad)
        setShadowLayer(pad.toFloat(), 0f, 0f, 0)
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (textChangeLock) {
            return
        }

        val pad = resources.getDimensionPixelSize(R.dimen.pad)

        textChangeLock = true

        super.setText(SpannableString(text).apply {
            setSpan(BackgroundSpan(bold, resources.getColor(R.color.white, theme), pad / 2), 0, text.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        })

        textChangeLock = false
    }
}