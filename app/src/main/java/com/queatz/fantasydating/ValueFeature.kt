package com.queatz.fantasydating

import com.queatz.on.On

class ValueFeature constructor(private val on: On) {
    fun referToAs(sex: String, objectiveForm: Boolean = false) = when (sex) {
        "Boy" -> if (objectiveForm) "him" else "his"
        "Girl" -> "her"
        else -> if (objectiveForm) "them" else "their"
    }

    fun pronoun(sex: String) = when (sex) {
        "Boy" -> "he"
        "Girl" -> "she"
        else -> "they"
    }

    fun pluralSex(who: String) = when (who) {
        "Girl" -> "Females"
        "Boy" -> "Males"
        else -> "People"
    }
}