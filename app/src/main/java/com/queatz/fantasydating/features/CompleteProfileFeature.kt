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
                val me = on<MyProfileFeature>().myProfile

                if (me.approved || me.active) {
                    completeYourProfileButton.visible = false
                    return@subscribe
                }

                val isMe = it?.id == on<MyProfileFeature>().myProfile.id
                val isProfileComplete = on<MyProfileFeature>().isComplete()

                if (isProfileComplete && me.approved.not()) {
                    completeYourProfileButton.text = getString(R.string.profile_in_review)
                    return@subscribe
                }

                completeYourProfileButton.visible = it != null && isMe.not() && isProfileComplete.not()

                if (it != null) {
                    completeYourProfileButton.text = getString(R.string.complete_your_profile, it.name)
                }
            }
        }
    }
}