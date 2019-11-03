package com.queatz.fantasydating.features

import com.queatz.fantasydating.models.MyPreferences
import com.queatz.on.On
import com.queatz.on.OnLifecycle

class MyProfileFeature constructor(private val on: On) : OnLifecycle {
    lateinit var myProfile: MyPreferences

    fun edit(function: MyPreferences.() -> Unit) {
        function.invoke(myProfile)

        on<StoreFeature>().get(MyPreferences::class).put(myProfile)
    }

    override fun on() {
        myProfile = on<StoreFeature>().get(MyPreferences::class).all.firstOrNull() ?:
                MyPreferences("", "", 0, "", listOf())
    }
}