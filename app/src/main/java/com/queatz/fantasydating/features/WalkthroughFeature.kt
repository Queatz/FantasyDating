package com.queatz.fantasydating.features

import android.animation.Animator
import android.view.View
import android.view.ViewGroup
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class WalkthroughFeature constructor(private val on: On) {

    fun start() {
        showWelcomeModal()
    }

    fun showBub(view: View) = view.apply {
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

    private fun showWelcomeModal() {
        on<ViewFeature>().with {
            welcomeMessageLayout.visible = true

            welcomeMessageText.onLinkClick = {
                on<MyProfileFeature>().myProfile.sex = it

                welcomeMessageLayout.visible = false

                showBub(bub1)
                showBub(bub2)
                showBub(bub3)
            }
        }
    }
}