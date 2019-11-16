package com.queatz.fantasydating.features

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.text.method.ScrollingMovementMethod
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import coil.Coil
import coil.api.load
import com.queatz.fantasydating.*
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class StoryFeature constructor(private val on: On) : OnLifecycle {

    private val disposables = CompositeDisposable()

    private var person: Person? = null
        set(value) {
            field = value

            if (on<LayoutFeature>().showFeed) {
                event(StoryEvent.Reset)
            } else {
                event(StoryEvent.Start)
            }

            on<ViewFeature>().with {
                fantasyTitle.text = "${value?.name ?: ""}'s Fantasy"
                fantasyText.text = value?.fantasy ?: ""
                fantasyText.scrollTo(0, 0)

                value?.stories?.apply { stories.count = size }
            }
        }

    fun event(event: StoryEvent) {
        on<ViewFeature>().with { stories }.apply {
            when (event) {
                StoryEvent.Pause -> pause()
                StoryEvent.Resume -> resume()
                StoryEvent.Previous -> previous()
                StoryEvent.Start -> start()
                StoryEvent.Next -> next()
                StoryEvent.Reset -> set(0)
            }
        }
    }

    fun start() {
        disposables.add(on<PeopleFeature>().current.subscribe {
            it?.let { person = it }
        })

        on<ViewFeature>().with {
            background.setBackgroundColor(getColor(R.color.colorPrimaryDark))

            fantasyText.movementMethod = ScrollingMovementMethod()

            loveButton.setOnClickListener {
                on<WalkthroughFeature>().closeBub(bub4)

                confirmLove.text = resources.getString(R.string.confirm_your_love, person!!.name)
                confirmLove.visible = confirmLove.visible.not()
                confirmLove.onLinkClick = {
                    person?.id?.let {
                        on<LayoutFeature>().showFantasy = false

                        on<Api>().person(it, PersonRequest(love = true)) {}
                    }
                }
            }

            disposables.add(stories.exitObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (stories.animate.not()) {
                        return@subscribe
                    }

                    if (it == 1) {
                        on<PeopleFeature>().nextPerson()
                    } else {
                        on<PeopleFeature>().previousPerson()
                    }
                })

            disposables.add(stories.currentObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    moreOptionsText.visible = false

                    if (on<LayoutFeature>().showEditProfile) {

                        stories.post { event(StoryEvent.Pause) }
                    }


                    person ?: return@subscribe

                    person?.apply {
                        if (it >= stories.size) {
                            return@subscribe
                        }

                        setPhoto(stories[it].photo)

                        if (on<LayoutFeature>().showEditProfile) {
                            on<EditProfileFeature>().updateMyStory()
                        } else {
                            storyText.text = "${name}, ${age}<br /><br />${stories[it].story}"
                        }

                        background.origin = PointF(stories[it].x, stories[it].y)
                    }
                })

            disposables.add(storyText.firstLineWidth
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    moreOptionsButton.apply {
                        (layoutParams as ViewGroup.MarginLayoutParams).leftMargin = it + resources.getDimensionPixelSize(R.dimen.dp) * 42
                        requestLayout()
                    }
                })
        }
    }

    fun setPhoto(photo: String) {
        on<ViewFeature>().with {
            stories.post { event(StoryEvent.Pause) }

            background.setImageDrawable(null)

            background.load("$photo?s=1600") {
                crossfade(true)
                listener { _, _ ->
                    stories.post { event(StoryEvent.Resume) }
                }
            }

            Coil.load(this, "$photo?s=1600") {
                allowHardware(false)
                target({}, {}, { drawable ->
                    val color = drawable.toBitmap(4, 4, Bitmap.Config.ARGB_8888)[0, 3]
                    val hsv = floatArrayOf(0f, 0f, 0f)
                    Color.colorToHSV(color, hsv)
                    hsv[2] = min(hsv[2], .333f)
                    val c = Color.HSVToColor(hsv)
                    storyText.setTextColor(c)
                    fantasy.setBackgroundColor(Color.argb(163, Color.red(c) / 4, Color.green(c) / 4, Color.blue(c) / 4))
                })
            }
        }
    }

    fun onBackPressed() = on<ViewFeature>().with {
        if (confirmLove.visible) {
            confirmLove.visible = false
            true
        } else false
    }

    override fun off() {
        disposables.dispose()
    }

    fun getCurrentStory() = on<ViewFeature>().activity.stories.currentObservable.value!!
}

enum class StoryEvent {
    Pause,
    Resume,
    Previous,
    Start,
    Next,
    Reset,
}
