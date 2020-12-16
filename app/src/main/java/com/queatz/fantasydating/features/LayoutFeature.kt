package com.queatz.fantasydating.features

import android.graphics.Shader
import com.queatz.fantasydating.*
import com.queatz.fantasydating.ui.TileDrawable
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.fantasy
import kotlinx.android.synthetic.main.activity_main.fantasyText
import kotlinx.android.synthetic.main.fullscreen_modal.*

class LayoutFeature constructor(private val on: On) {

    var isBoss = on<MyProfileFeature>().myProfile.boss
    var canCloseFullscreenModal = false
        set(value) {
            field = value

            if (canCloseFullscreenModal) {
                on<ViewFeature>().with { fullscreenMessageLayout }.setOnClickListener {
                    it.fadeOut()
                }
            } else {
                on<ViewFeature>().with { fullscreenMessageLayout }.setOnClickListener(null)
            }
        }

    fun start() {
        on<ViewFeature>().with {
            polka.setImageDrawable(TileDrawable(getDrawable(R.drawable.polka)!!, Shader.TileMode.REPEAT))

            on<State>().observe(State.Area.Ui) {
                when (ui.showDiscoveryPreferences) {
                    true -> discoveryPreferencesLayout.fadeIn(reverse = true)
                    false -> discoveryPreferencesLayout.fadeOut(reverse = true)
                }
                when (ui.showFeed && ui.showEditProfile.not() && ui.showDiscoveryPreferences.not()) {
                    true -> discoveryPreferencesText.fadeIn()
                    false -> discoveryPreferencesText.fadeOut()
                }
                when (ui.showFeed && ui.showEditProfile.not()) {
                    true -> feed.fadeIn(speed = .5f)
                    false -> feed.fadeOut(speed = 4f)
                }
                changed(it) { ui.showCompleteYourProfileButton } then {
                    completeYourProfileButton.visible = ui.showCompleteYourProfileButton
                }

                changed(it) { ui.showStoryDetails } then {
                    moreOptionsButton.visible = ui.showStoryDetails
                    swipeUpArrow.visible = ui.showStoryDetails
                    storyText.visible = ui.showStoryDetails
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
                        fantasy.appear()
                        choosePhotoButton.visible = false
                        storyText.visible = false
                        moreOptionsButton.visible = false
                        on<MoreOptionsFeature>().close(true)
                        on<StoryFeature>().event(StoryEvent.Pause)

                        ui.showEditProfile otherwise {
                            on<State>().person.current?.let {
                                bub4?.text = getString(R.string.bub4, on<ValueFeature>().referToAs(it.sex, true), on<ValueFeature>().pronoun(it.sex))
                                on<WalkthroughFeature>().showBub(bub4)
                            }
                            on<WalkthroughFeature>().closeBub(bub5)
                        }
                    } else {
                        fantasy.disappear {
                            swipeUpArrow.rotation = 0f
                            moreOptionsButton.visible = false

                            if (on<StoryFeature>().scaleHandler.targetScale == 1f) {
                                storyText.fadeIn(slide = false)

                                if (person.current != null) {
                                    moreOptionsButton.fadeIn(slide = false)
                                }
                            }

                            on<StoryFeature>().event(StoryEvent.Resume)

                            ui.showEditProfile then {
                                choosePhotoButton.fadeIn(slide = false)
                            } otherwise {
                                on<WalkthroughFeature>().closeBub(bub4)
                                person.current?.let {
                                    on<WalkthroughFeature>().showBub(bub5)
                                }
                            }
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

                        inviteAFriend.visible = on<MyProfileFeature>().myProfile.approved
                        inviteAFriend.onLinkClick = {
                            on<InviteFeature>().showInviteCode()
                        }

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
                on<ViewFeature>().activity.fullscreenMessageLayout.fadeOut()
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