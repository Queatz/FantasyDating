package com.queatz.fantasydating

import android.widget.Toast
import androidx.annotation.StringRes
import com.queatz.on.On

class Say constructor(private val on: On) {
    fun say(text: String) {
        Toast.makeText(on<ContextFeature>().context, text, Toast.LENGTH_SHORT).show()
    }

    fun say(@StringRes text: Int) = say(on<ContextFeature>().context.getString(text))
}