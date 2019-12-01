package com.queatz.fantasydating.features

import com.queatz.fantasydating.Phone
import com.queatz.fantasydating.Token
import com.queatz.on.On
import kotlin.math.abs
import kotlin.random.Random.Default.nextLong

class MeFeature constructor(private val on: On) {
    var phoneToken: String
        get() = phone.token
        set(value) {
            val phone = this.phone
            phone.token = value
            phone.synced = false
            on<StoreFeature>().get(Phone::class).put(phone)
        }

    var phoneSynced: Boolean
        get() = phone.synced
        set(value) {
            val phone = this.phone
            phone.synced = value
            on<StoreFeature>().get(Phone::class).put(phone)
        }

    val token: String
        get() = on<StoreFeature>().get(Token::class).all.firstOrNull()?.token ?: let {
            Token().apply {
                token = rndId()
                on<StoreFeature>().get(Token::class).put(this)
            }.token
        }

    private val phone get() = on<StoreFeature>().get(Phone::class).all.firstOrNull() ?: let {
        Phone().apply {
            token = ""
            on<StoreFeature>().get(Phone::class).put(this)
        }
    }

    private fun rndId() = (1..3).joinToString("") { abs(nextLong()).toString(36) }
}