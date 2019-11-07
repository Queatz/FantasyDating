package com.queatz.fantasydating

import com.google.gson.GsonBuilder
import com.queatz.on.On
import java.time.Instant
import kotlin.reflect.KClass

class Json constructor(private val on: On) {

    private val gson = GsonBuilder().registerTypeAdapter(Instant::class.java, InstantTypeConverter()).create()

    fun <T : Any> from(string: String, klass: KClass<T>): T = gson.fromJson(string, klass.java)
}