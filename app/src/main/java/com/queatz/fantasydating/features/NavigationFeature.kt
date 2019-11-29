package com.queatz.fantasydating.features

import android.content.Intent
import com.queatz.fantasydating.MainActivity
import com.queatz.fantasydating.MessagesActivity
import com.queatz.on.On

class NavigationFeature constructor(private val on: On) {

    companion object {
        const val ExtraPersonId = "person"
    }

    fun showPerson(person: String) {
        on<ViewFeature>().activity.startActivity(
            Intent(on<ViewFeature>().activity, MainActivity::class.java).apply {
                putExtra(ExtraPersonId, person)
            }
        )
    }

    fun showMessages(person: String) {
        on<ViewFeature>().activity.startActivity(
            Intent(on<ViewFeature>().activity, MessagesActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra(ExtraPersonId, person)
            }
        )
    }
}