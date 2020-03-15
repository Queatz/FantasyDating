package com.queatz.fantasydating

import java.time.Instant

data class SuccessResponse constructor(
    var success: Boolean = false,
    var message: String? = null
)

data class BossInfo constructor(
    val approvals: Int,
    val reports: Int
)

data class Report constructor(
    var id: String? = null,
    var created: Instant? = null,
    var person: String = "",
    var reporter: String = "",
    var report: String = ""
)

data class InviteCode(
    var code: String? = null,
    var used: Boolean = false,
    var inviter: Person? = null
)
