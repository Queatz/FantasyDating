package com.queatz.fantasydating.features

import com.queatz.fantasydating.models.Token
import com.queatz.on.On
import kotlin.math.abs
import kotlin.random.Random.Default.nextLong

class MeFeature constructor(private val on: On) {
    val token: String
        get() = on<StoreFeature>().get(Token::class).all.firstOrNull()?.token ?: let {
            Token().apply {
                token = rndId()
                on<StoreFeature>().get(Token::class).put(this)
            }.token
        }

    private fun rndId() = (1..3).joinToString("") { abs(nextLong()).toString(36) }
}