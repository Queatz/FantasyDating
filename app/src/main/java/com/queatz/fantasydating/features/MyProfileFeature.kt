package com.queatz.fantasydating.features

import com.queatz.fantasydating.models.MyPreferences
import com.queatz.on.On

class MyProfileFeature constructor(private val on: On) {
    var myProfile = MyPreferences("Person", "", 0, "", listOf())
}