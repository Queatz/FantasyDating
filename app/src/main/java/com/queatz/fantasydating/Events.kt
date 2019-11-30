package com.queatz.fantasydating

open class EventType constructor(
    val type: String
)

data class ProfileLiveEventType constructor(
    val live: Boolean,
    val message: String
) : EventType("live")

data class LoveEventType constructor(
    val person: String
) : EventType("love")