package com.queatz.fantasydating.models

data class DiscoveryPreferences constructor(
    var who: String,
    var where: String,
    var ageMin: Int,
    var ageMax: Int
)

data class MyPreferences constructor(
    var sex: String,
    var name: String,
    var age: Int,
    var fantasy: String,
    var stories: List<String>
)
