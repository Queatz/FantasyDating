package com.queatz.fantasydating.features

import com.queatz.on.On

class TopFeature constructor(private val on: On) {
    companion object {
        private var onCaught: () -> Unit = { }
        private var topPerson: String = ""
    }

    var onCaught
        get() = TopFeature.onCaught
        set(value) {
            TopFeature.onCaught = value
        }

    var topPerson
        get() = TopFeature.topPerson
        set(value) {
            TopFeature.topPerson = value
        }

    fun caught() {
        onCaught.invoke()
    }
}