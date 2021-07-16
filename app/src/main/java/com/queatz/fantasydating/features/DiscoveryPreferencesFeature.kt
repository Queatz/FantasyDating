package com.queatz.fantasydating.features

import com.queatz.fantasydating.*
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import kotlinx.android.synthetic.main.activity_main.*

class DiscoveryPreferencesFeature constructor(private val on: On) : OnLifecycle {

    private val sexes = setOf("Girl", "Boy", "Person")
    private val ages = listOf(17, 18, 19, 20, 21, 22, 24, 26, 28, 30, 35, 40, 45, 50, 1000)

    lateinit var discoveryPreferences: DiscoveryPreferences

    fun edit(function: DiscoveryPreferences.() -> Unit) {
        function.invoke(discoveryPreferences)

        save(discoveryPreferences)

        on<StoreFeature>().get(DiscoveryPreferences::class).put(discoveryPreferences)
    }

    private fun save(discoveryPreferences: DiscoveryPreferences) {
        on<Api>().discoveryPreferences(MeDiscoveryPreferencesRequest(
            where = discoveryPreferences.where,
            who = discoveryPreferences.who,
            ageMin = discoveryPreferences.ageMin,
            ageMax = discoveryPreferences.ageMax
        )) {
            on<PeopleFeature>().reload()
        }
    }

    override fun on() {
        discoveryPreferences = on<StoreFeature>().get(DiscoveryPreferences::class).all.firstOrNull() ?: let {
            val defaultDiscoveryPreferences = DiscoveryPreferences(
                "Person",
                "Austin",
                17,
                1000
            )

            save(defaultDiscoveryPreferences)

            defaultDiscoveryPreferences
        }
    }

    fun start() {
        on<ViewFeature>().with {
            discoveryPreferencesText.onLinkClick = {
                on<State>{
                    ui = ui.copy(showDiscoveryPreferences = true)
                }
            }

            discoveryPreferencesLayout.setOnClickListener {
                on<State>{
                    ui = ui.copy(showDiscoveryPreferences = false)
                }
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
                when (it) {
                    "edit" -> { on<EditProfileFeature>().editProfile() }
                    "qi" -> { on<EditPrefsFeature>().edit() }
                }
            }

            editProfileText.setOnLongClickListener {
                on<EditorFeature>().open {
                    on<Api>().boss(WhoIsTheBossRequest(it)) {
                        if (it.success) {
                            on<LayoutFeature>().isBoss = true
                            on<Say>().say("Yeaahhh")
                        } else {
                            on<Say>().say("Naahhh")
                        }
                    }
                }

                true
            }

            updateDiscoveryPreferences()
        }
    }

    private fun updateDiscoveryPreferences() {
        on<ViewFeature>().with {
            editDiscoveryPreferencesText.text = resources.getString(R.string.discovery_preferences_template,
                on<ValueFeature>().pluralSex(discoveryPreferences.who),
                discoveryPreferences.where,
                discoveryPreferences.ageMin.toString(),
                discoveryPreferences.ageMax.let { if (it == 1000) getString(R.string.any) else it.toString() }
            )

            discoveryPreferencesText.text = resources.getString(R.string.discovery_preferences,
                on<ValueFeature>().pluralSex(discoveryPreferences.who),
                discoveryPreferences.where,
                discoveryPreferences.ageMin.toString(),
                discoveryPreferences.ageMax.let { if (it == 1000) getString(R.string.any) else it.toString() }
            )
        }
    }
}