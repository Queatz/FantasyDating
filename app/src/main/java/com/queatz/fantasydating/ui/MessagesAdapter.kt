package com.queatz.fantasydating.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.queatz.fantasydating.Message
import com.queatz.fantasydating.R
import com.queatz.on.On
import kotlinx.android.synthetic.main.item_feed.view.*

class MessagesAdapter constructor(private val on: On) : RecyclerView.Adapter<MessagesViewHolder>() {

    var items = mutableListOf<Message>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {

    }
}

class MessagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.title
    val text: TextView = view.text
}