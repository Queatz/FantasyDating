package com.queatz.fantasydating

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
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

        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessagesAdapter(on)
        messagesRecyclerView.adapter = adapter

        sendMessageInput.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEND) {

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
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
