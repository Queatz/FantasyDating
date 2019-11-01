package com.queatz.fantasydating

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
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

    var fantasyShown = false
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        background.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark, theme))

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
                    0 -> "Emi, 23\n\nI love pretending I’m visiting Earth on an intergalactic mission."
                    1 -> "Sal, 27\n\nI love meeting new people in the rush of the city."
                    else -> "Liz, 29\n\nI love taking photos of myself in the pool!"
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
                    if (!fantasyShown) {
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

                if (velocityY > 0 && !fantasyShown) {
                    showFeed = true
                }

                fantasyShown = velocityY < 0

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
            if (!fantasyShown) when (event.action) {
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
            if (fantasyShown) {
                fantasyShown = false
                return@setOnClickListener
            }

            showFeed = false
            stories.previous()
        }

        rightTouchTarget.setOnClickListener {
            if (fantasyShown) {
                fantasyShown = false
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

        discoveryPreferencesText.setOnClickListener {
            showModal()
        }

//        zoom()
    }

    override fun onBackPressed() {
        if (fantasyShown) {
            fantasyShown = false
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

                positiveButton.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            positiveButton.paintFlags = positiveButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                        }
                        MotionEvent.ACTION_UP -> {
                            positiveButton.paintFlags = positiveButton.paintFlags xor Paint.UNDERLINE_TEXT_FLAG
                        }
                    }

                    false
                }

                positiveButton.setOnClickListener { dismiss() }

                messageTextView.movementMethod = LinkMovementMethod.getInstance()

                messageTextView.setTextAppearance(R.style.Text_Medium)
                messageTextView.setLineSpacing(0f, 1.5f)
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
}
