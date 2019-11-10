package com.queatz.fantasydating.features

import com.queatz.fantasydating.Api
import com.queatz.fantasydating.MeRequest
import com.queatz.fantasydating.Person
import com.queatz.fantasydating.PersonStory
import com.queatz.on.On
import com.queatz.on.OnLifecycle

class MyProfileFeature constructor(private val on: On) : OnLifecycle {
    lateinit var myProfile: Person

    fun edit(function: Person.() -> Unit) {
        function.invoke(myProfile)

        on<Api>().me(MeRequest(
            sex = myProfile.sex,
            name = myProfile.name,
            age = myProfile.age,
            active = myProfile.active,
            fantasy = myProfile.fantasy,
            stories = myProfile.stories
        )) {}

        on<StoreFeature>().get(Person::class).put(myProfile)
    }

    override fun on() {
        myProfile = on<StoreFeature>().get(Person::class).all.firstOrNull() ?: Person(
            "",
            "",
            0,
            approved = false,
            active = false,
            fantasy = "",
            stories = listOf(PersonStory(), PersonStory(), PersonStory())
        )
    }
}