package com.queatz.fantasydating

import android.view.View

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) { visibility = if (value) View.VISIBLE else View.GONE }

infix fun Boolean.then(function: () -> Unit): Boolean {
    if (this) {
        function.invoke()
    }

    return this
}

infix fun Boolean.otherwise(function: () -> Unit) = this.not().then(function)