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
                if (background.drawable == null) {
                    return@with
                }

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

        override fun event(event: MotionEvent) {
            on<ViewFeature>().with {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> choosePhotoButton.visible = false
                    MotionEvent.ACTION_UP -> choosePhotoButton.visible = true
                }
            }
        }
    }

    fun editProfile() {
        on<State> {
            ui = ui.copy(
                showFantasy = false,
                showDiscoveryPreferences = false,
                showFeed = false,
                showEditProfile = true
            )
        }

        on<PeopleFeature>().showMe()

        on<ViewFeature>().with {
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
                            if (myProfile.stories.isEmpty()) {
                                on<MyProfileFeature>().edit { stories = listOf(
                                    PersonStory(it),
                                    PersonStory(it),
                                    PersonStory(it)
                                ) }
                            }

                            on<EditorFeature>().open(myProfile.stories[on<StoryFeature>().getCurrentStory()].story, prefix = "${getString(R.string.about_photo_prefix)} ") {
                                on<MyProfileFeature>().edit {
                                    stories[on<StoryFeature>().getCurrentStory()].story = it
                                }

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
                choosePhoto()
            }

            updateMyStory()
        }
    }

    private fun choosePhoto() {
        if (on<GesturesFeature>().listener == listener) {
            on<MyProfileFeature>().edit {
                stories[on<StoryFeature>().getCurrentStory()].apply {
                    x = on<ViewFeature>().with { background.origin.x }
                    y = on<ViewFeature>().with { background.origin.y }
                }
            }

            updatePhotoButton()

            on<GesturesFeature>().listener = on<GesturesFeature>().storyNavigationListener
            return
        }

        on<ViewFeature>().with {
            choosePhotoModal.text = getString(
                if (currentStoryHasPhoto())
                    R.string.choose_photo_modal
                else
                    R.string.upload_photo_modal
            )
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
                    "reposition" -> {
                        reposition()
                    }
                    "close" -> {}
                }
            }
        }
    }

    private fun updatePhotoButton() {
        on<ViewFeature>().with {
            choosePhotoButton.text = getString(
                if (currentStoryHasPhoto())
                    R.string.choose__photo
                else
                    R.string.upload__photo
            )
        }
    }

    private fun currentStoryHasPhoto(): Boolean {
        return on<MyProfileFeature>().myProfile.stories[on<StoryFeature>().getCurrentStory()].photo.isNotBlank()
    }

    private fun updateCurrentStoryPhoto(photo: String) {
        reposition()

        on<MyProfileFeature>().edit {
            stories[on<StoryFeature>().getCurrentStory()].photo = photo
        }

        on<StoryFeature>().setPhoto(photo)
    }

    private fun reposition() {
        on<GesturesFeature>().listener = listener

        on<ViewFeature>().with {
            choosePhotoButton.text = getString(R.string.confirm__position)
        }
    }

    fun updateMyStory() {
        updatePhotoButton()
        updateFantasy()

        on<ViewFeature>().with {
            val me = on<MyProfileFeature>().myProfile

            storyText.text = me.let { "<tap data=\"name\">${if (it.name.isBlank()) getString(R.string.your_name) else it.name}</tap>, <tap data=\"age\">${
                when {
                    it.age < 18 -> getString(R.string.your_age)
                    it.age > 99 -> getString(
                        R.string.old_lol)
                    else -> it.age.toString()
                }
            }</tap><br /><br />${getString(R.string.about_photo_prefix)} <tap data=\"story\">${it.stories[on<StoryFeature>().getCurrentStory()].story.let {
                when {
                    it.isBlank() -> getString(R.string.write_photo_description)
                    it.startsWith("${getString(R.string.about_photo_prefix)} ") -> it.replaceFirst("${getString(R.string.about_photo_prefix)} ", "")
                    else -> it
                }
            }}</tap>" }
        }
    }

    fun updateFantasy() {
        on<ViewFeature>().with {
            val me = on<MyProfileFeature>().myProfile
            fantasyTitle.text = if (me.name.isNotBlank()) getString(R.string.persons_fantasy, me.name) else getString(R.string.about_you)
            styleTitle.text = if (me.name.isNotBlank()) getString(R.string.persons_cuddle_styles, me.name) else getString(R.string.your_styles)
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
