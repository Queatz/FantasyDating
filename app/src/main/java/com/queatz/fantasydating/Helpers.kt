package com.queatz.fantasydating

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import io.objectbox.converter.PropertyConverter
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class InstantTypeConverter : JsonSerializer<Instant>, JsonDeserializer<Instant> {

    companion object {
        private val DateFormat = object : ThreadLocal<DateFormat>() {
            override fun initialValue(): DateFormat {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                return formatter
            }
        }
    }

    override fun serialize(
        src: Instant,
        srcType: Type,
        context: JsonSerializationContext
    ) = JsonPrimitive(DateFormat.get()!!.format(Date.from(src)))

    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ) = try {
        DateFormat.get()!!.parse(json.asString)!!.toInstant()
    } catch (e: ParseException) {
        null
    }
}

class StringListJsonConverter : PropertyConverter<List<String>, String> {

    override fun convertToEntityProperty(databaseValue: String?): List<String>? {
        return if (databaseValue == null) {
            null
        } else gson.fromJson<List<String>>(databaseValue, object : TypeToken<List<String>>() {

        }.type)

    }

    override fun convertToDatabaseValue(entityProperty: List<String>): String {
        return gson.toJson(entityProperty)
    }

    companion object {
        private val gson = Gson()
    }
}
