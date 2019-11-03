package com.queatz.fantasydating

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import androidx.core.view.GestureDetectorCompat
import coil.Coil
import coil.api.load
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin


class MainActivity : AppCompatActivity() {

    var my = MyPreferences("Person", "", 0, "", listOf())
    val discoveryPreferences = DiscoveryPreferences("Girls", "Austin", 25, 35)

    var showFantasy = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            if (value) {
                closeBub(bub3)
                swipeUpArrow.rotation = 180f
                fantasy.visibility = View.VISIBLE
                storyText.visibility = View.GONE
                moreOptionsButton.visibility = View.GONE
                moreOptionsText.visibility = View.GONE
                stories.pause()
                showBub(bub4)
                closeBub(bub5)
            } else {
                swipeUpArrow.rotation = 0f
                fantasy.visibility = View.GONE
                storyText.visibility = View.VISIBLE
                moreOptionsButton.visibility = View.VISIBLE
                stories.resume()
                closeBub(bub4)
                showBub(bub5)
            }
        }

    var showFeed = true
        set(value) {
            if (field == value) {
                return
            }

            field = value

            discoveryPreferencesText.visibility = if (value) View.VISIBLE else View.GONE
            feedGroup.visibility = if (value) View.VISIBLE else View.GONE

            if (value) {
                stories.set(0)
            }
        }

    var showDiscoveryPreferences = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            discoveryPreferencesLayout.visibility = if (value) View.VISIBLE else View.GONE
            showFeed = !value

            if (value) {
                closeBub(bub2)
            } else {
                editPreferenceText.visibility = View.GONE
                editProfileText.visibility = View.VISIBLE
            }
        }

    private var editorCallback: (String) -> Unit = {}

    private val disposables = CompositeDisposable()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        background.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark, theme))

        fantasyText.movementMethod = ScrollingMovementMethod()

        notification1.text = resources.getString(R.string.link, "YOU MATCHED WITH AMY")
        notification2.text = resources.getString(R.string.link, "JING SENT YOU 3 MESSAGES")
        notification3.text = resources.getString(R.string.link, "YOU MATCHED WITH JING")
        notification4.text = resources.getString(R.string.link, "WELCOME TO FANTASY DATING")

        disposables.add(stories.exitObservable
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it == 1) {
                    stories.start()
                } else {
                    stories.set(6)
                    stories.next()
                }
            })

        Coil.load(this, "https://lh3.googleusercontent.com/80hJcieOULQhfT2hLS689_tNOCACzpilOYjTMvgw8aHH12Nk4hj7eTCsFdWY4lcC8laMoSAk8YIshlWMxRHELXYBE3UtDtWCK1_1uXFotpeUKn_D2AA0ZMcpQDwML8rBgDjMmFjaeW8")
        Coil.load(this, "https://lh3.googleusercontent.com/LE1DiS1zLJ-kdc639XxdC89NtNjBl3v8M7a2A_KX-8PaINqGvruYkcAnxOYu6Pbaa9asAdT75nHniyMUZRMrlSMoV0hH374dJlRdWzXhagh6ywZKvBZILyFEQnFLsnHLgIXQklUtcFo")
        Coil.load(this, "https://lh4.googleusercontent.com/luVnq4laYiTHUcQG3mEWirhNy6mJJ_aSTlYWcKHTfDFpQJOCKALKFUgJjJEQWeeadVtjv663soNxDSfm29Awgr2eYMDyDuHwMGUIOKho6zHMK-90FGR3Bs4ZMZYMSGmTk3Al58jcyKI")
        Coil.load(this, "https://lh6.googleusercontent.com/7HwiyGQgVsnaPSi0KDp_IE-UqKaDmZIlQQqKyJXAXHwiyVoYfkQfQwbCMGMf3nHhC3sIKzDIaSJq2-Aod9RErfPZlCVoLrHU4kAt3K7rwPXbKPW7Js4FmY4KXoIwTt4asEm6bBGvBkk")
        Coil.load(this, "https://lh3.googleusercontent.com/c6PnVBA7rKB_ofPEmcRXEQfNste2x5C3M5rmC1dlUUnoqHLlHg4R8wNZAS7LKFSBmL9f-5lB85oPwlBQ7Ib9aXTxHnHS9iBwdCkMQhTk4rgMsaKQyfObPg70xGB2_kH9GM8A8BOereo")
        Coil.load(this, "https://lh3.googleusercontent.com/F7BDjvSURaJuIEYUZ3ikrK6rdvqXjYmZvPrFoyyNpoHOAVOlzcheVO9WFCB3bZgkcvuhNxEQdKFiC-SGKOIDPdc8rPmHrTZFc6OovxKiM9VMcx93Bdf6kKMaZxFmVobxxZyXSh3kqfI")
        Coil.load(this, "https://lh5.googleusercontent.com/ecnggpWNeMRffzCDrEugrJqbolVoOXyd_jyHSRtuezPI461GIxdOXF6_e2yGE5Yf9BWf5QeSFwtaw8fsWdn8uKdyOtCZQDh7N86bNL5yXs7XsYHNJjpxUJoCsjdF2I1cTpBTwJuKLLs")
        Coil.load(this, "https://lh5.googleusercontent.com/Z7HKvSPvB-yj3xqvu1W8pOaHhzwS0uFVw7l6OoqDmMpBb4FZOZVkKCIFxB2T2mELkkQOXkfO4nAafk06-yGAO_zk22SLyTlgxW4RZAUjwphApCxu2i1CPbdXyG9ojVAa94yYkf3jwjA")

        disposables.add(stories.currentObservable
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
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

                moreOptionsText.visibility = View.GONE
            })

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
                closeBub(bub1)
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

            editProfile()
        }

        editorDoneButton.setOnClickListener {
            editorCancelButton.callOnClick()
            editorCallback.invoke(editor.text.toString())
        }

        editorCancelButton.setOnClickListener {
            editorLayout.visibility = View.GONE

            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editor.windowToken, 0)
        }

        editor.setOnEditorActionListener { textView, action, keyEvent ->
            when (action) {
                EditorInfo.IME_ACTION_DONE -> {
                    editorDoneButton.callOnClick()
                    true
                }
                else -> false
            }
        }

        updateDiscoveryPreferences()

        showWelcomeModal()

        loveButton.setOnClickListener {
            showFantasy = false
            closeBub(bub4)
        }

        moreOptionsText.onLinkClick = {
            moreOptionsText.visibility = View.GONE
            stories.resume()
        }

        moreOptionsButton.setOnClickListener {
            moreOptionsText.visibility = View.VISIBLE
            stories.pause()
            closeBub(bub5)
        }

        val oneDp = resources.getDimensionPixelSize(R.dimen.dp)

        disposables.add(storyText.firstLineWidth
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                (moreOptionsButton.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    leftMargin = it + oneDp * 42
                }
                moreOptionsButton.requestLayout()
            })
    }

    private fun editProfile() {
        updateMyStory()

        storyText.onLinkClick = {
            when (it) {
                "name" -> {
                    editor.setText(my.name)
                    editorCallback = {
                        my.name = it

                        updateMyStory()
                    }
                }
                "age" -> {
                    editor.setText(if (my.age > 18) my.age.toString() else "")
                    editorCallback = {
                        my.age = it.toIntOrNull() ?: 0

                        updateMyStory()
                    }
                }
                "story" -> {
                    editor.setText(my.stories.firstOrNull() ?: "")
                    editorCallback = {
                        if (it.isEmpty()) {
                            my.stories = listOf()
                        } else {
                            my.stories = listOf(it)
                        }

                        updateMyStory()
                    }
                }
            }

            openEditor()
        }

        storyText.elevation = 1f

        fantasyText.setOnClickListener {
            editor.setText(my.fantasy)
            editorCallback = {
                my.fantasy = it

                updateMyStory()
            }

            openEditor()
        }

        choosePhotoButton.visibility = View.VISIBLE
        choosePhotoButton.setOnClickListener {
            choosePhotoButton.visibility = View.GONE
            storyText.onLinkClick = {}
            storyText.elevation = 0f
            fantasyText.setOnClickListener {  }
        }
    }

    private fun openEditor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        editorLayout.visibility = View.VISIBLE
        editor.requestFocus()
        editor.selectAll()


        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editor, 0)
    }

    private fun updateMyStory() {
        storyText.text = "<tap data=\"name\">${if (my.name.isBlank()) "Your name" else my.name}</tap>, <tap data=\"age\">${if (my.age < 18) "your age" else my.age.toString()}</tap><br /><br />I love <tap data=\"story\">${if (my.stories.isEmpty()) "write something here" else my.stories[0]}</tap>"
        fantasyText.text = if (my.fantasy.isBlank()) "Start writing your fantasy here" else my.fantasy
    }

    private fun showWelcomeModal() {
        welcomeMessageLayout.visibility = View.VISIBLE
        welcomeMessageText.onLinkClick = {
            my.sex = it
            welcomeMessageLayout.visibility = View.GONE

            bub1.visibility = View.VISIBLE
            bub2.visibility = View.VISIBLE
            bub3.visibility = View.VISIBLE

            listOf(bub1, bub2, bub3).forEach { showBub(it) }
        }
    }

    private fun showAustinOnly() {
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
        if (editorLayout.visibility != View.GONE) {
            editorCancelButton.callOnClick()
        } else if (showDiscoveryPreferences) {
            showDiscoveryPreferences = false
        } else if (showFantasy) {
            showFantasy = false
        } else if (!showFeed) {
            showFeed = true
        } else {
            super.onBackPressed()
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

    fun showBub(view: View) = view.apply {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }

    fun closeBub(view: View) {
        if (view.visibility == View.GONE) {
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
}
