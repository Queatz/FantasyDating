package com.queatz.fantasydating.features

import com.queatz.fantasydating.*
import com.queatz.on.On
import com.queatz.on.OnLifecycle

class MyProfileFeature constructor(private val on: On) : OnLifecycle {
    lateinit var myProfile: Person

    fun reload() {
        on<Api>().me {
            myProfile.active = it.active
            myProfile.approved = it.approved
            myProfile.boss = it.boss
            on<StoreFeature>().get(Person::class).put(myProfile)
            on<State>().profile = ProfileState(myProfile)
            on<LayoutFeature>().isBoss = myProfile.boss
        }
    }

    fun edit(function: Person.() -> Unit) {
        function.invoke(myProfile)

        on<StoreFeature>().get(Person::class).put(myProfile)

        on<Api>().me(MeRequest(
            sex = myProfile.sex,
            name = myProfile.name,
            age = myProfile.age,
            active = myProfile.active,
            fantasy = myProfile.fantasy,
            stories = myProfile.stories
        )) {}
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

        reload()
    }

    fun isComplete() = myProfile.let {
        it.stories.isNotEmpty() &&
                it.stories.all { story ->
                    story.photo.isNotBlank() && story.story.isNotBlank()
                } &&
                it.fantasy.isNotBlank() &&
                it.name.isNotBlank() &&
                it.sex.isNotBlank() &&
                it.age > 0
    }
}