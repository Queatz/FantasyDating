package com.queatz.fantasydating

import com.google.gson.GsonBuilder
import com.queatz.on.On
import java.lang.reflect.Type
import java.time.Instant
import kotlin.reflect.KClass

class Json constructor(private val on: On) {

    private val gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantTypeConverter())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create()

    fun to(any: Any): String = gson.toJson(any)

    fun <T : Any> from(string: String, klass: Type): T = gson.fromJson(string, klass)

    fun <T : Any> from(string: String, klass: KClass<T>): T = gson.fromJson<T>(string, klass.java)
}