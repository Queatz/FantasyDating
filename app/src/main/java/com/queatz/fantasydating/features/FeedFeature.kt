package com.queatz.fantasydating.features

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.queatz.fantasydating.*
import com.queatz.fantasydating.ui.FeedAdapter
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fullscreen_modal.*

class FeedFeature constructor(private val on: On) {
    private lateinit var adapter: FeedAdapter

    fun start() {
        reload()

        on<ViewFeature>().with {
            feedRecyclerView.layoutManager = LinearLayoutManager(this)

            adapter = FeedAdapter(on)

            feedRecyclerView.adapter = adapter

            on<State>().observe(State.Area.Ui) {
                if (changed(it) { ui.showFeed }) {
                    if (ui.showFeed) {
                        reload()
                    }
                }
            }
        }
    }

    private fun reload() {
        on<Api>().events {
            adapter.items = it.toMutableList()
        }
    }

    fun open(event: Event) {
        if (event.data.isNotBlank()) {
            val json = on<Json>().from<JsonObject>(event.data, JsonObject::class.java)
            if (json.has("type").not()) {
                return
            }

            when (json.getAsJsonPrimitive("type").asString) {
                "live" -> {
                    open(on<Json>().from<ProfileLiveEventType>(event.data, ProfileLiveEventType::class.java))
                }
                "invited" -> {
                    open(on<Json>().from<ProfileLiveEventType>(event.data, InvitedEventType::class.java))
                }
                "love" -> {
                    open(on<Json>().from<LoveEventType>(event.data, LoveEventType::class.java))
                }
                "unlove" -> {
                    open(on<Json>().from<UnloveEventType>(event.data, LoveEventType::class.java))
                }
                "story" -> {
                    open(on<Json>().from<StoryUpdateEventType>(event.data, StoryUpdateEventType::class.java))
                }
            }
        }
    }

    private fun open(event: EventType) {
        when (event) {
            is ProfileLiveEventType -> {
                if (event.live) {
                    on<EditProfileFeature>().editProfile()
                } else {
                    on<ViewFeature>().with {
                        on<LayoutFeature>().canCloseFullscreenModal = true
                        fullscreenMessageText.text = "${event.message}<br /><br /><tap data=\"profile\">Edit your profile</tap> or <tap data=\"close\">Close</tap>"
                        fullscreenMessageLayout.fadeIn()
                        fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)


                        fullscreenMessageText.onLinkClick = {
                            when (it) {
                                "profile" -> on<EditProfileFeature>().editProfile()
                            }
                            fullscreenMessageLayout.fadeOut()
                        }
                    }
                }
            }
            is InvitedEventType -> {
                on<NavigationFeature>().showPerson(event.person)
            }
            is LoveEventType -> {
                on<NavigationFeature>().showMessages(event.person)
            }
            is UnloveEventType -> {
                on<NavigationFeature>().showMessages(event.person)
            }
            is StoryUpdateEventType -> {
                on<NavigationFeature>().showMessages(event.person)
            }
        }
    }
}
