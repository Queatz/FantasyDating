package com.queatz.fantasydating

data class MeRequest constructor(
    val name: String? = null,
    var sex: String? = null,
    var age: Int? = null,
    var active: Boolean? = null,
    var fantasy: String? = null,
    var stories: List<String>? = null
)

data class MeDiscoveryPreferencesRequest constructor(
    var who: String? = null,
    var where: String? = null,
    var ageMin: Int? = null,
    var ageMax: Int? = null
)
