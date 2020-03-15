package com.queatz.fantasydating

import com.google.gson.reflect.TypeToken
import com.queatz.on.On
import io.ktor.client.utils.EmptyContent

class Api constructor(private val on: On) {
    fun me(callback: (Person) -> Unit) =
        get("me", callback)

    fun me(request: MeRequest, callback: (Person) -> Unit) =
        post("me", request, callback)

    fun events(callback: (List<Event>) -> Unit) =
        get("me/events", callback)

    fun deleteMe(callback: (SuccessResponse) -> Unit) =
        post("me/delete", callback =
        callback)

    fun discoveryPreferences(callback: (DiscoveryPreferences) -> Unit) =
        get("me/discovery-preferences", callback)

    fun discoveryPreferences(request: MeDiscoveryPreferencesRequest, callback: (DiscoveryPreferences) -> Unit) =
        post("me/discovery-preferences", request, callback)

    fun people(callback: (List<Person>) -> Unit) =
        get("me/people", callback)

    fun messages(person: String, callback: (List<Message>) -> Unit) =
        get("person/${person}/messages", callback)

    fun sendMessage(person: String, request: MessageRequest, callback: (SuccessResponse) -> Unit) =
        post("person/${person}/messages", request, callback)

    fun person(person: String, callback: (Person) -> Unit) =
        get("person/${person}", callback)

    fun person(person: String, request: PersonRequest, callback: (SuccessResponse) -> Unit) =
        post("person/${person}", request, callback)

    fun bossInfo(callback: (BossInfo) -> Unit) =
        get("boss/info", callback)

    fun bossReports(callback: (List<Report>) -> Unit) =
        get("boss/reports", callback)

    fun bossApprovals(callback: (List<Person>) -> Unit) =
        get("boss/approvals", callback)

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

    private inline fun <reified T : Any> get(url: String, noinline callback: (T) -> Unit) = CallbackHandle { error(it) }.apply {
        on<Http>().get(url, type<T>(), { errorCallback(it) }, callback)
    }

    private inline fun <reified T : Any> post(url: String, body: Any = EmptyContent, noinline callback: (T) -> Unit) = CallbackHandle { error(it) }.apply {
        on<Http>().post(url, body, type<T>(), { errorCallback(it) }, callback)
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
