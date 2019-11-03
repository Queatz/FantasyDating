package com.queatz.fantasydating.features

import com.queatz.fantasydating.R
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class MoreOptionsFeature constructor(private val on: On) {

    val isOpen: Boolean get() = on<ViewFeature>().with { moreOptionsText }.visible

    fun start() {
        on<ViewFeature>().with {
            moreOptionsText.onLinkClick = {
                moreOptionsText.visible = false
                on<StoryFeature>().event(StoryEvent.Resume)
            }

            moreOptionsButton.setOnClickListener {
                if (on<LayoutFeature>().showEditProfile.not()) {
                    moreOptionsText.setText(R.string.moreOptionsTemplate)
                } else {
                    moreOptionsText.setText(R.string.moreOptionsProfileTemplate)
                }

                moreOptionsText.visible = true
                on<StoryFeature>().event(StoryEvent.Pause)

                on<WalkthroughFeature>().closeBub(bub5)
            }
        }
    }

    fun close() {
        on<ViewFeature>().with { moreOptionsText }.visible = false
    }
}
