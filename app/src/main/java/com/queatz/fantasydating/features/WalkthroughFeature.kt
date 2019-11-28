package com.queatz.fantasydating.features

import android.animation.Animator
import android.view.View
import android.view.ViewGroup
import com.queatz.fantasydating.R
import com.queatz.fantasydating.WalkthroughStep
import com.queatz.fantasydating.WalkthroughStep_
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class WalkthroughFeature constructor(private val on: On) {

    fun start() {
        showWelcomeModal()
    }

    fun showBub(view: View?) = view?.apply {
        if (step(view.id.toString())?.shown == true) {
            return this
        }

        setOnClickListener { closeBub(this) }

        alpha = 0f
        visible = true
        animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }

    fun closeBub(view: View?) {
        if (view?.visible?.not() != false) {
            return
        }

        shown(view.id.toString())

        view.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    (view.parent as ViewGroup?)?.removeView(view)
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }
            })
            .start()
    }

    private fun step(step: String) = on<StoreFeature>().get(WalkthroughStep::class).query()
        .equal(WalkthroughStep_.step, step)
        .build()
        .findFirst()

    private fun shown(step: String) {
        on<StoreFeature>().get(WalkthroughStep::class).put(step(step)?.also { it.shown = true } ?: WalkthroughStep(
            step,
            true
        ))
    }

    private fun showWelcomeModal() {
        if (on<MyProfileFeature>().myProfile.sex.isNotBlank()) {
            on<ViewFeature>().with {

                showBub(bub1)
                showBub(bub2)
                showBub(bub3)
            }
            return
        }

        on<ViewFeature>().with {
            on<LayoutFeature>().canCloseFullscreenModal = false
            fullscreenMessageText.setText(R.string.welcome_modal_message)
            fullscreenMessageLayout.visible = true

            fullscreenMessageText.onLinkClick = {
                on<MyProfileFeature>().edit { sex = it }

                fullscreenMessageLayout.visible = false

                showBub(bub1)
                showBub(bub2)
                showBub(bub3)
            }
        }
    }
}