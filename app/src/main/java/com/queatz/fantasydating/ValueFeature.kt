package com.queatz.fantasydating

import com.queatz.on.On

class ValueFeature constructor(private val on: On) {
    fun referToAs(sex: String, objectiveForm: Boolean = false) = when (sex) {
        "Boy" -> if (objectiveForm) "him" else "his"
        "Girl" -> "her"
        else -> "them"
    }

    fun pluralSex(who: String) = when (who) {
        "Girl" -> "Girls"
        "Boy" -> "Boys"
        else -> "People"
    }
}