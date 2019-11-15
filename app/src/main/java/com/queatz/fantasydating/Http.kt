package com.queatz.fantasydating

import com.queatz.fantasydating.features.MeFeature
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.utils.EmptyContent
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.time.Instant

@KtorExperimentalAPI
class Http constructor(private val on: On) : OnLifecycle {

//    private val baseUrl = "http://10.0.2.2:8888/"
    private val baseUrl = "https://mage.camp/"

    private val contentHttp = HttpClient(Android) {
        install(DefaultRequest) {
            headers.append("X-CLOSER-UPLOAD", "iamsupersupersecret")
        }
    }

    private val http = HttpClient(CIO) {
        install(DefaultRequest) {
            headers.append(HttpHeaders.Authorization, on<MeFeature>().token)
            contentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))
        }

        install(JsonFeature) {
            serializer = GsonSerializer {
                registerTypeAdapter(Instant::class.java, InstantTypeConverter())
            }
        }
    }

    override fun off() {
        http.close()
    }

    fun <T : Any> get(url: String, klass: Type, error: ((Throwable) -> Unit)? = null, result: ((T) -> Unit)? = null) {
        call(url, klass, Get, result = result, error = error)
    }

    fun <T : Any> post(url: String, body: Any, klass: Type, error: ((Throwable) -> Unit)? = null, result: ((T) -> Unit)? = null) {
        call(url, klass, Post, body, result, error)
    }

    fun <T : Any> call(url: String, klass: Type, method: HttpMethod, body: Any = EmptyContent, result: ((T) -> Unit)? = null, error: ((Throwable) -> Unit)? = null) {
        val fullUrl = (if (url.contains("://")) "" else baseUrl) + url
        val httpClient = if (fullUrl.startsWith(PhotoUpload.url)) contentHttp else http

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                try {
                    Result(if (httpClient == contentHttp)
                        httpClient.post(fullUrl) { this.body = body } as Any as T
                    else
                        on<Json>().from(when (method) {
                            Post -> httpClient.post(fullUrl) { this.body = body }
                            else -> httpClient.get(fullUrl)
                        }, klass) as T, null)
                } catch (e: Exception) {
                    Result(null, e)
                }
            }.let {
                it.result?.let { result?.invoke(it) }
                it.error?.let { error?.invoke(it) }
            }
        }
    }
}

data class Result<T : Any> constructor(val result: T?, val error: Exception?)