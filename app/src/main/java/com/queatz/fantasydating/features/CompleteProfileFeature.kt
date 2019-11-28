package com.queatz.fantasydating.features

import com.queatz.fantasydating.*
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class CompleteProfileFeature constructor(private val on: On) {
    fun start() {
        on<ViewFeature>().with {
            completeYourProfileButton.onLinkClick = {
                when (it) {
                    "profile" -> on<EditProfileFeature>().editProfile()
                    "submit" -> {
                        on<Api>().me(MeRequest(active = true)) {
                            val me = on<MyProfileFeature>().myProfile
                            me.active = true
                            on<StoreFeature>().get(Person::class).put(me)
                            on<State>().profile = ProfileState(me)
                        }
                    }
                }
            }

            on<State>().observe(
                State.Area.Person,
                State.Area.Profile,
                State.Area.Ui
            ) {
                val person = on<State>().person.current

                val isMe = person?.id == on<MyProfileFeature>().myProfile.id
                val isProfileComplete = on<MyProfileFeature>().isComplete()

                if (isProfileComplete) {
                    if (profile.me.active.not()) {
                        completeYourProfileButton.text = getString(R.string.submit_profile_for_review)
                        show()
                        return@observe
                    } else if (profile.me.approved.not()) {
                        completeYourProfileButton.text = getString(R.string.profile_in_review)
                        show()
                        return@observe
                    }
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

    private fun show() {
        on<State> {
            ui = ui.copy(showCompleteYourProfileButton = ui.showDiscoveryPreferences.not())
        }
    }
}