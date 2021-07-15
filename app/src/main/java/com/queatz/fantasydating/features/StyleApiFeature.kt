package com.queatz.fantasydating.features

import com.queatz.fantasydating.Api
import com.queatz.fantasydating.MeStyleRequest
import com.queatz.fantasydating.R
import com.queatz.fantasydating.Say
import com.queatz.on.On

class StyleApiFeature constructor(private val on: On) {
    fun add(styleId: String) {
        on<Api>().linkStyle(MeStyleRequest(link = styleId)) {
            on<Say>().say(R.string.style_added_to_your_profile)
            on<MyProfileFeature>().reload()
        }
    }

    fun remove(styleId: String) {
        on<Api>().linkStyle(MeStyleRequest(unlink = styleId)) {
            on<Say>().say(R.string.style_removed_from_your_profile)
            on<MyProfileFeature>().reload()
        }
    }

    fun promote(styleId: String) {
        on<Api>().linkStyle(MeStyleRequest(promote = styleId)) {
            on<Say>().say(R.string.style_preferences_updated)
            on<MyProfileFeature>().reload()
        }
    }

    fun demote(styleId: String) {
        on<Api>().linkStyle(MeStyleRequest(demote = styleId)) {
            on<Say>().say(R.string.style_preferences_updated)
            on<MyProfileFeature>().reload()
        }
    }

    fun dismiss(styleId: String) {
        on<Api>().linkStyle(MeStyleRequest(dismiss = styleId)) {
            on<Say>().say(R.string.style_preferences_updated)
            on<MyProfileFeature>().reload()
        }
    }
}