package com.queatz.fantasydating.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.queatz.fantasydating.R
import com.queatz.fantasydating.Style
import com.queatz.on.On
import kotlinx.android.synthetic.main.item_style_small.view.*

class StyleAdapter constructor(
    private val on: On,
    private val addCallback: (() -> Unit)? = null,
    private val callback: (style: Style, longPress: Boolean) -> Unit) : RecyclerView.Adapter<StyleViewHolder>() {

    var showAdd: Boolean = false
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var items = mutableListOf<Style>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()

            // todo diff
        }

    fun moveItem(from: Int, to: Int) {
        if (from >= items.size || to >= items.size) {
            return
        }

        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder {
        return StyleViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_style_small, parent, false))
    }

    override fun getItemCount() = items.size + (if (showAdd) 1 else 0)

    override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
        if (position >= items.size) {
            holder.name.text = "➕"

            holder.name.setOnClickListener {
                addCallback?.invoke()
            }
            return
        }

        val item = items[position]

        holder.name.text = item.name

        holder.name.setOnClickListener {
            callback(item, false)
        }

//        holder.name.setOnLongClickListener {
//            callback(item, true)
//            true
//        }
    }
}

class StyleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
}
