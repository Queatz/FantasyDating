package com.queatz.fantasydating.features

import android.animation.Animator
import android.view.View
import android.view.ViewGroup
import com.queatz.fantasydating.*
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fullscreen_modal.*

class WalkthroughFeature constructor(private val on: On) {

    private val anyBubIsVisible: Boolean get() = on<ViewFeature>().with {
        bub1?.visible ?: false ||
        bub2?.visible ?: false ||
        bub3?.visible ?: false ||
        bub4?.visible ?: false ||
        bub5?.visible ?: false ||
        bub6?.visible ?: false
    }

    fun start() {
        showWelcomeModal()
    }

    fun showBub(view: View?) = view?.apply {
        if (step(id.toString())?.shown == true) {
            return this
        }

        if (anyBubIsVisible) return@apply

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
        if (on<MyProfileFeature>().myProfile.invited.not()) {
            on<ViewFeature>().with {
                on<LayoutFeature>().canCloseFullscreenModal = false
                fullscreenMessageText.setText(R.string.welcome_modal_scan_invite)
                fullscreenMessageLayout.visible = true
                fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

                fullscreenMessageText.onLinkClick = {
                    on<ScanQrCodeFeature>().scan {
                        fullscreenMessageLayout.visible = false
                    }
                }
            }

            return
        }

        if (on<MyProfileFeature>().myProfile.sex.isNotBlank()) {
            on<ViewFeature>().with {

                showBub(bub2)
                showBub(bub1)

                on<State>().person.current?.let {
                    bub3?.text = getString(R.string.bub3, it.name)
                    showBub(bub3)
                }
            }

            return
        }

        on<ViewFeature>().with {
            on<LayoutFeature>().canCloseFullscreenModal = false
            fullscreenMessageText.setText(R.string.welcome_modal_message)
            fullscreenMessageLayout.visible = true
            fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

            fullscreenMessageText.onLinkClick = {
                on<MyProfileFeature>().edit { sex = it }

                fullscreenMessageLayout.visible = false

                showBub(bub2)
                showBub(bub1)

                on<State>().person.current?.let {
                    bub3?.text = getString(R.string.bub3, it.name)
                    showBub(bub3)
                }
            }
        }
    }
}