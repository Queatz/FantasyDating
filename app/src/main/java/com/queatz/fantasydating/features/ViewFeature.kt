package com.queatz.fantasydating.features

import com.queatz.fantasydating.BaseActivity
import com.queatz.on.On

class ViewFeature constructor(private val on: On) {

    lateinit var activity: BaseActivity

    fun <T> with(function: BaseActivity.() -> T): T = function.invoke(activity)
}