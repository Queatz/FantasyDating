package com.queatz.fantasydating.features

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.text.method.ScrollingMovementMethod
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.queatz.fantasydating.*
import com.queatz.fantasydating.ui.MoveZoomHandler
import com.queatz.fantasydating.ui.StyleAdapter
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_style_modal.*
import kotlinx.android.synthetic.main.add_style_modal.view.*
import kotlin.math.min

class StoryFeature constructor(private val on: On) : OnLifecycle {

    private val disposables = CompositeDisposable()

    var currentOrigin = PointF()
    var overrideEvent: StoryEventListener? = null
    var personNavigationListener: PersonNavigationListener? = null

    val scaleHandler =
        MoveZoomHandler(on) { scale, origin ->
            on<ViewFeature>().with {
                background.scale = scale
                background.origin = origin
            }
        }

    private lateinit var adapter: StyleAdapter

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
        on<ViewFeature>().with {
            styleRecyclerView.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)

            adapter = StyleAdapter(on, true, {
                on<ViewFeature>().with {
                    val adapter = StyleAdapter(on, false) { it, longPress ->
                        if (longPress) return@StyleAdapter

                        on<Api>().linkStyle(MeStyleRequest(link = it.id)) {
                            on<Say>().say(R.string.style_added_to_your_profile)
                            on<MyProfileFeature>().reload()
                        }
                        searchLayout.visible = false
                    }

                    on<Api>().getStyles {
                        adapter.items = it.toMutableList()
                    }

                    searchLayout.searchEditText.setText("")

                    searchLayout.searchRecyclerView.adapter = adapter
                    searchLayout.searchRecyclerView.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)

                    searchModalText.text = "Tap on a Cuddle Style to add it to your profile.<br /><br /><tap data=\"create\">Create one</tap>, or <tap data=\"close\">Close</tap>"
                    searchModalText.onLinkClick = {
                        when (it) {
                            "create" -> {
                                addStyleModalLayout.visible = true
                                addStyleModalLayout.addStyleNameEditText.setText("")
                                addStyleModalLayout.addStyleAboutEditText.setText("")
                                addStyleModalLayout.addStyleModalText.text = "<tap data=\"create\">Create</tap>, or <tap data=\"close\">Cancel</tap>"

                                addStyleModalLayout.addStyleModalText.onLinkClick = {
                                    when (it) {
                                        "create" -> {
                                            val name = addStyleModalLayout.addStyleNameEditText.text.toString()
                                            val about = addStyleModalLayout.addStyleAboutEditText.text.toString()

                                            on<Api>().createStyle(StyleRequest(name, about)) {
                                                on<Say>().say(R.string.style_added_to_your_profile)
                                                on<MyProfileFeature>().reload()
                                            }
                                        }
                                        "close" -> {
                                        }
                                    }

                                    addStyleModalLayout.visible = false
                                }
                            }
                            "close" -> { }
                        }

                        searchLayout.visible = false
                    }

                    searchLayout.visible = true
                }
            }) { style, _ ->
                on<ViewFeature>().with {
                    on<LayoutFeature>().canCloseFullscreenModal = true
                    fullscreenMessageText.text = "<b>${style.name}</b><br />${style.about}<br /><br /><tap data=\"close\">Close</tap>, or <tap data=\"remove\">Remove</tap>"
                    fullscreenMessageLayout.visible = true
                    fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

                    fullscreenMessageText.onLinkClick = {
                        when (it) {
                            "remove" -> {
                                on<Api>().linkStyle(MeStyleRequest(unlink = style.id)) {
                                    on<Say>().say(R.string.style_removed_from_your_profile)
                                    on<MyProfileFeature>().reload()
                                }
                            }
                        }

                        fullscreenMessageLayout.visible = false
                    }
                }
            }

            styleRecyclerView.adapter = adapter
        }

        on<Api>().me {
            adapter.items = it.styles.toMutableList()
        }

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
                    fantasyTitle.text = when {
                        person.failed && on<MyProfileFeature>().myProfile.invited.not() -> "Scan someone's invite code to join Fantasy Dating."
                        person.failed && on<MyProfileFeature>().myProfile.invited -> "Failed to load ${
                            on<ValueFeature>().pluralSex(on<DiscoveryPreferencesFeature>().discoveryPreferences.who).toLowerCase()
                        }. <tap data=\"reload\">Reload</tap>"
                        person.loading -> "Finding ${
                            on<ValueFeature>().pluralSex(on<DiscoveryPreferencesFeature>().discoveryPreferences.who).toLowerCase()
                        }, please wait..."
                        else -> "There's no more ${on<ValueFeature>().pluralSex(on<DiscoveryPreferencesFeature>().discoveryPreferences.who).toLowerCase()} to discover in Austin right now. <tap data=\"reload\">Reload</tap>"
                    }
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
                    fantasyTitle.text = getString(R.string.persons_fantasy, person.current?.name ?: "")
                    styleTitle.text = getString(R.string.persons_cuddle_styles, person.current?.name ?: "")
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

                        currentOrigin = PointF(stories[it].x, stories[it].y)

                        scaleHandler.reset(1f, currentOrigin)
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

            on<PhotoFeature>().load("$photo?s=1600", background, R.drawable.bkg) {
                stories.post { event(StoryEvent.Resume) }
            }

            on<PhotoFeature>().load("$photo?s=4") { drawable ->
                val color = drawable.toBitmap(4, 4, Bitmap.Config.ARGB_8888)[0, 3]
                val hsv = floatArrayOf(0f, 0f, 0f)
                Color.colorToHSV(color, hsv)
                hsv[2] = min(hsv[2], .333f)
                val c = Color.HSVToColor(hsv)
                storyText.setTextColor(c)
                fantasy.setBackgroundColor(Color.argb(163, Color.red(c) / 4, Color.green(c) / 4, Color.blue(c) / 4))
            }
        }
    }

    fun onBackPressed() = on<ViewFeature>().with {
        when {
            searchLayout.visible -> {
                searchLayout.visible = false
                true
            }
            addStyleModalLayout.visible -> {
                addStyleModalLayout.visible = false
                true
            }
            confirmLove.visible -> {
                confirmLove.visible = false
                on<StoryFeature>().event(StoryEvent.Resume)
                true
            }
            else -> false
        }
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
