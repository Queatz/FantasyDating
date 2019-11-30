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

    var overrideEvent: StoryEventListener? = null
    var personNavigationListener: PersonNavigationListener? = null

    fun event(event: StoryEvent) {
        if (on<State>().person.current == null) {
            return
        }

        if (overrideEvent?.invoke(event) == true) {
            return
        }

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
        on<State>().observe(State.Area.Person) {
            on<ViewFeature>().with {
                disposables.add(lifecycle.subscribe {
                    when (it) {
                        LifecycleEvent.Pause -> event(StoryEvent.Pause)
                        LifecycleEvent.Resume -> event(StoryEvent.Resume)
                    }
                })

                if (ui.showFeed || ui.showDiscoveryPreferences || ui.showFantasy || ui.showEditProfile) {
                    event(StoryEvent.Reset)
                } else {
                    event(StoryEvent.Start)
                }

                if (person.current == null) {
                    if (on<State>().ui.showFantasy.not()) {
                        on<State> {
                            ui = ui.copy(showFantasy = true)
                        }
                    }

                    stories.visible = false
                    background.setImageResource(R.drawable.bkg)
                    swipeUpArrow.visible = false
                    moreOptionsButton.visible = false
                    loveButton.visible = false
                    fantasyTitle.text = "There's no more ${on<ValueFeature>().pluralSex(on<DiscoveryPreferencesFeature>().discoveryPreferences.who).toLowerCase()} to discover in Austin right now. <tap data=\"reload\">Reload</tap>"
                    fantasyTitle.onLinkClick = {
                        on<PeopleFeature>().reload()
                    }
                    fantasyText.text = ""
                    storyText.text = ""
                    return@with
                } else {
                    stories.visible = true
                    loveButton.visible = true
                    swipeUpArrow.visible = true
                    moreOptionsButton.visible = true

                    if (it.previous.person.current == null) {
                        if (on<State>().ui.showFantasy) {
                            on<State> {
                                ui = ui.copy(showFantasy = false)
                            }
                        }
                    }
                }

                if (ui.showEditProfile) {
                    on<EditProfileFeature>().updateFantasy()
                } else {
                    fantasyTitle.text = "${person.current?.name ?: ""}'s Fantasy"
                    fantasyText.text = person.current?.fantasy ?: ""
                    person.current?.stories?.apply { stories.count = size }
                }

                fantasyText.scrollTo(0, 0)
            }
        }

        on<ViewFeature>().with {
            background.setImageResource(R.drawable.bkg)

            fantasyText.movementMethod = ScrollingMovementMethod()

            loveButton.setOnClickListener {
                if (on<MyProfileFeature>().myProfile.id == on<State>().person.current?.id) {
                    confirmLove.text = getString(R.string.confirm_love_self)
                } else {
                    val person = on<State>().person.current!!
                    confirmLove.text = when {
                        person.youLove && person.lovesYou ->
                            getString(R.string.you_love_each_other, person.name, on<ValueFeature>().referToAs(person.sex, true))
                        person.youLove ->
                            getString(R.string.remove_love, person.name)
                        on<MyProfileFeature>().isComplete().not() ->
                            getString(R.string.complete_your_profile, person.name)
                        else ->
                            getString(R.string.confirm_your_love, person.name)

                    }
                }

                on<WalkthroughFeature>().closeBub(bub4)

                confirmLove.visible = confirmLove.visible.not()
                confirmLove.onLinkClick = {
                    when (it) {
                        "love" -> {
                            on<State>().person.current?.let {
                                on<State> {
                                    ui = ui.copy(showFantasy = false)
                                }

                                it.youLove = true
                                on<StoreFeature>().get(Person::class).put(it)

                                on<Api>().person(it.id!!, PersonRequest(love = true)) {}
                            }
                        }
                        "love:remove" -> {
                            on<State>().person.current?.let {
                                on<State> {
                                    ui = ui.copy(showFantasy = false)
                                }

                                it.youLove = false
                                on<StoreFeature>().get(Person::class).put(it)

                                on<Api>().person(it.id!!, PersonRequest(love = false)) {}
                            }
                        }
                        "profile" -> {
                            on<EditProfileFeature>().editProfile()
                        }
                        "message" -> {
                            on<State>().person.current?.let {
                                on<NavigationFeature>().showMessages(it.id!!)
                            }
                        }
                    }
                }
            }

            disposables.add(stories.exitObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (personNavigationListener?.invoke(it) == true) {
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

                    if (on<State>().ui.showEditProfile) {
                        stories.post { event(StoryEvent.Pause) }
                    }

                    on<State>().person.current?.apply {
                        if (it >= stories.size) {
                            return@subscribe
                        }

                        setPhoto(stories[it].photo)

                        if (on<State>().ui.showEditProfile) {
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

            background.setImageResource(R.drawable.bkg)

            if (photo.isBlank()) {
                return@with
            }

            background.load("$photo?s=1600") {
                placeholder(R.drawable.bkg)
                crossfade(true)
                listener { _, _ ->
                    stories.post { event(StoryEvent.Resume) }
                }
            }

            Coil.load(this, "$photo?s=4") {
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
            on<StoryFeature>().event(StoryEvent.Resume)
            true
        } else false
    }

    override fun off() {
        disposables.dispose()
    }

    fun getCurrentStory() = on<ViewFeature>().activity.stories.currentObservable.value!!
}

typealias StoryEventListener = (event: StoryEvent) -> Boolean
typealias PersonNavigationListener = (direction: Int) -> Boolean

enum class StoryEvent {
    Pause,
    Resume,
    Previous,
    Start,
    Next,
    Reset,
}
