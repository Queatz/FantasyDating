package com.queatz.fantasydating

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.queatz.fantasydating.features.NavigationFeature
import com.queatz.fantasydating.features.TopFeature
import com.queatz.fantasydating.features.ViewFeature
import com.queatz.fantasydating.ui.MessagesAdapter
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : BaseActivity() {

    private var person: Person? = null
    private lateinit var personId: String
    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        if (handle(intent).not()) {
            finish()
        }

        messagesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        adapter = MessagesAdapter(on) { person?.name ?: "" }
        messagesRecyclerView.adapter = adapter

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
                on<Say>().say("Message not sent")
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
            setPhoto(person.stories.firstOrNull()?.photo)
            viewStoryButton.visible = true
            viewStoryButton.text = getString(R.string.view_story,  on<ValueFeature>().referToAs(person.sex))
            viewStoryButton.onLinkClick = {
                on<NavigationFeature>().showPerson(person.id!!)
            }
            sendMessageInput.hint = "Send ${ on<ValueFeature>().referToAs(person.sex, true)} a message"
            sendMessageInput.isEnabled = true
            sendMessageButton.isEnabled = true
        } ?: run {
            setPhoto(null)
            viewStoryButton.visible = true
            viewStoryButton.text = "Viewing archived messages"
            viewStoryButton.onLinkClick = {}
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

        background.load("$photo?s=1600") {
            placeholder(R.color.white)
            crossfade(true)
            listener { _, _ -> }
        }
    }
}
