package com.queatz.fantasydating

import android.widget.Toast
import androidx.annotation.StringRes
import com.queatz.fantasydating.features.ViewFeature
import com.queatz.on.On

class Say constructor(private val on: On) {
    fun say(text: String) {
        on<ViewFeature>().with {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun say(@StringRes text: Int) = say(on<ViewFeature>().activity.getString(text))
}