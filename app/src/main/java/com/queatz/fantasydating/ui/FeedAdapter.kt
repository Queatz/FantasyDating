package com.queatz.fantasydating.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.queatz.fantasydating.Event
import com.queatz.fantasydating.Pretty
import com.queatz.fantasydating.R
import com.queatz.fantasydating.features.FeedFeature
import com.queatz.fantasydating.features.ViewFeature
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

        val day = on<Pretty>().date(item.created)

        holder.title.text = day

        if (position > 0) {
            val itemBefore = items[position - 1]
            val itemBeforeDay = on<Pretty>().date(itemBefore.created)
            holder.title.visible = itemBeforeDay != day
        } else {
            holder.title.visible = true
        }

        holder.text.text = on<ViewFeature>().activity.getString(R.string.link, item.name)

        holder.text.onLinkClick = {
            on<FeedFeature>().open(item)
        }
    }
}

class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.title
    val text: FancyTextView = view.text
}