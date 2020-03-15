package com.queatz.fantasydating.features

import com.queatz.fantasydating.*
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class BossFeature constructor(private val on: On) {

    private val personNavigationListener: PersonNavigationListener = {
        when (it) {
            1 -> {
                showNextReport()
            }
            -1 -> {
                index -= 2
                showNextReport()
            }
            else -> false
        }
    }

    private var index = -1
    private var reports = listOf<Report>()

    fun start() {
        on<ViewFeature>().with {
            bossOptions.onLinkClick = {
                when (it) {
                    "approve" -> approveCurrent(true)
                    "unapprove" -> approveCurrent(false)
                    "resolve" -> resolveCurrent()
                    "remove" -> removeCurrent()
                }
            }

            on<State>().observe(State.Area.Person) {
                if (on<LayoutFeature>().isBoss) {
                    currentReport()?.let { report ->
                        bossOptions.visible = true
                        bossOptions.setBackgroundResource(R.drawable.red_rounded_2dp)
                        bossOptions.text = "(${index + 1}/${reports.size}) Reported for \"${report.report}\"" +
                                "${if (person.current!!.approved) " <tap data=\"unapprove\">Unapprove</tap> ${person.current?.name}, <tap data=\"remove\">Remove</tap> this profile, or <tap data=\"resolve\">Resolve</tap> this report." else ". ${person.current!!.name} is currently unapproved. <tap data=\"remove\">Remove</tap> this profile, or <tap data=\"resolve\">Resolve</tap> this report."}"
                    } ?: run {
                        val needsApproval = person.current?.approved?.not() ?: false
                        bossOptions.visible = needsApproval

                        if (needsApproval) {
                            bossOptions.setBackgroundResource(R.drawable.green_rounded_2dp)
                            bossOptions.text = "<tap data=\"approve\">Approve</tap> ${person.current!!.name}, or <tap data=\"unapprove\">Unapprove</tap> with a comment"
                        }
                    }
                } else {
                    bossOptions.visible = false
                }
            }
        }
    }

    fun showApprovals() {
        reports = listOf()

        on<Api>().bossApprovals {
            if (it.isEmpty()) {
                on<PeopleFeature>().reload()
                on<State>().ui = on<State>().ui.copy(showFeed = true, showEditProfile = false)
            } else {
                on<PeopleFeature>().show(it)
                on<Timer>().post(Runnable {
                    on<StoryFeature>().event(StoryEvent.Start)
                    on<State>().ui = on<State>().ui.copy(showFeed = false)
                })
            }
        }
    }

    fun showReports() {
        on<Api>().bossReports {
            reports = it
            index = -1
            on<StoryFeature>().personNavigationListener = personNavigationListener
            on<PeopleFeature>().show(listOf())
            on<State>().ui = on<State>().ui.copy(showFeed = false)
            showNextReport()
        }
    }

    private fun resolveCurrent() {
        on<Api>().bossResolveReport(BossReportRequest(reports[index].id)) {
            on<Say>().say("Report has been resolved")
            showNextReport()
        }
    }

    private fun approveCurrent(approve: Boolean) {
        on<State>().person.current?.let { person ->
            person.id ?: return@let

            if (approve) {
                on<Api>().bossApprove(BossApproveRequest(person.id!!, approve = true)) {
                    on<Say>().say("${person.name} has been approved")
                    next(person.id!!)
                }
            } else {
                on<EditorFeature>().open(prefix = "Comments for ${person.name}: ") { reason ->
                    on<Api>().bossApprove(
                        BossApproveRequest(
                            person.id!!,
                            approve = false,
                            message = reason
                        )
                    ) {
                        on<Say>().say("${person.name} has been unapproved")
                        next(person.id!!)
                    }
                }
            }
        }
    }

    private fun removeCurrent() {
        on<State>().person.current?.let { person ->
            person.id ?: return@let

            on<ViewFeature>().with {
                on<LayoutFeature>().canCloseFullscreenModal = true
                fullscreenMessageText.text = "Completely remove this profile?<br /><br /><tap data=\"confirm\">Confirm</tap> or <tap data=\"no\">No way</tap>"
                fullscreenMessageLayout.visible = true
                fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

                fullscreenMessageText.onLinkClick = {
                    when (it) {
                        "confirm" -> {
                            on<Api>().bossRemoveProfile(BossRemoveProfileRequest(person.id!!)) {
                                if (it.success) {
                                    on<Say>().say("Profile removed")
                                }
                            }
                        }
                    }

                    fullscreenMessageLayout.visible = false
                }
            }
        }
    }

    private fun next(person: String) {
        if (currentReport() == null) {
            on<PeopleFeature>().remove(person)
        } else {
            showNextReport()
        }
    }

    private fun currentReport(): Report? {
        return if (index >= 0 && index < reports.size && reports[index].person == on<State>().person.current?.id) {
            reports[index]
        } else null
    }

    private fun showNextReport(): Boolean {
        index++

        if (index >= 0 && index < reports.size) {
            on<PeopleFeature>().show(reports[index].person)
            on<Timer>().post(Runnable {
                on<StoryFeature>().event(StoryEvent.Start)
            })
            return true
        }

        on<PeopleFeature>().reload()
        on<State>().ui = on<State>().ui.copy(showFeed = true, showEditProfile = false)

        return false
    }
}