package com.queatz.fantasydating

import com.queatz.on.On
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import io.ktor.util.flattenEntries
import io.ktor.utils.io.*
import java.io.InputStream
import java.util.*

class PhotoUpload constructor(private val on: On) {
    companion object {
        const val url = "http://closer-files.vlllage.com/"
    }

    fun uploadPhoto(photo: InputStream, error: ((Throwable) -> Unit)? = null, success: ((String) -> Unit)? = null) {
        val fullUrl = "$url${on<Rnd>().rnd()}"

        on<Http>().post<Any>(fullUrl, MultiPartContent.build {
            add("photo", photo.readBytes(), filename = "fantasy.jpg")
        }, Any::class.java, null, error, {
            success?.invoke(fullUrl)
        })
    }
}

class MultiPartContent(val parts: List<Part>) : OutgoingContent.WriteChannelContent() {
    private val uuid: UUID = UUID.randomUUID()
    private val boundary = "***ktor-$uuid-ktor-${System.currentTimeMillis()}***"

    data class Part(val name: String, val filename: String? = null, val headers: Headers = Headers.Empty, val writer: suspend ByteWriteChannel.() -> Unit)

    override suspend fun writeTo(channel: ByteWriteChannel) {
        for (part in parts) {
            channel.writeStringUtf8("--$boundary\r\n")
            val partHeaders = Headers.build {
                val fileNamePart = if (part.filename != null) "; filename=\"${part.filename}\"" else ""
                append("Content-Disposition", "form-data; name=\"${part.name}\"$fileNamePart")
                appendAll(part.headers)
            }
            for ((key, value) in partHeaders.flattenEntries()) {
                channel.writeStringUtf8("$key: $value\r\n")
            }
            channel.writeStringUtf8("\r\n")
            part.writer(channel)
            channel.writeStringUtf8("\r\n")
        }
        channel.writeStringUtf8("--$boundary--\r\n")
    }

    override val contentType = ContentType.MultiPart.FormData
        .withParameter("boundary", boundary)
        .withCharset(Charsets.UTF_8)

    class Builder {
        val parts = arrayListOf<Part>()

        fun add(part: Part) {
            parts += part
        }

        fun add(name: String, filename: String? = null, contentType: ContentType? = null, headers: Headers = Headers.Empty, writer: suspend ByteWriteChannel.() -> Unit) {
            val contentTypeHeaders: Headers = if (contentType != null) headersOf(HttpHeaders.ContentType, contentType.toString()) else headersOf()
            add(Part(name, filename, headers + contentTypeHeaders, writer))
        }

        fun add(name: String, text: String, contentType: ContentType? = null, filename: String? = null) {
            add(name, filename, contentType) { writeStringUtf8(text) }
        }

        fun add(name: String, data: ByteArray, contentType: ContentType? = ContentType.Application.OctetStream, filename: String? = null) {
            add(name, filename, contentType) { writeFully(data) }
        }

        internal fun build(): MultiPartContent = MultiPartContent(parts.toList())
    }

    companion object {
        fun build(callback: Builder.() -> Unit) = Builder().apply(callback).build()
    }
}

operator fun Headers.plus(other: Headers): Headers = when {
    this.isEmpty() -> other
    other.isEmpty() -> this
    else -> Headers.build {
        appendAll(this@plus)
        appendAll(other)
    }
}