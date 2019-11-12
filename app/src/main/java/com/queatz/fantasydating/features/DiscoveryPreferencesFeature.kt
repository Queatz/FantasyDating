package com.queatz.fantasydating.features

import com.queatz.fantasydating.*
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import kotlinx.android.synthetic.main.activity_main.*

class DiscoveryPreferencesFeature constructor(private val on: On) : OnLifecycle {

    private val sexes = setOf("Girls", "Boys", "People")
    private val ages = listOf(18, 20, 22, 24, 26, 28, 30, 35, 40, 45, 50, 1000)

    private lateinit var discoveryPreferences: DiscoveryPreferences

    fun edit(function: DiscoveryPreferences.() -> Unit) {
        function.invoke(discoveryPreferences)

        on<Api>().discoveryPreferences(MeDiscoveryPreferencesRequest(
            where = discoveryPreferences.where,
            who = discoveryPreferences.who,
            ageMin = discoveryPreferences.ageMin,
            ageMax = discoveryPreferences.ageMax
        )) {}

        on<StoreFeature>().get(DiscoveryPreferences::class).put(discoveryPreferences)
    }

    override fun on() {
        discoveryPreferences = on<StoreFeature>().get(DiscoveryPreferences::class).all.firstOrNull() ?: DiscoveryPreferences(
            "Girls",
            "Austin",
            25,
            35
        )
    }

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
                    it == "nope" -> {
                        editPreferenceText.text = resources.getString(R.string.not_in_austin)
                    }
                    it == "ok" -> {
                        editPreferenceText.text = resources.getString(R.string.edit_where)
                    }
                    sexes.contains(it) -> on<DiscoveryPreferencesFeature>().edit { who = it }
                    it == "Austin" -> on<DiscoveryPreferencesFeature>().edit { where = it }
                    it.startsWith("min:") -> on<DiscoveryPreferencesFeature>().edit { ageMin = it.split(":").last().toInt() }
                    it.startsWith("max:") -> on<DiscoveryPreferencesFeature>().edit { ageMax = it.split(":").last().toInt() }
                }

                if (it != "nope") {
                    editPreferenceText.visible = false
                    editProfileText.visible = true

                    updateDiscoveryPreferences()
                }
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