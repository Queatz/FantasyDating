package com.queatz.fantasydating.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText

class FancyEditText : EditText {
    var onSelectionChangeListener: ((Int, Int) -> Unit)? = null

    constructor(context: Context) : super(context) { initialize() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initialize() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { initialize() }

    private fun initialize() {}

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        onSelectionChangeListener?.invoke(selStart, selEnd)
    }
}