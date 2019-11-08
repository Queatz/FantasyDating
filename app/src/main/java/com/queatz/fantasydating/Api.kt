package com.queatz.fantasydating

import com.google.gson.reflect.TypeToken
import com.queatz.on.On

class Api constructor(private val on: On) {
    fun me(callback: (Person) -> Unit) {
        on<Http>().get("me", type<Person>(), this::error, callback)
    }

    fun me(request: MeRequest, callback: (Person) -> Unit) {
        on<Http>().post("me", request, type<Person>(), this::error, callback)
    }

    fun discoveryPreferences(callback: (DiscoveryPreferences) -> Unit) {
        on<Http>().get("me/discovery-preferences", type<DiscoveryPreferences>(), this::error, callback)
    }

    fun discoveryPreferences(request: MeDiscoveryPreferencesRequest, callback: (DiscoveryPreferences) -> Unit) {
        on<Http>().post("me/discovery-preferences", request, type<DiscoveryPreferences>(), this::error, callback)
    }

    fun people(callback: (List<Person>) -> Unit) {
        on<Http>().get("me/people", type<List<Person>>(), this::error, callback)
    }

    fun messages(person: String, callback: (List<Message>) -> Unit) {
        on<Http>().get("person/${person}/messages", type<List<Message>>(), this::error, callback)
    }

    fun sendMessage(person: String, request: MessageRequest, callback: (SuccessResponse) -> Unit) {
        on<Http>().post("person/${person}/messages", request, type<SuccessResponse>(), this::error, callback)
    }

    private fun error(throwable: Throwable) = throwable.printStackTrace()

    private inline fun <reified T> type() = object : TypeToken<T>() {}.type
}
