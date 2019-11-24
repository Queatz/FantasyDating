package com.queatz.fantasydating

import com.queatz.on.On

class Env constructor(private val on: On) {
    val isDev: Boolean get() = true
    val isProd: Boolean get() = isDev.not()
}