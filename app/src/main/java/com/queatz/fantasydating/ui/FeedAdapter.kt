package com.queatz.fantasydating.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.queatz.fantasydating.Event
import com.queatz.fantasydating.R
import com.queatz.fantasydating.visible
import com.queatz.on.On
import kotlinx.android.synthetic.main.item_feed.view.*

class FeedAdapter constructor(private val on: On) : RecyclerView.Adapter<FeedViewHolder>() {

    var items = mutableListOf<Event>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = listOf("TODAY", "YESTERDAY", "MAY 23RD").random()
        holder.text.text = item.name

        holder.title.visible = position % 2 == 0
    }
}

class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.title
    val text: TextView = view.text
}