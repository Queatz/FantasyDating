package com.queatz.fantasydating

data class DiscoveryPreferences constructor(
    var who: String,
    var where: String,
    var ageMin: Int,
    var ageMax: Int
)