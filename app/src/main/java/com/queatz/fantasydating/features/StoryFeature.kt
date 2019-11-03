package com.queatz.fantasydating.features

import android.graphics.Bitmap
import android.graphics.Color
import android.text.method.ScrollingMovementMethod
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import coil.Coil
import coil.api.load
import com.queatz.fantasydating.R
import com.queatz.fantasydating.visible
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class StoryFeature constructor(private val on: On) : OnLifecycle {

    private val disposables = CompositeDisposable()

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
        on<ViewFeature>().with {
            background.setBackgroundColor(getColor(R.color.colorPrimaryDark))

            fantasyText.movementMethod = ScrollingMovementMethod()

            loveButton.setOnClickListener {
                on<LayoutFeature>().showFantasy = false
                on<WalkthroughFeature>().closeBub(bub4)
            }

            preload()

            disposables.add(stories.exitObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == 1) {
                        stories.start()
                    } else {
                        stories.set(1)
                        stories.next()
                    }
                })

            disposables.add(stories.currentObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (on<LayoutFeature>().showEditProfile) {
                        stories.post { stories.pause() }
                    }

                    val photo = when (it) {
                        0 -> "https://lh3.googleusercontent.com/80hJcieOULQhfT2hLS689_tNOCACzpilOYjTMvgw8aHH12Nk4hj7eTCsFdWY4lcC8laMoSAk8YIshlWMxRHELXYBE3UtDtWCK1_1uXFotpeUKn_D2AA0ZMcpQDwML8rBgDjMmFjaeW8"
                        1 -> "https://lh3.googleusercontent.com/LE1DiS1zLJ-kdc639XxdC89NtNjBl3v8M7a2A_KX-8PaINqGvruYkcAnxOYu6Pbaa9asAdT75nHniyMUZRMrlSMoV0hH374dJlRdWzXhagh6ywZKvBZILyFEQnFLsnHLgIXQklUtcFo"
                        2 -> "https://lh4.googleusercontent.com/luVnq4laYiTHUcQG3mEWirhNy6mJJ_aSTlYWcKHTfDFpQJOCKALKFUgJjJEQWeeadVtjv663soNxDSfm29Awgr2eYMDyDuHwMGUIOKho6zHMK-90FGR3Bs4ZMZYMSGmTk3Al58jcyKI"
                        3 -> "https://lh6.googleusercontent.com/7HwiyGQgVsnaPSi0KDp_IE-UqKaDmZIlQQqKyJXAXHwiyVoYfkQfQwbCMGMf3nHhC3sIKzDIaSJq2-Aod9RErfPZlCVoLrHU4kAt3K7rwPXbKPW7Js4FmY4KXoIwTt4asEm6bBGvBkk"
                        4 -> "https://lh3.googleusercontent.com/c6PnVBA7rKB_ofPEmcRXEQfNste2x5C3M5rmC1dlUUnoqHLlHg4R8wNZAS7LKFSBmL9f-5lB85oPwlBQ7Ib9aXTxHnHS9iBwdCkMQhTk4rgMsaKQyfObPg70xGB2_kH9GM8A8BOereo"
                        5 -> "https://lh3.googleusercontent.com/F7BDjvSURaJuIEYUZ3ikrK6rdvqXjYmZvPrFoyyNpoHOAVOlzcheVO9WFCB3bZgkcvuhNxEQdKFiC-SGKOIDPdc8rPmHrTZFc6OovxKiM9VMcx93Bdf6kKMaZxFmVobxxZyXSh3kqfI"
                        6 -> "https://lh5.googleusercontent.com/ecnggpWNeMRffzCDrEugrJqbolVoOXyd_jyHSRtuezPI461GIxdOXF6_e2yGE5Yf9BWf5QeSFwtaw8fsWdn8uKdyOtCZQDh7N86bNL5yXs7XsYHNJjpxUJoCsjdF2I1cTpBTwJuKLLs"
                        else -> "https://lh5.googleusercontent.com/Z7HKvSPvB-yj3xqvu1W8pOaHhzwS0uFVw7l6OoqDmMpBb4FZOZVkKCIFxB2T2mELkkQOXkfO4nAafk06-yGAO_zk22SLyTlgxW4RZAUjwphApCxu2i1CPbdXyG9ojVAa94yYkf3jwjA"
                    }

                    background.load(photo)

                    Coil.load(this, photo) {
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

                    storyText.text = when (it) {
                        0 -> "Emi, 23<br /><br />I love pretending I’m visiting Earth on an intergalactic mission."
                        1 -> "Sal, 27<br /><br />I love meeting new people in the rush of the city."
                        2 -> "Liz, 29<br /><br />I love taking photos of myself in the pool!"
                        3 -> "Mary, 24<br /><br /> love going out for a run in the cool evening."
                        else -> "Sal, 27<br /><br />I love meeting new people out in the countryside."
                    }

                    fantasyText.text = when (it) {
                        0 -> "I want a boy to masturbate and kiss me on a bench overlooking the lake.\n\nEmi’s Fantasy\n\nI want a boy to masturbate and kiss me on a bench overlooking the lake.\n\nEmi’s Fantasy\n\nI want a boy to masturbate and kiss me on a bench overlooking the lake.\n\nEmi’s Fantasy\n\nI want a boy to masturbate and kiss me on a bench overlooking the lake.\n\nEmi’s Fantasy\n\nI want a boy to masturbate and kiss me on a bench overlooking the lake.\n\nEmi’s Fantasy\n\nI want a boy to masturbate and kiss me on a bench overlooking the lake.\n\nEmi’s Fantasy\n\nI want a boy to masturbate and kiss me on a bench overlooking the lake.\n\nEmi’s Fantasy\n\nI want a boy to masturbate and kiss me on a bench overlooking the lake.\n\n"
                        else -> "I want a boy to masturbate and kiss me on a bench overlooking the lake."
                    }

                    fantasyText.scrollTo(0, 0)

                    moreOptionsText.visible = false
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

    private fun preload() {
        on<ViewFeature>().with {
            Coil.load(this, "https://lh3.googleusercontent.com/80hJcieOULQhfT2hLS689_tNOCACzpilOYjTMvgw8aHH12Nk4hj7eTCsFdWY4lcC8laMoSAk8YIshlWMxRHELXYBE3UtDtWCK1_1uXFotpeUKn_D2AA0ZMcpQDwML8rBgDjMmFjaeW8")
            Coil.load(this, "https://lh3.googleusercontent.com/LE1DiS1zLJ-kdc639XxdC89NtNjBl3v8M7a2A_KX-8PaINqGvruYkcAnxOYu6Pbaa9asAdT75nHniyMUZRMrlSMoV0hH374dJlRdWzXhagh6ywZKvBZILyFEQnFLsnHLgIXQklUtcFo")
            Coil.load(this, "https://lh4.googleusercontent.com/luVnq4laYiTHUcQG3mEWirhNy6mJJ_aSTlYWcKHTfDFpQJOCKALKFUgJjJEQWeeadVtjv663soNxDSfm29Awgr2eYMDyDuHwMGUIOKho6zHMK-90FGR3Bs4ZMZYMSGmTk3Al58jcyKI")
            Coil.load(this, "https://lh6.googleusercontent.com/7HwiyGQgVsnaPSi0KDp_IE-UqKaDmZIlQQqKyJXAXHwiyVoYfkQfQwbCMGMf3nHhC3sIKzDIaSJq2-Aod9RErfPZlCVoLrHU4kAt3K7rwPXbKPW7Js4FmY4KXoIwTt4asEm6bBGvBkk")
            Coil.load(this, "https://lh3.googleusercontent.com/c6PnVBA7rKB_ofPEmcRXEQfNste2x5C3M5rmC1dlUUnoqHLlHg4R8wNZAS7LKFSBmL9f-5lB85oPwlBQ7Ib9aXTxHnHS9iBwdCkMQhTk4rgMsaKQyfObPg70xGB2_kH9GM8A8BOereo")
            Coil.load(this, "https://lh3.googleusercontent.com/F7BDjvSURaJuIEYUZ3ikrK6rdvqXjYmZvPrFoyyNpoHOAVOlzcheVO9WFCB3bZgkcvuhNxEQdKFiC-SGKOIDPdc8rPmHrTZFc6OovxKiM9VMcx93Bdf6kKMaZxFmVobxxZyXSh3kqfI")
            Coil.load(this, "https://lh5.googleusercontent.com/ecnggpWNeMRffzCDrEugrJqbolVoOXyd_jyHSRtuezPI461GIxdOXF6_e2yGE5Yf9BWf5QeSFwtaw8fsWdn8uKdyOtCZQDh7N86bNL5yXs7XsYHNJjpxUJoCsjdF2I1cTpBTwJuKLLs")
            Coil.load(this, "https://lh5.googleusercontent.com/Z7HKvSPvB-yj3xqvu1W8pOaHhzwS0uFVw7l6OoqDmMpBb4FZOZVkKCIFxB2T2mELkkQOXkfO4nAafk06-yGAO_zk22SLyTlgxW4RZAUjwphApCxu2i1CPbdXyG9ojVAa94yYkf3jwjA")
        }
    }

    override fun off() {
        disposables.dispose()
    }
}

enum class StoryEvent {
    Pause,
    Resume,
    Previous,
    Start,
    Next,
    Reset,
}
