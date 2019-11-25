package com.queatz.fantasydating.features

import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.queatz.fantasydating.State
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

class GesturesFeature constructor(private val on: On) {

    private lateinit var gestureDetector: GestureDetectorCompat

    var storyNavigationListener = object : InteractionListener {
        override fun click(gravity: Int) {
            if (on<State>().person.current == null) {
                return
            }

            if (on<State>().ui.showFantasy) {
                on<State> {
                    ui = ui.copy(showFantasy = false)
                }
                return
            }

            when (gravity) {
                Gravity.START -> {
                    on<State> {
                        ui = ui.copy(showFeed = false)
                    }
                    on<StoryFeature>().event(StoryEvent.Previous)
                }
                Gravity.END -> {
                    if (on<State>().ui.showFeed && on<State>().ui.showEditProfile.not()) {
                        on<StoryFeature>().event(StoryEvent.Start)

                        on<ViewFeature>().with {
                            on<WalkthroughFeature>().closeBub(bub1)
                            on<WalkthroughFeature>().showBub(bub6)
                        }
                    } else {
                        on<StoryFeature>().event(StoryEvent.Next)
                    }

                    on<State> {
                        ui = ui.copy(showFeed = false)
                    }
                }
            }
        }

        override fun fling(velocityX: Float, velocityY: Float): Boolean {
            if (on<State>().person.current == null) {
                return false
            }

            if (abs(velocityY) < abs(velocityX)) {
                if (on<State>().ui.showFantasy.not()) {
                    if (velocityX > 0) {
                        on<StoryFeature>().event(StoryEvent.Previous)
                    } else {
                        on<StoryFeature>().event(if (on<State>().ui.showFeed) StoryEvent.Start else StoryEvent.Next)
                    }

                    on<State> {
                        ui = ui.copy(showFeed = false)
                    }

                    return true
                }

                return false
            }

            if (velocityY > 0 && on<State>().ui.showFantasy.not()) {
                on<WalkthroughFeature>().closeBub(on<ViewFeature>().with { bub6 })

                on<State> {
                    ui = ui.copy(showFeed = true)
                }
            }

            on<State> {
                ui = ui.copy(showFantasy = velocityY < 0)
            }

            return true
        }

        override fun event(event: MotionEvent) {
            if (on<State>().person.current == null) {
                return
            }

            if (on<State>().ui.showFantasy.not()) when (event.action) {
                MotionEvent.ACTION_DOWN -> on<StoryFeature>().event(StoryEvent.Pause)
                MotionEvent.ACTION_UP -> on<StoryFeature>().event(StoryEvent.Resume)
            }
        }
    }

    var listener: InteractionListener = storyNavigationListener


    fun start() {
        val gestures = object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ) = listener.fling(velocityX, velocityY)

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ) = listener.scroll(distanceX, distanceY, e2)
        }

        on<ViewFeature>().with {
            gestureDetector = GestureDetectorCompat(this, gestures)

            View.OnTouchListener { _, event ->
                listener.event(event)

                gestureDetector.onTouchEvent(event)
            }.let {
                leftTouchTarget.setOnTouchListener(it)
                rightTouchTarget.setOnTouchListener(it)
            }

            leftTouchTarget.setOnClickListener {
                listener.click(Gravity.START)
            }

            rightTouchTarget.setOnClickListener {
                listener.click(Gravity.END)
            }

            leftTouchTarget.setOnLongClickListener { true }
            rightTouchTarget.setOnLongClickListener { true }
        }
    }
}

interface InteractionListener {
    fun click(gravity: Int) {}
    fun fling(velocityX: Float, velocityY: Float): Boolean = false
    fun scroll(distanceX: Float, distanceY: Float, event: MotionEvent): Boolean = false
    fun event(event: MotionEvent) = Unit
}
