package com.queatz.fantasydating

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlin.math.max

class StoryProgress : View {

    var count: Int = 3
    var barPadding: Float = 0f

    var currentObservable = BehaviorSubject.createDefault(0)
    var exitObservable = PublishSubject.create<Int>()

    private var theme: Resources.Theme? = null
    private var current: Int = 0
    private var currentProgress: Float = 0f
    private var animator: ValueAnimator? = null
    private val paint = Paint()
    private val rect = RectF()
    private val listener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animator: Animator) {}
        override fun onAnimationCancel(animator: Animator) {
            (animator as ValueAnimator).removeAllUpdateListeners()
            animator.removeAllListeners()
        }
        override fun onAnimationStart(animator: Animator) {}
        override fun onAnimationEnd(animator: Animator) {
            next()
        }
    }

    constructor(context: Context) : super(context) { initialize(context) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize(context) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initialize(context) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { initialize(context) }

    fun start() {
        current = -1
        next()
    }

    fun pause() {
        animator?.pause()
    }

    fun resume() {
        animator?.resume()
    }

    fun previous() {
        current -= 2
        next()
    }

    fun set(bar: Int) {
        animator?.cancel()
        current = bar
        currentProgress = 0f
        currentObservable.onNext(current)
        invalidate()
    }

    fun isPaused() = animator?.isPaused ?: true

    fun next() {
        current += 1
        currentProgress = 0f
        animator?.cancel()
        animator = null

        if (current >= count) {
            current = count - 1
            currentProgress = 1f
            invalidate()
            exitObservable.onNext(1)
            return
        } else if (current < 0) {
            current = 0
            currentProgress = 0f
            invalidate()
            exitObservable.onNext(-1)
            return
        }

        currentObservable.onNext(current)

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 7000
            interpolator = LinearInterpolator()
            addUpdateListener {
                currentProgress = it.animatedFraction
                invalidate()
            }
            addListener(listener)
            start()
        }
    }

    private fun initialize(context: Context) {
        theme = context.theme
        barPadding = resources.getDimensionPixelSize(R.dimen.dp) * 4f
    }

    override fun onDraw(canvas: Canvas) {

        val barWidth = (measuredWidth.toFloat() - barPadding * (count.toFloat() - 1f)) / count
        val barHeight= measuredHeight.toFloat()

        for (bar in 0 until count) {
            val x = (barWidth + barPadding) * bar

            if (bar >= current) {
                paint.style = Paint.Style.FILL
                paint.color = resources.getColor(R.color.story_background, theme)


                rect.set(
                    x,
                    0f,
                    x + barWidth,
                    barHeight
                )
                canvas.drawRoundRect(rect, barPadding, barPadding, paint)
            }

            if (bar <= current) {
                paint.style = Paint.Style.FILL
                paint.color = resources.getColor(R.color.white, theme)
                rect.set(
                    x,
                    0f,
                    x + max(barHeight / 2f, barWidth * (if (bar == current) currentProgress else 1f)),
                    barHeight
                )
                canvas.drawRoundRect(rect, barPadding, barPadding, paint)
            }
        }

        super.onDraw(canvas)
    }
}