package com.queatz.fantasydating

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.queatz.fantasydating.features.NavigationFeature
import com.queatz.fantasydating.ui.MessagesAdapter
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : BaseActivity() {

    private lateinit var person: Person
    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        intent?.getStringExtra(NavigationFeature.ExtraPersonId)?.let {
            show(it)
        } ?: finish()

        messagesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        adapter = MessagesAdapter(on) { person.name }
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

    private fun reload() {
        on<Api>().messages(person.id!!) {
            adapter.items = it.toMutableList()
        }
    }

    private fun sendMessage() {
        val message = sendMessageInput.text.toString()

        if (message.isBlank()) {
            return
        }

        on<Api>().sendMessage(person.id!!, MessageRequest(message)) {
            if (it.success.not()) {
                on<Say>().say("Message not sent")
            } else {
                reload()
            }
        }

        sendMessageInput.setText("")
    }

    private fun show(person: String) {
        on<Api>().person(person) {
            show(it)
        }
    }

    private fun show(person: Person) {
        this.person = person
        val refer = on<ValueFeature>().referToAs(person.sex)

        setPhoto(person.stories.firstOrNull()?.photo)
        viewStoryButton.text = getString(R.string.view_story, refer)
        viewStoryButton.onLinkClick = {
            on<NavigationFeature>().showPerson(person.id!!)
        }
        sendMessageInput.hint = "Send $refer a message"

        reload()
    }

    private fun setPhoto(photo: String?) {
        background.setImageResource(R.drawable.bkg)

        if (photo.isNullOrBlank()) {
            return
        }

        background.load("$photo?s=1600") {
            placeholder(R.drawable.bkg)
            crossfade(true)
            listener { _, _ -> }
        }
    }
}
