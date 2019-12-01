package com.queatz.fantasydating.features

import com.queatz.fantasydating.Api
import com.queatz.fantasydating.PhoneRequest
import com.queatz.on.On

class NotificationFeature constructor(private val on: On) {
    fun start() {
        val token = on<MeFeature>().phoneToken

        if (token.isNotBlank() && on<MeFeature>().phoneSynced.not()) {
            sync(token)
        }
    }

    fun set(token: String) {
        on<MeFeature>().phoneToken = token

        sync(token)
    }

    private fun sync(token: String) {
        on<Api>().phone(PhoneRequest(token)) {
            on<MeFeature>().phoneSynced = true
        }
    }
}
