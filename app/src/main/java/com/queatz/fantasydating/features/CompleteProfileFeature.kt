package com.queatz.fantasydating.features

import com.queatz.fantasydating.R
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class CompleteProfileFeature constructor(private val on: On) {
    fun start() {
        on<ViewFeature>().with {
            completeYourProfileButton.onLinkClick = {
                on<EditProfileFeature>().editProfile()
            }

            on<PeopleFeature>().current.subscribe {
                update()
            }
        }
    }

    fun update() {
        on<ViewFeature>().with {
            val person = on<PeopleFeature>().current.value
            val me = on<MyProfileFeature>().myProfile

            if (me.approved || me.active || discoveryPreferencesLayout.visible) {
                completeYourProfileButton.visible = false
                return@with
            }

            val isMe = person?.id == on<MyProfileFeature>().myProfile.id
            val isProfileComplete = on<MyProfileFeature>().isComplete()

            if (isProfileComplete && me.approved.not()) {
                completeYourProfileButton.text = getString(R.string.profile_in_review)
                return@with
            }

            completeYourProfileButton.visible = person != null && isMe.not() && isProfileComplete.not()

            if (person != null) {
                completeYourProfileButton.text = getString(R.string.complete_your_profile, person.name)
            }
        }
    }
}