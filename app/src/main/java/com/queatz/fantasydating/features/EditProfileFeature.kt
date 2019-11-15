package com.queatz.fantasydating.features

import android.graphics.PointF
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import com.queatz.fantasydating.*
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class EditProfileFeature constructor(private val on: On) {

    private val listener = object : InteractionListener {
        override fun scroll(distanceX: Float, distanceY: Float, event: MotionEvent): Boolean {
            on<ViewFeature>().with {
                val dw = background.drawable.intrinsicWidth.toFloat()
                val dh = background.drawable.intrinsicHeight.toFloat()
                val vw = background.measuredWidth.toFloat()
                val vh = background.measuredHeight.toFloat()

                val xScale = (vw - dw * background.baseScale)
                val yScale = (vh - dh * background.baseScale)

                background.origin = PointF(
                    (background.origin.x - if (xScale != 0f) distanceX / xScale else 0f).clamp(),
                    (background.origin.y - if (yScale != 0f) distanceY / yScale else 0f).clamp()
                )
            }

            return true
        }
    }

    fun editProfile() {
        on<PeopleFeature>().showMe()

        on<ViewFeature>().with {
            on<LayoutFeature>().showEditProfile = true

            on<MyProfileFeature>().myProfile.let { myProfile ->
                storyText.onLinkClick = {
                    when (it) {
                        "name" -> {
                            on<EditorFeature>().open(myProfile.name, inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_TEXT_VARIATION_PERSON_NAME) {
                                on<MyProfileFeature>().edit { name = it }

                                updateMyStory()
                            }
                        }
                        "age" -> {
                            on<EditorFeature>().open(if (myProfile.age > 18) myProfile.age.toString() else "", inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED) {
                                on<MyProfileFeature>().edit { age = it.toIntOrNull() ?: 0 }

                                updateMyStory()
                            }
                        }
                        "story" -> {
                            on<EditorFeature>().open(myProfile.stories.firstOrNull()?.story ?: "", prefix = "I love ") {
                                on<MyProfileFeature>().edit { stories = if (it.isEmpty()) listOf() else listOf(
                                    PersonStory(it),
                                    PersonStory(it),
                                    PersonStory(it)
                                ) }

                                updateMyStory()
                            }
                        }
                    }
                }

                fantasyText.setOnClickListener {
                    on<EditorFeature>().open(myProfile.fantasy, Gravity.START) {
                        on<MyProfileFeature>().edit { fantasy = it }

                        updateMyStory()
                    }
                }
            }

            choosePhotoButton.setOnClickListener {
//                on<GesturesFeature>().listener = listener
//                on<StoryFeature>().event(StoryEvent.Pause)
                choosePhoto()
            }

            updateMyStory()
        }
    }

    private fun choosePhoto() {
        if (on<GesturesFeature>().listener == listener) {
            on<MyProfileFeature>().edit {
                stories[on<StoryFeature>().getCurrentStory()].apply {
                    x = on<ViewFeature>().with { background.x }
                    y = on<ViewFeature>().with { background.y }
                }
            }

            on<ViewFeature>().with {
                choosePhotoButton.text = getString(R.string.choose__photo)
            }

            on<GesturesFeature>().listener = on<GesturesFeature>().storyNavigationListener
            return
        }

        on<ViewFeature>().with {
            choosePhotoModal.text = getString(R.string.choose_photo_modal)
            choosePhotoModal.visible = true

            choosePhotoModal.onLinkClick = {
                choosePhotoModal.visible = false

                when (it) {
                    "guidebook" -> {
                        choosePhotoModal.text = getString(R.string.photography_guidebook)
                        choosePhotoModal.visible = true
                    }
                    "guidebook:close" -> {
                        choosePhoto()
                    }
                    "choose" -> {
                        on<Upload>().getPhotoFromDevice {
                            updateCurrentStoryPhoto(it)
                        }
                    }
                    "hire" -> {
                        on<Say>().say("working on it...")
                    }
                }
            }
        }
    }

    private fun updateCurrentStoryPhoto(photo: String) {
        on<GesturesFeature>().listener = listener

        on<ViewFeature>().with {
            choosePhotoButton.text = getString(R.string.confirm__position)
        }

        on<MyProfileFeature>().edit {
            stories[on<StoryFeature>().getCurrentStory()].photo = photo
        }

        on<StoryFeature>().setPhoto(photo)
    }

    private fun updateMyStory() {
        on<ViewFeature>().with {
            storyText.text = on<MyProfileFeature>().myProfile.let { "<tap data=\"name\">${if (it.name.isBlank()) getString(R.string.your_name) else it.name}</tap>, <tap data=\"age\">${if (it.age < 18) getString(R.string.your_age) else it.age.toString()}</tap><br /><br />I love <tap data=\"story\">${if (it.stories.isEmpty() || it.stories.first().story.isBlank()) "write something to complement your photo" else it.stories[0].story.let { 
                if (it.startsWith("I love ")) {
                    it.replaceFirst("I love ", "")
                } else {
                    it
                }
            }}</tap>" }
            fantasyText.text = on<MyProfileFeature>().myProfile.fantasy.let { if (it.isBlank()) getString(R.string.empty_fantasy) else it }
        }
    }

    fun onBackPressed() = on<ViewFeature>().with {
        if (choosePhotoModal.visible) {
            choosePhotoModal.visible = false
            true
        } else false
    }
}
