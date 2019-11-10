package com.queatz.fantasydating.features

import coil.Coil
import coil.api.load
import com.queatz.fantasydating.Person
import com.queatz.on.On
import io.reactivex.subjects.BehaviorSubject

class PeopleFeature constructor(private val on: On) {

    var current: BehaviorSubject<Person?> = BehaviorSubject.create()

    private var people = listOf<Person>()

    fun start() {
        show(on<MyProfileFeature>().myProfile)
    }

    fun show(person: Person) {
        current.onNext(on<MyProfileFeature>().myProfile)

        person.stories.forEach {
            preload(it.photo)
        }
    }

    private fun preload(url: String) {
        on<ViewFeature>().with {

            Coil.load(this, url)
        }
    }
}