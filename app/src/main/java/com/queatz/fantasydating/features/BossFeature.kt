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
                }
            }

            on<State>().observe(State.Area.Person) {
                if (on<LayoutFeature>().isBoss) {
                    currentReport()?.let { report ->
                        bossOptions.visible = true
                        bossOptions.setBackgroundResource(R.drawable.red_rounded_2dp)
                        bossOptions.text = "(${index + 1}/${reports.size}) Reported for \"${report.report}\"${if (person.current!!.approved) " <tap data=\"unapprove\">Unapprove</tap>" else ". ${person.current!!.name} is currently unapproved."}"
                    } ?: run {
                        val needsApproval = person.current?.approved?.not() ?: false
                        bossOptions.visible = needsApproval

                        if (needsApproval) {
                            bossOptions.setBackgroundResource(R.drawable.green_rounded_2dp)
                            bossOptions.text = "<tap data=\"approve\">Approve</tap> ${person.current!!.name}"
                        }
                    }
                } else {
                    bossOptions.visible = false
                }
            }
        }
    }

    fun showApprovals() {
        on<Api>().bossApprovals {
            on<PeopleFeature>().show(it)
            on<Timer>().post(Runnable {
                on<StoryFeature>().event(StoryEvent.Start)
                on<State>().ui = on<State>().ui.copy(showFeed = false)
            })
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

    private fun approveCurrent(approve: Boolean) {
        on<State>().person.current?.let { person ->
            if (approve) {
                on<Api>().bossApprove(BossApproveRequest(person.id!!, approve = true)) {
                    on<Say>().say("${person.name} has been approved")
                    on<PeopleFeature>().nextPerson()
                }
            } else {
                on<EditorFeature>().open(prefix = "Comments for ${person.name}: ") { reason ->
                    on<Api>().bossApprove(BossApproveRequest(person.id!!, approve = false, message = reason)) {
                        on<Say>().say("${person.name} has been unapproved")
                        showNextReport()
                    }
                }
            }
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

        return false
    }
}