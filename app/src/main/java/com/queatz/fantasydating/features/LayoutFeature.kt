package com.queatz.fantasydating.features

import android.graphics.Shader
import com.queatz.fantasydating.*
import com.queatz.fantasydating.ui.TileDrawable
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class LayoutFeature constructor(private val on: On) {

    var isBoss = on<MyProfileFeature>().myProfile.boss
    var canCloseFullscreenModal = false

    fun start() {
        on<ViewFeature>().with {
            polka.setImageDrawable(TileDrawable(getDrawable(R.drawable.polka)!!, Shader.TileMode.REPEAT))

            on<State>().observe(State.Area.Ui) {
                discoveryPreferencesLayout.visible = ui.showDiscoveryPreferences
                discoveryPreferencesText.visible = ui.showFeed && ui.showEditProfile.not() && ui.showDiscoveryPreferences.not()
                feed.visible = ui.showFeed && ui.showEditProfile.not()

                changed(it) { ui.showCompleteYourProfileButton } then {
                    completeYourProfileButton.visible = ui.showCompleteYourProfileButton
                }

                changed(it) { ui.showEditProfile } then {
                    polka.visible = ui.showEditProfile.not() && ui.showFeed || ui.showDiscoveryPreferences
                    stories.animate = ui.showEditProfile.not()

                    on<StoryFeature>().personNavigationListener = if (ui.showEditProfile) { _ -> true } else null

                    on<GesturesFeature>().listener =
                        on<GesturesFeature>().storyNavigationListener

                    if (ui.showEditProfile) {
                        moreOptionsButton.elevation = 1f

                        storyText.elevation = 1f

                        choosePhotoButton.visible = true
                    } else {
                        on<PeopleFeature>().reset()

                        if (ui.showFeed.not()) {
                            ui = ui.copy(showFeed = true)
                        }

                        choosePhotoButton.visible = false
                        moreOptionsButton.elevation = 0f
                        storyText.onLinkClick = { }
                        storyText.elevation = 0f
                        fantasyText.setOnClickListener { }
                    }
                }

                changed(it) { ui.showFantasy } then {
                    confirmLove.visible = false

                    if (ui.showFantasy) {
                        on<WalkthroughFeature>().closeBub(bub3)
                        swipeUpArrow.rotation = 180f
                        fantasy.visible = true
                        choosePhotoButton.visible = false
                        storyText.visible = false
                        moreOptionsButton.visible = false
                        on<MoreOptionsFeature>().close()
                        on<StoryFeature>().event(StoryEvent.Pause)

                        ui.showEditProfile otherwise {
                            on<State>().person.current?.let {
                                bub4?.text = getString(R.string.bub4, on<ValueFeature>().referToAs(it.sex, true), on<ValueFeature>().pronoun(it.sex))
                                on<WalkthroughFeature>().showBub(bub4)
                            }
                            on<WalkthroughFeature>().closeBub(bub5)
                        }
                    } else {
                        swipeUpArrow.rotation = 0f
                        fantasy.visible = false
                        storyText.visible = true
                        moreOptionsButton.visible = person.current != null
                        on<StoryFeature>().event(StoryEvent.Resume)

                        ui.showEditProfile then {
                            choosePhotoButton.visible = true
                        } otherwise {
                            on<WalkthroughFeature>().closeBub(bub4)
                            on<WalkthroughFeature>().showBub(bub5)
                        }
                    }
                }

                changed(it) { ui.showFeed } then {
                    polka.visible = ui.showFeed && ui.showEditProfile.not() || ui.showDiscoveryPreferences

                    if (ui.showFeed) {
                        stories.post { on<StoryFeature>().event(StoryEvent.Reset) }
                    }
                }

                changed(it) { ui.showDiscoveryPreferences } then {
                    if (ui.showDiscoveryPreferences && ui.showCompleteYourProfileButton) {
                        ui = ui.copy(showCompleteYourProfileButton = false)
                    }

                    if (ui.showFeed != ui.showDiscoveryPreferences.not()) {
                        ui = ui.copy(showFeed = ui.showDiscoveryPreferences.not())
                    }

                    if (ui.showDiscoveryPreferences) {
                        loadBossInfo()

                        bossOverview.visible = isBoss
                        bossOverview.onLinkClick = {
                            when (it) {
                                "approve" -> {
                                    on<BossFeature>().showApprovals()
                                    ui = ui.copy(showDiscoveryPreferences = false)
                                }
                                "reports" -> {
                                    on<BossFeature>().showReports()
                                    ui = ui.copy(showDiscoveryPreferences = false)
                                }
                                "reload" -> {
                                    loadBossInfo()
                                }
                            }
                        }
                        on<WalkthroughFeature>().closeBub(bub2)
                    } else {
                        editPreferenceText.visible = false
                        editProfileText.visible = true
                    }
                }
            }
        }
    }

    private fun loadBossInfo() {
        on<ViewFeature>().with {
            bossOverview.text = "Please wait..."
            on<Api>().bossInfo {
                bossOverview.text = "${it.approvals} <tap data=\"approve\">Approvals</tap>, ${it.reports} <tap data=\"reports\">Reports</tap>"
            } error {
                bossOverview.text = "Failed to load. <tap data=\"reload\">Reload</tap>"
            }
        }
    }

    fun onBackPressed(): Boolean {
        if (on<ViewFeature>().activity.fullscreenMessageLayout.visible) {
            if (canCloseFullscreenModal) {
                on<ViewFeature>().activity.fullscreenMessageLayout.visible = false
            }

            return true
        }

        on<State>().apply {
            ui = when {
                ui.showFantasy -> ui.copy(showFantasy = false)
                ui.showEditProfile -> ui.copy(showEditProfile = false)
                ui.showDiscoveryPreferences -> ui.copy(showDiscoveryPreferences = false)
                ui.showFeed.not() -> ui.copy(showFeed = true)
                else -> return false
            }
        }

        return true
    }
}