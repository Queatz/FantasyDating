package com.queatz.fantasydating

data class MeRequest constructor(
    val name: String? = null,
    var sex: String? = null,
    var age: Int? = null,
    var active: Boolean? = null,
    var fantasy: String? = null,
    var stories: List<PersonStory>? = null,
    var stylesOrder: List<String>? = null
)

data class MeDiscoveryPreferencesRequest constructor(
    var who: String? = null,
    var where: String? = null,
    var ageMin: Int? = null,
    var ageMax: Int? = null
)

data class MessageRequest constructor(
    var message: String? = null,
    var attachment: String? = null
)

data class PersonRequest constructor(
    var love: Boolean? = null,
    var report: Boolean? = null,
    var hide: Boolean? = null,
    var message: String? = null
)

data class BossApproveRequest constructor(
    var person: String? = null,
    var approve: Boolean? = null,
    var message: String? = null
)

data class BossReportRequest constructor(
    var report: String? = null,
    var resolve: Boolean = true
)

data class BossRemoveProfileRequest constructor(
    var person: String? = null
)

data class WhoIsTheBossRequest constructor(
    var whoIsTheBoss: String? = null
)

data class PhoneRequest constructor(
    var token: String? = null
)

data class StyleRequest constructor(
    var name: String? = null,
    var about: String? = null
)

data class MeStyleRequest constructor(
    var link: String? = null,
    var unlink: String? = null,
    var promote: String? = null,
    var demote: String? = null,
    var dismiss: String? = null,
    var undismiss: String? = null
)
