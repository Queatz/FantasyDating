package com.queatz.fantasydating.features

import com.queatz.fantasydating.R
import com.queatz.fantasydating.State
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class CompleteProfileFeature constructor(private val on: On) {
    fun start() {
        on<ViewFeature>().with {
            completeYourProfileButton.onLinkClick = {
                on<EditProfileFeature>().editProfile()
            }

            on<State>().observe(
                State.Area.Person,
                State.Area.Profile,
                State.Area.Ui
            ) {
                val person = on<State>().person.current

                val isMe = person?.id == on<MyProfileFeature>().myProfile.id
                val isProfileComplete = on<MyProfileFeature>().isComplete()

                if (isProfileComplete && profile.me.approved.not()) {
                    completeYourProfileButton.text = getString(R.string.profile_in_review)
                    return@observe
                }

                on<State> {
                    ui = ui.copy(showCompleteYourProfileButton = person != null && isMe.not() && isProfileComplete.not() && ui.showDiscoveryPreferences.not())
                }

                if (person != null) {
                    completeYourProfileButton.text = getString(R.string.complete_your_profile, person.name)
                }
            }
        }
    }
}