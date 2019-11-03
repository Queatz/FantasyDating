package com.queatz.fantasydating.features

import android.view.Gravity
import com.queatz.fantasydating.R
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class EditProfileFeature constructor(private val on: On) {
    fun editProfile() {
        on<ViewFeature>().with {
            on<LayoutFeature>().showEditProfile = true

            loveButton.visible = false
            moreOptionsButton.elevation = 1f

            on<MyProfileFeature>().myProfile.let { myProfile ->
                storyText.onLinkClick = {
                    when (it) {
                        "name" -> {
                            on<EditorFeature>().open(myProfile.name) {
                                myProfile.name = it

                                updateMyStory()
                            }
                        }
                        "age" -> {
                            on<EditorFeature>().open(if (myProfile.age > 18) myProfile.age.toString() else "") {
                                myProfile.age = it.toIntOrNull() ?: 0

                                updateMyStory()
                            }
                        }
                        "story" -> {
                            on<EditorFeature>().open(
                                myProfile.stories.firstOrNull() ?: ""
                            ) {
                                if (it.isEmpty()) {
                                    myProfile.stories = listOf()
                                } else {
                                    myProfile.stories = listOf(it)
                                }

                                updateMyStory()
                            }
                        }
                    }
                }

                fantasyText.setOnClickListener {
                    on<EditorFeature>().open(myProfile.fantasy, Gravity.START) {
                        myProfile.fantasy = it

                        updateMyStory()
                    }
                }
            }

            storyText.elevation = 1f

            choosePhotoButton.visible = true
            choosePhotoButton.setOnClickListener { close() }

            updateMyStory()
        }
    }

    fun close() {
        on<ViewFeature>().with {
            on<LayoutFeature>().showEditProfile = false

            choosePhotoButton.visible = false
            loveButton.visible = true
            moreOptionsButton.elevation = 0f
            storyText.onLinkClick = {}
            storyText.elevation = 0f
            fantasyText.setOnClickListener { }
        }
    }

    private fun updateMyStory() {
        on<ViewFeature>().with {
            storyText.text = on<MyProfileFeature>().myProfile.let { "<tap data=\"name\">${if (it.name.isBlank()) getString(R.string.your_name) else it.name}</tap>, <tap data=\"age\">${if (it.age < 18) getString(R.string.your_age) else it.age.toString()}</tap><br /><br />I love <tap data=\"story\">${if (it.stories.isEmpty()) "write something here" else it.stories[0]}</tap>" }
            fantasyText.text = on<MyProfileFeature>().myProfile.fantasy.let { if (it.isBlank()) getString(R.string.empty_fantasy) else it }
        }
    }
}