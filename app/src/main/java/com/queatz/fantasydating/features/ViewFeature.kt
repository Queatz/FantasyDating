package com.queatz.fantasydating.features

import com.queatz.fantasydating.MainActivity
import com.queatz.on.On

class ViewFeature constructor(private val on: On) {

    lateinit var activity: MainActivity

    fun <T> with(function: MainActivity.() -> T): T = function.invoke(activity)
}