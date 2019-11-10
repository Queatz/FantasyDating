package com.queatz.fantasydating.features

import android.view.Gravity
import com.queatz.fantasydating.PersonStory
import com.queatz.fantasydating.R
import com.queatz.fantasydating.Upload
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class EditProfileFeature constructor(private val on: On) {
    fun editProfile() {
        on<StoryFeature>().person = on<MyProfileFeature>().myProfile

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
                                myProfile.stories.firstOrNull()?.story ?: ""
                            ) {
                                on<MyProfileFeature>().edit { stories = if (it.isEmpty()) listOf() else listOf(
                                    PersonStory(it, "https://somephoto.jpg")
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

            choosePhotoButton.setOnClickListener { on<Upload>().getPhotoFromDevice {
                on<StoryFeature>().setPhoto(it)
            } }

            updateMyStory()
        }
    }

    private fun updateMyStory() {
        on<ViewFeature>().with {
            storyText.text = on<MyProfileFeature>().myProfile.let { "<tap data=\"name\">${if (it.name.isBlank()) getString(R.string.your_name) else it.name}</tap>, <tap data=\"age\">${if (it.age < 18) getString(R.string.your_age) else it.age.toString()}</tap><br /><br />I love <tap data=\"story\">${if (it.stories.isEmpty()) "write something here" else it.stories[0].story}</tap>" }
            fantasyText.text = on<MyProfileFeature>().myProfile.fantasy.let { if (it.isBlank()) getString(R.string.empty_fantasy) else it }
        }
    }
}