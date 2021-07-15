package com.queatz.fantasydating

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.queatz.fantasydating.features.*
import com.queatz.fantasydating.ui.MessagesAdapter
import com.queatz.fantasydating.ui.StyleAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.activity_messages.background
import kotlinx.android.synthetic.main.activity_messages.fantasy
import kotlinx.android.synthetic.main.activity_messages.fantasyText
import kotlinx.android.synthetic.main.activity_messages.fantasyTitle
import kotlinx.android.synthetic.main.activity_messages.styleRecyclerView
import kotlinx.android.synthetic.main.activity_messages.styleTitle
import kotlinx.android.synthetic.main.fullscreen_modal.*

class MessagesActivity : BaseActivity() {

    private var person: Person? = null
    private lateinit var personId: String
    private lateinit var adapter: MessagesAdapter
    private lateinit var styleAdapter: StyleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        if (handle(intent).not()) {
            finish()
        }

        messagesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        adapter = MessagesAdapter(on) { person?.name ?: "" }
        messagesRecyclerView.adapter = adapter

        styleAdapter = StyleAdapter(on) { style, _ ->
            on<ViewFeature>().with {
                on<LayoutFeature>().canCloseFullscreenModal = true
                fullscreenMessageText.text = "<b>${style.name}</b><br />${style.about}<br /><br />Show <tap data=\"style-promote\">More</tap>, <tap data=\"style-demote\">Less</tap>, or <tap data=\"style-dismiss\">Don't</tap> show people with this 氣<br /><br /><tap data=\"close\">${getString(R.string.close)}</tap>"
                fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                fullscreenMessageLayout.fadeIn()

                fullscreenMessageText.onLinkClick = {
                    when (it) {
                        "style-promote" -> {}
                        "style-demote" -> {}
                        "style-dismiss" -> {}
                    }

                    fullscreenMessageLayout.fadeOut()
                }
            }
        }
        styleRecyclerView.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        styleRecyclerView.adapter = styleAdapter

        sendMessageInput.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        sendMessageButton.setOnClickListener {
            sendMessage()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handle(intent)
    }

    override fun onPause() {
        super.onPause()
        on<TopFeature>().topPerson = ""
        on<TopFeature>().onCaught = {}
    }

    override fun onResume() {
        super.onResume()
        on<TopFeature>().topPerson = personId
        on<TopFeature>().onCaught = { reloadMessages() }
    }

    private fun handle(intent: Intent?) =
        intent?.getStringExtra(NavigationFeature.ExtraPersonId)?.let {
            show(it)
            true
        } ?: false

    private fun reloadMessages() {
        on<Api>().messages(personId) {
            adapter.items = it.toMutableList()
            on<ViewFeature>().with {
                messagesRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun sendMessage() {
        val message = sendMessageInput.text.toString()

        if (person?.id == null || message.isBlank()) {
            return
        }

        on<Api>().sendMessage(person!!.id!!, MessageRequest(message)) {
            if (it.success.not()) {
                on<Say>().say(getString(R.string.message_not_sent))
            } else {
                reloadMessages()
            }
        }

        sendMessageInput.setText("")
    }

    private fun show(person: String) {
        personId = person

        reloadMessages()

        setPhoto(null)

        on<Api>().person(person) {
            show(it)
        } error { show(null) }
    }

    private fun show(person: Person?) {
        this.person = person

        person?.let { person ->
            styleAdapter.items = person.styles.toMutableList()
            setPhoto(person.stories.firstOrNull()?.photo)
            viewStoryButton.visible = true
            viewStoryButton.text = getString(R.string.view_story,  on<ValueFeature>().referToAs(person.sex))
            viewStoryButton.onLinkClick = {
                on<NavigationFeature>().showPerson(person.id!!)
            }
            fantasy.visible = true
            fantasyTitle.text = getString(R.string.introduction)
            fantasyText.text = person.fantasy
            styleTitle.text = getString(R.string.persons_cuddle_styles, person.name)
            styleTitle.visible = person.styles.isNotEmpty()
            styleRecyclerView.visible = person.styles.isNotEmpty()
            sendMessageInput.hint = getString(R.string.send_x_a_message, on<ValueFeature>().referToAs(person.sex, true))
            sendMessageInput.isEnabled = true
            sendMessageButton.isEnabled = true
        } ?: run {
            setPhoto(null)
            viewStoryButton.visible = true
            viewStoryButton.text = getString(R.string.viewing_archived_messages)
            viewStoryButton.onLinkClick = {}
            fantasy.visible = false
            sendMessageInput.hint = "Send ${ on<ValueFeature>().referToAs("Person", true)} a message"
            sendMessageInput.isEnabled = false
            sendMessageButton.isEnabled = false
        }
    }

    private fun setPhoto(photo: String?) {
        background.setImageResource(R.color.white)

        if (photo.isNullOrBlank()) {
            return
        }

        on<PhotoFeature>().load("$photo?s=1600", background, R.color.white)
    }
}
