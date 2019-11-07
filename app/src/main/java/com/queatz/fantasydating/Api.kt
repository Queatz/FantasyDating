package com.queatz.fantasydating

import com.queatz.fantasydating.models.Person
import com.queatz.on.On

class Api constructor(private val on: On) {
    fun me(callback: (Person) -> Unit) {
        on<Http>().get("me", Person::class, {}, callback)
    }

    fun me(request: MeRequest, callback: (Person) -> Unit) {
        on<Http>().post("me", request, Person::class, {}, callback)
    }

}
