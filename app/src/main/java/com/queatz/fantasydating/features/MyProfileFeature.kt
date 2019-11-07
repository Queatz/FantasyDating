package com.queatz.fantasydating.features

import com.queatz.fantasydating.models.Person
import com.queatz.on.On
import com.queatz.on.OnLifecycle

class MyProfileFeature constructor(private val on: On) : OnLifecycle {
    lateinit var myProfile: Person

    fun edit(function: Person.() -> Unit) {
        function.invoke(myProfile)

        on<StoreFeature>().get(Person::class).put(myProfile)
    }

    override fun on() {
        myProfile = on<StoreFeature>().get(Person::class).all.firstOrNull() ?:
                Person("", "", 0, approved = false, active = false, fantasy = "", stories = listOf())
    }
}