package com.queatz.fantasydating

import com.queatz.on.On
import kotlin.math.abs
import kotlin.random.Random


class Rnd constructor(private val on: On) {
    fun rnd() = (1..3).joinToString("") { abs(Random.nextLong()).toString(36) }
}