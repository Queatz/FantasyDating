package com.queatz.fantasydating.features

import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.queatz.fantasydating.State
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class TutorialFeature constructor(private val on: On) {
    fun refresh() {
        on<ViewFeature>().with {
            createProfileHintChoosePhotoButton.animation?.cancel()
            createProfileHintSwipeUpButton.animation?.cancel()
            createProfileHintStoryText.animation?.cancel()
            createProfileHintNextStory.animation?.cancel()
            createProfileHintChoosePhotoButton.visible = false
            createProfileHintSwipeUpButton.visible = false
            createProfileHintStoryText.visible = false
            createProfileHintNextStory.visible = false

            if (on<State>().ui.showEditProfile && !on<State>().ui.showFantasy) {
                listOfNotNull(
                    if (on<MyProfileFeature>().myProfile.fantasy.isBlank() || on<MyProfileFeature>().myProfile.styles.isEmpty()) createProfileHintSwipeUpButton else null,
                    if (on<MyProfileFeature>().myProfile.stories[on<StoryFeature>().getCurrentStory()].photo.isBlank()) createProfileHintChoosePhotoButton else null,
                    if (on<MyProfileFeature>().myProfile.name.isBlank() || on<MyProfileFeature>().myProfile.age < 1 || on<MyProfileFeature>().myProfile.stories[on<StoryFeature>().getCurrentStory()].story.isBlank()) createProfileHintStoryText else null
                ).forEach {
                    it.visible = true
                    it.startAnimation(
                        TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,
                            0f,
                            Animation.RELATIVE_TO_SELF,
                            0f,
                            Animation.RELATIVE_TO_SELF,
                            -.75f,
                            Animation.RELATIVE_TO_SELF,
                            0f
                        ).also {
                            it.repeatCount = Animation.INFINITE
                            it.repeatMode = Animation.REVERSE
                            it.interpolator = FastOutSlowInInterpolator()
                            it.duration = 1500
                        })
                }
                listOfNotNull(
                    if (on<MyProfileFeature>().myProfile.stories.any { it.story.isBlank() || it.photo.isBlank() }) createProfileHintNextStory else null
                ).forEach {
                    it.visible = true
                    it.startAnimation(
                        TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,
                            -.75f,
                            Animation.RELATIVE_TO_SELF,
                            0f,
                            Animation.RELATIVE_TO_SELF,
                            0f,
                            Animation.RELATIVE_TO_SELF,
                            0f
                        ).also {
                            it.repeatCount = Animation.INFINITE
                            it.repeatMode = Animation.REVERSE
                            it.interpolator = FastOutSlowInInterpolator()
                            it.duration = 1500
                        })
                }
            } else {
                createProfileHintChoosePhotoButton.animation?.cancel()
                createProfileHintSwipeUpButton.animation?.cancel()
                createProfileHintStoryText.animation?.cancel()
                createProfileHintNextStory.animation?.cancel()
                createProfileHintChoosePhotoButton.visible = false
                createProfileHintSwipeUpButton.visible = false
                createProfileHintStoryText.visible = false
                createProfileHintNextStory.visible = false
            }
        }
    }
}
