package com.queatz.fantasydating

import java.time.Instant

data class SuccessResponse constructor(
    var success: Boolean = false
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