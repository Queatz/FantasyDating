package com.queatz.fantasydating.features

import androidx.core.widget.addTextChangedListener
import com.queatz.fantasydating.*
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

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

                if (on<LayoutFeature>().showEditProfile.not()) {
                    moreOptionsText.text = getString(R.string.moreOptionsTemplate, on<PeopleFeature>().current.value!!.name)
                } else {
                    moreOptionsText.setText(R.string.moreOptionsProfileTemplate)
                }

                moreOptionsText.visible = true
                on<StoryFeature>().event(StoryEvent.Pause)

                on<WalkthroughFeature>().closeBub(bub5)

                moreOptionsButton.handler.removeCallbacks(closeCallback)
            }

            moreOptionsText.onLinkClick = {
                val person = on<PeopleFeature>().current.value!!

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
                        fullscreenMessageText.setText(R.string.moreOptionsProfileConfirmTemplate)
                        fullscreenMessageLayout.visible = true

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

    private fun report(reason: String) {
        val person = on<PeopleFeature>().current.value!!

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
