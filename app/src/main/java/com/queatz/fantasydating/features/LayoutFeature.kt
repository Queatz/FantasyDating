package com.queatz.fantasydating.features

import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class LayoutFeature constructor(private val on: On) {

    var showEditProfile = false

    var showFantasy = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            on<ViewFeature>().with {
                if (value) {
                    on<WalkthroughFeature>().closeBub(bub3)
                    swipeUpArrow.rotation = 180f
                    fantasy.visible = true
                    choosePhotoButton.visible = false
                    storyText.visible = false
                    moreOptionsButton.visible = false
                    moreOptionsText.visible = false
                    on<StoryFeature>().event(StoryEvent.Pause)
                    on<WalkthroughFeature>().showBub(bub4)
                    on<WalkthroughFeature>().closeBub(bub5)
                } else {
                    swipeUpArrow.rotation = 0f
                    fantasy.visible = false
                    storyText.visible = true
                    moreOptionsButton.visible = true
                    on<StoryFeature>().event(StoryEvent.Resume)
                    on<WalkthroughFeature>().closeBub(bub4)
                    on<WalkthroughFeature>().showBub(bub5)

                    if (showEditProfile) {
                        choosePhotoButton.visible = true
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

                discoveryPreferencesText.visible = value
                feed.visible = value

                if (value) {
                    on<StoryFeature>().event(StoryEvent.Reset)
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
            showDiscoveryPreferences -> showDiscoveryPreferences = false
            showFantasy -> showFantasy = false
            showFeed.not() -> showFeed = true
            else -> return false
        }

        return true
    }
}
