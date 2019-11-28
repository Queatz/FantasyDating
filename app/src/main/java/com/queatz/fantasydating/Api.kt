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

    fun events(callback: (List<Event>) -> Unit) {
        on<Http>().get("me/events", type<List<Event>>(), this::error, callback)
    }

    fun deleteMe(callback: (SuccessResponse) -> Unit) {
        on<Http>().post("me/delete", Any(), type<SuccessResponse>(), this::error, callback)
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

    fun person(person: String, callback: (Person) -> Unit) {
        on<Http>().get("person/${person}", type<Person>(), this::error, callback)
    }

    fun person(person: String, request: PersonRequest, callback: (SuccessResponse) -> Unit) {
        on<Http>().post("person/${person}", request, type<SuccessResponse>(), this::error, callback)
    }

    fun bossInfo(callback: (BossInfo) -> Unit) {
        on<Http>().get("boss/info", type<BossInfo>(), this::error, callback)
    }


    fun bossReports(callback: (List<Report>) -> Unit) {
        on<Http>().get("boss/reports", type<List<Report>>(), this::error, callback)
    }

    fun bossApprovals(callback: (List<Person>) -> Unit) {
        on<Http>().get("boss/approvals", type<List<Person>>(), this::error, callback)
    }

    fun boss(request: WhoIsTheBossRequest, callback: (SuccessResponse) -> Unit) {
        on<Http>().post("boss/me", request, type<SuccessResponse>(), this::error, callback)
    }

    fun bossApprove(request: BossApproveRequest, callback: (SuccessResponse) -> Unit) {
        on<Http>().post("boss/approve", request, type<SuccessResponse>(), this::error, callback)
    }

    private fun error(throwable: Throwable) {
        on<Say>().say(R.string.something_went_wrong)
        throwable.printStackTrace()
    }

    private inline fun <reified T> type() = object : TypeToken<T>() {}.type
}
