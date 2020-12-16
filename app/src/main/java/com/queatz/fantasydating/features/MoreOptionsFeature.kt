package com.queatz.fantasydating.features

import androidx.core.widget.addTextChangedListener
import com.queatz.fantasydating.*
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fullscreen_modal.*

class MoreOptionsFeature constructor(private val on: On) {

    val isOpen: Boolean get() = on<ViewFeature>().with { moreOptionsText }.visible

    private val closeCallback = Runnable { close() }

    fun start() {
        on<ViewFeature>().with {
            moreOptionsText.addTextChangedListener {
                timeout()
            }

            moreOptionsButton.setOnClickListener {
                timeout()

                if (on<State>().ui.showEditProfile.not()) {
                    moreOptionsText.text = getString(R.string.moreOptionsTemplate, on<State>().person.current?.name ?: "")
                } else {
                    moreOptionsText.setText(R.string.moreOptionsProfileTemplate)
                }

                moreOptionsText.visible = true
                on<StoryFeature>().event(StoryEvent.Pause)

                on<WalkthroughFeature>().closeBub(bub5)

                moreOptionsButton.handler.removeCallbacks(closeCallback)
            }

            moreOptionsText.onLinkClick = {
                on<State>().person.current?.let { person ->
                    when (it) {
                        "hide" -> {
                            moreOptionsText.text = getString(R.string.moreOptionsHideConfirmTemplate, person.name)
                        }
                        "hide:confirm" -> {
                            on<PeopleFeature>().hide(person.id!!)
                            close()
                        }
                        "report" -> {
                            moreOptionsText.text = getString(R.string.moreOptionsReportConfirmTemplate, person.name)
                        }
                        "report:confirm:fake" -> {
                            close()
                            report("Fake account")
                        }
                        "report:confirm:other" -> {
                            close()
                            report("Other reason")
                        }
                        "deleteMyAccount" -> {
                            close()
                            on<LayoutFeature>().canCloseFullscreenModal = true
                            fullscreenMessageText.setText(R.string.moreOptionsProfileConfirmTemplate)
                            fullscreenMessageLayout.visible = true
                            fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

                            fullscreenMessageText.onLinkClick = {
                                when (it) {
                                    "deleteMyAccount:confirm" -> {
                                        on<Api>().deleteMe {
                                            on<StoreFeature>().clear()
                                            on<Say>().say("Byeeeee")
                                            finish()
                                        }
                                    }
                                }

                                fullscreenMessageLayout.visible = false
                            }
                        }
                    }
                }
            }
        }
    }

    private fun report(reason: String) {
        val person = on<State>().person.current!!

        on<PeopleFeature>().report(person, reason)
    }

    private fun timeout() {
        on<ViewFeature>().with {
            on<Timer>().remove(closeCallback)
            on<Timer>().post(closeCallback, 7000)
        }
    }

    fun close() {
        on<StoryFeature>().event(StoryEvent.Resume)
        on<ViewFeature>().with { on<Timer>().remove(closeCallback) }
        on<ViewFeature>().with { moreOptionsText }.visible = false
    }
}
