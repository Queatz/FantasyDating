package com.queatz.fantasydating.features

import com.queatz.fantasydating.otherwise
import com.queatz.fantasydating.then
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class LayoutFeature constructor(private val on: On) {

    var showEditProfile = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            on<ViewFeature>().with {
                stories.animate = value.not()

                if (value) {
                    on<GesturesFeature>().listener = on<GesturesFeature>().storyNavigationListener

                    moreOptionsButton.elevation = 1f

                    storyText.elevation = 1f

                    choosePhotoButton.visible = true
                } else {
                    on<GesturesFeature>().listener = on<GesturesFeature>().storyNavigationListener

                    choosePhotoButton.visible = false
                    moreOptionsButton.elevation = 0f
                    storyText.onLinkClick = { }
                    storyText.elevation = 0f
                    fantasyText.setOnClickListener { }
                }
            }
        }

    var showFantasy = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            on<ViewFeature>().with {
                confirmLove.visible = false

                if (value) {
                    on<WalkthroughFeature>().closeBub(bub3)
                    swipeUpArrow.rotation = 180f
                    fantasy.visible = true
                    choosePhotoButton.visible = false
                    storyText.visible = false
                    moreOptionsButton.visible = false
                    on<MoreOptionsFeature>().close()
                    on<StoryFeature>().event(StoryEvent.Pause)

                    showEditProfile otherwise {
                        on<WalkthroughFeature>().showBub(bub4)
                        on<WalkthroughFeature>().closeBub(bub5)
                    }
                } else {
                    swipeUpArrow.rotation = 0f
                    fantasy.visible = false
                    storyText.visible = true
                    moreOptionsButton.visible = true
                    on<StoryFeature>().event(StoryEvent.Resume)

                    showEditProfile then {
                        choosePhotoButton.visible = true
                    } otherwise {
                        on<WalkthroughFeature>().closeBub(bub4)
                        on<WalkthroughFeature>().showBub(bub5)
                    }
                }
            }
        }

    var showFeed = true
        set(value) {
            if (field == value) {
                return
            }

            field = value

            on<ViewFeature>().with {
                showEditProfile = false

                discoveryPreferencesText.visible = value
                feed.visible = value

                if (value) {
                    on<PeopleFeature>().reset()
                }
            }
        }

    var showDiscoveryPreferences = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            on<ViewFeature>().with {
                discoveryPreferencesLayout.visible = value
                on<CompleteProfileFeature>().update()
                showFeed = !value

                if (value) {
                    on<WalkthroughFeature>().closeBub(bub2)
                } else {
                    editPreferenceText.visible = false
                    editProfileText.visible = true
                }
            }
        }

    fun onBackPressed(): Boolean {
        when {
            showFantasy -> showFantasy = false
            showEditProfile -> showEditProfile = false
            showDiscoveryPreferences -> showDiscoveryPreferences = false
            showFeed.not() -> showFeed = true
            else -> return false
        }

        return true
    }
}