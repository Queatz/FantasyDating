package com.queatz.fantasydating.features

import coil.Coil
import coil.api.load
import com.queatz.fantasydating.*
import com.queatz.on.On
import io.reactivex.subjects.BehaviorSubject

class PeopleFeature constructor(private val on: On) {

    var current: BehaviorSubject<Person?> = BehaviorSubject.create()

    private var index = -1
    private var people = mutableListOf<Person>()

    fun start() {
        on<Api>().people {
            people = it.toMutableList()
            nextPerson()
        }
    }

    fun showMe() {
        current.onNext(on<MyProfileFeature>().myProfile)
    }

    fun nextPerson() {
        index++

        if (index < 0) {
            index = 0
        }

        if (index >= people.size) {
            index = people.size - 1
        }

        if (index >= 0) {
            show(people[index])
        }
    }

    fun previousPerson() {
        index -= 2
        nextPerson()
    }

    fun reset() {
        index = -1
        nextPerson()
    }

    fun hide(person: String) {
        on<Api>().person(person, PersonRequest(hide = true)) {}
        people.removeIf { it.id == person }

        index--
        nextPerson()
    }

    fun report(person: Person, message: String) {
        hide(person.id!!)

        on<Api>().person(person.id!!, PersonRequest(report = true, message = message)) {
            if (it.success) {
                on<Say>().say("Thank you for reporting ${person.name}")
            } else {
                on<Say>().say(R.string.something_went_wrong)
            }
        }
    }

    private fun show(person: Person) {
        current.onNext(person)

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
