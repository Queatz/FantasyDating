package com.queatz.fantasydating

import com.queatz.on.On

class ValueFeature constructor(private val on: On) {
    fun referToAs(sex: String) = when (sex) {
        "Boy" -> "him"
        "Girl" -> "her"
        else -> "them"
    }
}