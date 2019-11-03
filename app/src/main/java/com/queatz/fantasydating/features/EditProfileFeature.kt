package com.queatz.fantasydating.features

import android.view.Gravity
import com.queatz.fantasydating.R
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class EditProfileFeature constructor(private val on: On) {
    fun editProfile() {
        on<ViewFeature>().with {
            on<LayoutFeature>().showEditProfile = true

            on<MyProfileFeature>().myProfile.let { myProfile ->
                storyText.onLinkClick = {
                    when (it) {
                        "name" -> {
                            on<EditorFeature>().open(myProfile.name) {
                                on<MyProfileFeature>().edit { name = it }

                                updateMyStory()
                            }
                        }
                        "age" -> {
                            on<EditorFeature>().open(if (myProfile.age > 18) myProfile.age.toString() else "") {
                                on<MyProfileFeature>().edit { age = it.toIntOrNull() ?: 0 }

                                updateMyStory()
                            }
                        }
                        "story" -> {
                            on<EditorFeature>().open(
                                myProfile.stories.firstOrNull() ?: ""
                            ) {
                                on<MyProfileFeature>().edit { stories = if (it.isEmpty()) listOf() else listOf(it) }

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

            choosePhotoButton.setOnClickListener { close() }

            updateMyStory()
        }
    }

    fun close() {
        on<LayoutFeature>().showEditProfile = false
    }

    private fun updateMyStory() {
        on<ViewFeature>().with {
            storyText.text = on<MyProfileFeature>().myProfile.let { "<tap data=\"name\">${if (it.name.isBlank()) getString(R.string.your_name) else it.name}</tap>, <tap data=\"age\">${if (it.age < 18) getString(R.string.your_age) else it.age.toString()}</tap><br /><br />I love <tap data=\"story\">${if (it.stories.isEmpty()) "write something here" else it.stories[0]}</tap>" }
            fantasyText.text = on<MyProfileFeature>().myProfile.fantasy.let { if (it.isBlank()) getString(R.string.empty_fantasy) else it }
        }
    }
}