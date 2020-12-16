package com.queatz.fantasydating

import android.util.Log
import com.queatz.fantasydating.features.MeFeature
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.*
import io.ktor.client.response.readText
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
import java.util.concurrent.CancellationException

@KtorExperimentalAPI
class Http constructor(private val on: On) : OnLifecycle {

    private val baseUrl = when (on<Env>().isDev) {
        true -> "http://10.0.2.2:8888/"
        false -> "https://mage.camp/"
    }

    private val contentHttp = HttpClient(CIO) {
        install(DefaultRequest) {
            headers.append("X-CLOSER-UPLOAD", "iamsupersupersecret")
        }
    }

    private val http = HttpClient(CIO) {
        install(DefaultRequest) {
            headers.append(HttpHeaders.Authorization, on<MeFeature>().token)
            contentType(ContentType.Application.Json.withCharset(Charset.forName("UTF-8")))

            Log.d("MAGIC", url.buildString() + " -> " + on<Json>().to(body))
        }

        install(ResponseObserver) {
            onResponse {
                Log.d("MAGIC", it.call.request.url.toString() + " <- " + it.call.response.status + " " + try { it.call.response.readText() } catch (throwable: Throwable) { "" })
            }
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

    fun <T : Any> get(url: String, klass: Type, queryParams: Map<String, String>? = null, error: ((Throwable) -> Unit)? = null, result: ((T) -> Unit)? = null, cancel: ((((CancellationException) -> Unit)) -> Unit)? = null) {
        call(url, klass, Get, queryParams = queryParams, result = result, error = error, cancel = cancel)
    }

    fun <T : Any> post(url: String, body: Any, klass: Type, queryParams: Map<String, String>? = null, error: ((Throwable) -> Unit)? = null, result: ((T) -> Unit)? = null, cancel: ((((CancellationException) -> Unit)) -> Unit)? = null) {
        call(url, klass, Post, queryParams, body, result, error, cancel)
    }

    private fun <T : Any> call(url: String, klass: Type, method: HttpMethod, queryParams: Map<String, String>? = null, body: Any = EmptyContent, result: ((T) -> Unit)? = null, error: ((Throwable) -> Unit)? = null, cancel: ((((CancellationException) -> Unit)) -> Unit)? = null) {
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
                            else -> httpClient.get(fullUrl) {
                                queryParams?.forEach { k, v -> parameter(k, v) }
                            }
                        }, klass) as T, null)
                } catch (e: Exception) {
                    Result(null, e)
                }
            }.let {
                it.result?.let { result?.invoke(it) }
                it.error?.let { error?.invoke(it) }
            }
        }.also { job ->
            cancel?.invoke { job.cancel(it) }
        }
    }
}

data class Result<T : Any> constructor(val result: T?, val error: Exception?)