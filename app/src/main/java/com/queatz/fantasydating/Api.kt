package com.queatz.fantasydating

import com.google.gson.reflect.TypeToken
import com.queatz.on.On
import io.ktor.client.utils.EmptyContent

class Api constructor(private val on: On) {
    fun me(callback: (Person) -> Unit) =
        get("me", callback = callback)

    fun me(request: MeRequest, callback: (Person) -> Unit) =
        post("me", request, callback)

    fun events(callback: (List<Event>) -> Unit) =
        get("me/events", callback = callback)

    fun deleteMe(callback: (SuccessResponse) -> Unit) =
        post("me/delete", callback =
        callback)

    fun discoveryPreferences(callback: (DiscoveryPreferences) -> Unit) =
        get("me/discovery-preferences", callback = callback)

    fun discoveryPreferences(request: MeDiscoveryPreferencesRequest, callback: (DiscoveryPreferences) -> Unit) =
        post("me/discovery-preferences", request, callback)

    fun people(callback: (List<Person>) -> Unit) =
        get("me/people", callback = callback)

    fun linkStyle(request: MeStyleRequest, callback: (SuccessResponse) -> Unit) =
        post("me/style", request, callback)

    fun createStyle(request: StyleRequest, callback: (SuccessResponse) -> Unit) =
        post("style", request, callback)

    fun getStyles(callback: (SuccessResponse) -> Unit) =
        get("style", callback = callback)

    fun searchStyles(query: String, callback: (SuccessResponse) -> Unit) =
        get("style", mapOf("search" to query), callback)

    fun messages(person: String, callback: (List<Message>) -> Unit) =
        get("person/${person}/messages", callback = callback)

    fun sendMessage(person: String, request: MessageRequest, callback: (SuccessResponse) -> Unit) =
        post("person/${person}/messages", request, callback)

    fun person(person: String, callback: (Person) -> Unit) =
        get("person/${person}", callback = callback)

    fun person(person: String, request: PersonRequest, callback: (SuccessResponse) -> Unit) =
        post("person/${person}", request, callback)

    fun bossInfo(callback: (BossInfo) -> Unit) =
        get("boss/info", callback = callback)

    fun bossReports(callback: (List<Report>) -> Unit) =
        get("boss/reports", callback = callback)

    fun bossApprovals(callback: (List<Person>) -> Unit) =
        get("boss/approvals", callback = callback)

    fun boss(request: WhoIsTheBossRequest, callback: (SuccessResponse) -> Unit) =
        post("boss/me", request, callback)

    fun bossApprove(request: BossApproveRequest, callback: (SuccessResponse) -> Unit) =
        post("boss/approve", request, callback)

    fun bossResolveReport(request: BossReportRequest, callback: (SuccessResponse) -> Unit) =
        post("boss/report", request, callback)

    fun bossRemoveProfile(request: BossRemoveProfileRequest, callback: (SuccessResponse) -> Unit) =
        post("boss/removeProfile", request, callback)

    fun createInviteCode(callback: (InviteCode) -> Unit) =
        post("invite", callback = callback)

    fun useInviteCode(code: String, callback: (SuccessResponse) -> Unit) =
        post("invite/${code}", callback = callback)

    fun phone(request: PhoneRequest, callback: (SuccessResponse) -> Unit) =
        post("phone", request, callback)

    private inline fun <reified T : Any> get(url: String, queryParams: Map<String, String>? = null, noinline callback: (T) -> Unit) = CallbackHandle { error(it) }.apply {
        on<Http>().get(url, type<T>(), queryParams, { errorCallback(it) }, callback)
    }

    private inline fun <reified T : Any> post(url: String, body: Any = EmptyContent, noinline callback: (T) -> Unit) = CallbackHandle { error(it) }.apply {
        on<Http>().post(url, body, type<T>(), null, { errorCallback(it) }, callback)
    }

    private fun error(throwable: Throwable) {
        on<Say>().say(R.string.something_went_wrong)
        throwable.printStackTrace()
    }

    private inline fun <reified T> type() = object : TypeToken<T>() {}.type
}

typealias ErrorCallback = (throwable: Throwable) -> Unit

class CallbackHandle constructor(var errorCallback: ErrorCallback) {
    infix fun error(errorCallback: ErrorCallback): CallbackHandle {
        this.errorCallback = errorCallback
        return this
    }
}
