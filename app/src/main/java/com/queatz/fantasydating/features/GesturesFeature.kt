package com.queatz.fantasydating.features

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

class GesturesFeature constructor(private val on: On) {

    private lateinit var gestureDetector: GestureDetectorCompat

    fun start() {
        val gestures = object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (abs(velocityY) < abs(velocityX)) {
                    if (on<LayoutFeature>().showFantasy.not()) {
                        if (velocityX > 0) {
                            on<StoryFeature>().event(StoryEvent.Previous)
                        } else {
                            on<StoryFeature>().event(if (on<LayoutFeature>().showFeed) StoryEvent.Start else StoryEvent.Next)
                        }

                        on<LayoutFeature>().showFeed = false

                        return true
                    }

                    return false
                }

                if (velocityY > 0 && on<LayoutFeature>().showFantasy.not()) {
                    on<WalkthroughFeature>().closeBub(on<ViewFeature>().with { bub6 })
                    on<LayoutFeature>().showFeed = true
                }

                on<LayoutFeature>().showFantasy = velocityY < 0

                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ) = false
        }

        on<ViewFeature>().with {
            gestureDetector = GestureDetectorCompat(this, gestures)

            View.OnTouchListener { _, event ->
                if (on<LayoutFeature>().showFantasy.not()) when (event.action) {
                    MotionEvent.ACTION_DOWN -> on<StoryFeature>().event(StoryEvent.Pause)
                    MotionEvent.ACTION_UP -> on<StoryFeature>().event(StoryEvent.Resume)
                }

                gestureDetector.onTouchEvent(event)
            }.let {
                leftTouchTarget.setOnTouchListener(it)
                rightTouchTarget.setOnTouchListener(it)
            }

            leftTouchTarget.setOnClickListener {
                if (on<LayoutFeature>().showFantasy) {
                    on<LayoutFeature>().showFantasy = false
                    return@setOnClickListener
                }

                on<LayoutFeature>().showFeed = false
                on<StoryFeature>().event(StoryEvent.Previous)
            }

            rightTouchTarget.setOnClickListener {
                if (on<LayoutFeature>().showFantasy) {
                    on<LayoutFeature>().showFantasy = false
                    return@setOnClickListener
                }

                if (on<LayoutFeature>().showFeed) {
                    on<StoryFeature>().event(StoryEvent.Start)
                    on<WalkthroughFeature>().closeBub(bub1)
                    on<WalkthroughFeature>().showBub(bub6)
                } else {
                    on<StoryFeature>().event(StoryEvent.Next)
                }

                on<LayoutFeature>().showFeed = false
            }

            leftTouchTarget.setOnLongClickListener { true }
            rightTouchTarget.setOnLongClickListener { true }
        }
    }

}