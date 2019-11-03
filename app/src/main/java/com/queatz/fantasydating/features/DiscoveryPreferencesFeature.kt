package com.queatz.fantasydating.features

import com.queatz.fantasydating.R
import com.queatz.fantasydating.models.DiscoveryPreferences
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class DiscoveryPreferencesFeature constructor(private val on: On) {

    private val sexes = setOf("Girls", "Boys", "People")
    private val ages = listOf(18, 20, 22, 24, 26, 28, 30, 35, 40, 45, 50, 1000)
    private val discoveryPreferences = DiscoveryPreferences("Girls", "Austin", 25, 35)

    fun start() {
        on<ViewFeature>().with {
            discoveryPreferencesText.onLinkClick = {
                on<LayoutFeature>().showDiscoveryPreferences = true
            }

            discoveryPreferencesLayout.setOnClickListener {
                on<LayoutFeature>().showDiscoveryPreferences = false
            }

            editPreferenceText.onLinkClick = {
                when {
                    it == "nope" -> {}
                    sexes.contains(it) -> discoveryPreferences.who = it
                    it == "Austin" -> discoveryPreferences.where = it
                    it.startsWith("min:") -> discoveryPreferences.ageMin = it.split(":").last().toInt()
                    it.startsWith("max:") -> discoveryPreferences.ageMax = it.split(":").last().toInt()
                }

                editPreferenceText.visible = false
                editProfileText.visible = true

                updateDiscoveryPreferences()
            }

            editDiscoveryPreferencesText.onLinkClick = {
                when (it) {
                    "who" -> {
                        editPreferenceText.text = resources.getString(R.string.edit_who)
                    }
                    "where" -> {
                        editPreferenceText.text = resources.getString(R.string.edit_where)
                    }
                    "ageMin" -> {
                        editPreferenceText.text = ages
                            .filter { it < discoveryPreferences.ageMax }
                            .map { "<tap data=\"min:${it}\">${if (it == 1000) getString(R.string.any) else it.toString()}</tap>" }
                            .joinToString(" &nbsp;&nbsp; ")
                    }
                    "ageMax" -> {
                        editPreferenceText.text = ages
                            .filter { it > discoveryPreferences.ageMin }
                            .map { "<tap data=\"max:${it}\">${if (it == 1000) getString(R.string.any) else it.toString()}</tap>" }
                            .joinToString(" &nbsp;&nbsp; ")
                    }
                }

                editPreferenceText.visible = true
                editProfileText.visible = false

                updateDiscoveryPreferences()
            }

            editProfileText.onLinkClick = {
                on<LayoutFeature>().showFantasy = false
                on<LayoutFeature>().showDiscoveryPreferences = false
                on<LayoutFeature>().showFeed = false

                on<EditProfileFeature>().editProfile()
            }

            updateDiscoveryPreferences()
        }
    }

    private fun updateDiscoveryPreferences() {
        on<ViewFeature>().with {
            editDiscoveryPreferencesText.text = resources.getString(R.string.discovery_preferences_template,
                discoveryPreferences.who,
                discoveryPreferences.where,
                discoveryPreferences.ageMin.toString(),
                discoveryPreferences.ageMax.let { if (it == 1000) getString(R.string.any) else it.toString() }
            )

            discoveryPreferencesText.text = resources.getString(R.string.discovery_preferences,
                discoveryPreferences.who,
                discoveryPreferences.where,
                discoveryPreferences.ageMin.toString(),
                discoveryPreferences.ageMax.let { if (it == 1000) getString(R.string.any) else it.toString() }
            )
        }
    }
}