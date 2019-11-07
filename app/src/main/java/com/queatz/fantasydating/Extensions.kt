package com.queatz.fantasydating

import android.view.View

var View.visible: Boolean
    inline get() = visibility == View.VISIBLE
    inline set(value) { visibility = if (value) View.VISIBLE else View.GONE }

inline infix fun Boolean.then(function: () -> Unit): Boolean {
    if (this) {
        function.invoke()
    }

    return this
}

inline infix fun Boolean.otherwise(function: () -> Unit) = this.not().then(function)