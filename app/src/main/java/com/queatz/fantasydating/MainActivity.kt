package com.queatz.fantasydating

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import coil.Coil
import coil.api.load
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import kotlin.math.sin


class MainActivity : AppCompatActivity() {

    var sex = ""
    val discoveryPreferences = DiscoveryPreferences("Girls", "Austin", 25, 35)

    var showFantasy = false
        set(value) {
            field = value

            if (value) {
                swipeUpArrow.rotation = 180f
                fantasy.visibility = View.VISIBLE
                storyText.visibility = View.GONE
                stories.pause()
            } else {
                swipeUpArrow.rotation = 0f
                fantasy.visibility = View.GONE
                storyText.visibility = View.VISIBLE
                stories.resume()
            }
        }

    var showFeed = true
        set(value) {
            field = value

            discoveryPreferencesText.visibility = if (value) View.VISIBLE else View.GONE
            feedGroup.visibility = if (value) View.VISIBLE else View.GONE

            if (value) {
                stories.set(0)
            }
        }

    var showDiscoveryPreferences = false
        set(value) {
            field = value

            discoveryPreferencesLayout.visibility = if (value) View.VISIBLE else View.GONE
            showFeed = !value

            if (!value) {
                editPreferenceText.visibility = View.GONE
                editProfileText.visibility = View.VISIBLE
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        background.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark, theme))

        notification1.text = resources.getString(R.string.link, "YOU MATCHED WITH AMY")
        notification2.text = resources.getString(R.string.link, "JING SENT YOU 3 MESSAGES")
        notification3.text = resources.getString(R.string.link, "YOU MATCHED WITH JING")
        notification4.text = resources.getString(R.string.link, "WELCOME TO FANTASY DATING")

        stories.exitObservable
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe { stories.start() }

        Coil.load(this, "https://lh3.googleusercontent.com/80hJcieOULQhfT2hLS689_tNOCACzpilOYjTMvgw8aHH12Nk4hj7eTCsFdWY4lcC8laMoSAk8YIshlWMxRHELXYBE3UtDtWCK1_1uXFotpeUKn_D2AA0ZMcpQDwML8rBgDjMmFjaeW8")
        Coil.load(this, "https://lh3.googleusercontent.com/LE1DiS1zLJ-kdc639XxdC89NtNjBl3v8M7a2A_KX-8PaINqGvruYkcAnxOYu6Pbaa9asAdT75nHniyMUZRMrlSMoV0hH374dJlRdWzXhagh6ywZKvBZILyFEQnFLsnHLgIXQklUtcFo")
        Coil.load(this, "https://lh6.googleusercontent.com/7HwiyGQgVsnaPSi0KDp_IE-UqKaDmZIlQQqKyJXAXHwiyVoYfkQfQwbCMGMf3nHhC3sIKzDIaSJq2-Aod9RErfPZlCVoLrHU4kAt3K7rwPXbKPW7Js4FmY4KXoIwTt4asEm6bBGvBkk")

        stories.currentObservable
            .subscribe {
                background.load(
                    when (it) {
                        0 -> "https://lh3.googleusercontent.com/80hJcieOULQhfT2hLS689_tNOCACzpilOYjTMvgw8aHH12Nk4hj7eTCsFdWY4lcC8laMoSAk8YIshlWMxRHELXYBE3UtDtWCK1_1uXFotpeUKn_D2AA0ZMcpQDwML8rBgDjMmFjaeW8"
                        1 -> "https://lh3.googleusercontent.com/LE1DiS1zLJ-kdc639XxdC89NtNjBl3v8M7a2A_KX-8PaINqGvruYkcAnxOYu6Pbaa9asAdT75nHniyMUZRMrlSMoV0hH374dJlRdWzXhagh6ywZKvBZILyFEQnFLsnHLgIXQklUtcFo"
                        else -> "https://lh6.googleusercontent.com/7HwiyGQgVsnaPSi0KDp_IE-UqKaDmZIlQQqKyJXAXHwiyVoYfkQfQwbCMGMf3nHhC3sIKzDIaSJq2-Aod9RErfPZlCVoLrHU4kAt3K7rwPXbKPW7Js4FmY4KXoIwTt4asEm6bBGvBkk"
                    }
                )

                storyText.text = when (it) {
                    0 -> "Emi, 23<br /><br />I love pretending I’m visiting Earth on an intergalactic mission."
                    1 -> "Sal, 27<br /><br />I love meeting new people in the rush of the city."
                    else -> "Liz, 29<br /><br />I love taking photos of myself in the pool!"
                }
            }

        val gestures = object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (abs(velocityY) < abs(velocityX)) {
                    if (!showFantasy) {
                        if (velocityX > 0) {
                            stories.previous()
                        } else {
                            if (showFeed) {
                                stories.start()
                            } else {
                                stories.next()
                            }
                        }

                        showFeed = false

                        return true
                    }

                    return false
                }

                if (velocityY > 0 && !showFantasy) {
                    showFeed = true
                }

                showFantasy = velocityY < 0

                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        }

        val gestureDetector = GestureDetectorCompat(this, gestures)

        val touchListener = View.OnTouchListener { _, event ->
            if (!showFantasy) when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    stories.pause()
                }
                MotionEvent.ACTION_UP -> {
                    stories.resume()
                }
            }

            gestureDetector.onTouchEvent(event)
        }

        leftTouchTarget.setOnTouchListener(touchListener)
        rightTouchTarget.setOnTouchListener(touchListener)

        leftTouchTarget.setOnClickListener {
            if (showFantasy) {
                showFantasy = false
                return@setOnClickListener
            }

            showFeed = false
            stories.previous()
        }

        rightTouchTarget.setOnClickListener {
            if (showFantasy) {
                showFantasy = false
                return@setOnClickListener
            }

            if (showFeed) {
                stories.start()
            } else {
                stories.next()
            }

            showFeed = false
        }

        leftTouchTarget.setOnLongClickListener { true }
        rightTouchTarget.setOnLongClickListener { true }

        discoveryPreferencesText.onLinkClick = {
            showDiscoveryPreferences = true
        }

        discoveryPreferencesLayout.setOnClickListener {
            showDiscoveryPreferences = false
        }

        editPreferenceText.onLinkClick = {
            when {
                it == "nope" -> showAustinOnly()
                setOf("Girls", "Boys", "People").contains(it) -> discoveryPreferences.who = it
                it == "Austin" -> discoveryPreferences.where = it
                it.startsWith("min:") -> discoveryPreferences.ageMin = it.split(":")[1].toInt()
                it.startsWith("max:") -> discoveryPreferences.ageMax = it.split(":")[1].toInt()
            }

            editPreferenceText.visibility = View.GONE
            editProfileText.visibility = View.VISIBLE

            updateDiscoveryPreferences()
        }

        editDiscoveryPreferencesText.onLinkClick = {
            when (it) {
                "who" -> {
                    editPreferenceText.text = resources.getString(R.string.edit_who)
                }
                "where" -> {
                    editPreferenceText.text = resources.getString(R.string.edit_where)
                }
                "ageMin" -> {
                    editPreferenceText.text = listOf(18, 20, 22, 24, 26, 28, 30, 35, 40, 45, 50)
                        .filter { it < discoveryPreferences.ageMax }
                        .map { "<tap data=\"min:${it}\">${it}</tap>" }
                        .joinToString(" &nbsp;&nbsp; ")
                }
                "ageMax" -> {
                    editPreferenceText.text = listOf(18, 20, 22, 24, 26, 28, 30, 35, 40, 45, 50, 1000)
                        .filter { it > discoveryPreferences.ageMin }
                        .map { "<tap data=\"max:${it}\">${if (it == 1000) "Any" else it.toString()}</tap>" }
                        .joinToString(" &nbsp;&nbsp; ")
                }
            }

            editPreferenceText.visibility = View.VISIBLE
            editProfileText.visibility = View.GONE

            updateDiscoveryPreferences()
        }

        editProfileText.onLinkClick = {
            showFantasy = false
            showDiscoveryPreferences = false
            showFeed = false
            stories.start()
        }

        updateDiscoveryPreferences()

        showWelcomeModal()
    }

    private fun showWelcomeModal() {
        welcomeMessageLayout.visibility = View.VISIBLE
        welcomeMessageText.onLinkClick = {
            sex = it
            welcomeMessageLayout.visibility = View.GONE

            bub1.visibility = View.VISIBLE
            bub2.visibility = View.VISIBLE

            listOf(bub1, bub2).forEach {
                it.alpha = 0f
                it.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
        }
    }

    private fun showAustinOnly() {
        showModal()
    }

    private fun updateDiscoveryPreferences() {
        editDiscoveryPreferencesText.text = resources.getString(R.string.discovery_preferences_template,
            discoveryPreferences.who,
            discoveryPreferences.where,
            discoveryPreferences.ageMin.toString(),
            discoveryPreferences.ageMax.let { if (it == 1000) "Any" else it.toString() }
        )

        discoveryPreferencesText.text = resources.getString(R.string.discovery_preferences,
            discoveryPreferences.who,
            discoveryPreferences.where,
            discoveryPreferences.ageMin.toString(),
            discoveryPreferences.ageMax.let { if (it == 1000) "Any" else it.toString() }
        )
    }

    override fun onBackPressed() {
        if (showDiscoveryPreferences) {
            showDiscoveryPreferences = false
        } else if (showFantasy) {
            showFantasy = false
        } else if (!showFeed) {
            showFeed = true
        } else {
            super.onBackPressed()
        }
    }

    private fun showModal() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setMessage(getString(R.string.location_modal_message))
            .setPositiveButton(getString(R.string.enable_location)) { _, _ ->

            }
            .show().apply {
                val messageTextView = window?.findViewById(android.R.id.message) as TextView
                val positiveButton = window?.findViewById(android.R.id.button1) as Button

                val pad = resources.getDimensionPixelSize(R.dimen.pad)

                positiveButton.setBackgroundResource(android.R.color.transparent)
                positiveButton.setPaddingRelative(pad, 0, pad, 0)

                positiveButton.setOnClickListener { dismiss() }

                messageTextView.setTextAppearance(R.style.Text_Medium)
                messageTextView.setLineSpacing(0f, 1.4f)
            }
    }

    private fun zoom() {
        val x = 0.5f + 0.5f * sin((System.currentTimeMillis().toDouble() % 30000) / 30000.0 * Math.PI * 2).toFloat()
        background.origin = PointF(x, x)
        background.scale = 1 + 1 - x

        background.postDelayed({
            zoom()
        }, 5)
    }

    fun closeBub(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    (view.parent as ViewGroup).removeView(view)
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }
            })
            .start()
    }
}
