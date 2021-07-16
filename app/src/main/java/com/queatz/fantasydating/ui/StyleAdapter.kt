package com.queatz.fantasydating.ui

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.toSpannable
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

        if (item.preference == null) {
            holder.name.text = item.name
        } else {
            holder.name.text = styleWithPrefs(item)
        }

        holder.name.setOnClickListener {
            callback(item, false)
        }

//        holder.name.setOnLongClickListener {
//            callback(item, true)
//            true
//        }
    }

    private fun styleWithPrefs(style: Style): Spannable {
        val spannable = SpannableStringBuilder(style.name)

        val pref = pref(style)

        if (pref.isNotEmpty()) {
            spannable.bold { color(if ((style.preference?.favor ?: 0f) > 0f) Color.parseColor("#00aa00") else Color.RED) { append(pref) } }
        }

        return spannable.toSpannable()
    }

    private fun pref(style: Style) = when {
        style.preference?.dismissed == true -> " ⛔"
        style.preference!!.favor > 0 -> " +${style.preference!!.favor.toInt()}"
        style.preference!!.favor < 0 -> " ${style.preference!!.favor.toInt()}"
        else -> ""
    }
}

class StyleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.name
}
