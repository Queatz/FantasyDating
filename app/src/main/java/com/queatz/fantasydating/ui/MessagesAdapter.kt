package com.queatz.fantasydating.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.queatz.fantasydating.*
import com.queatz.fantasydating.features.MyProfileFeature
import com.queatz.on.On
import kotlinx.android.synthetic.main.item_message.view.*

class MessagesAdapter constructor(private val on: On, val personName: () -> String) : RecyclerView.Adapter<MessagesViewHolder>() {

    var items = mutableListOf<Message>()
        set(value) {
            val diffCallback = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    field[oldItemPosition].id == value[newItemPosition].id

                override fun getOldListSize() = field.size

                override fun getNewListSize() = value.size

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ) = field[oldItemPosition].message == value[newItemPosition].message
            })

            field.clear()
            field.addAll(value)

            diffCallback.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val item = items[position]
        val isFromMe = on<MyProfileFeature>().myProfile.id == item.from

        holder.name.visible = isFromMe.not()

        if (isFromMe.not()) {
            holder.name.text = personName()
        }

        holder.message.text = item.message

        holder.messageLayout.setBackgroundResource(if (isFromMe) R.drawable.primary_light_rounded else R.drawable.white_rounded)

        (holder.messageLayout.layoutParams as ConstraintLayout.LayoutParams).apply {
            horizontalBias = if (isFromMe) 1f else 0f
        }

        holder.messageLayout.setOnClickListener {
            on<Say>().say("Sent ${on<Pretty>().date(item.created)}")
        }
    }
}

class MessagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val messageLayout: ViewGroup = view.messageLayout
    val name: TextView = view.name
    val message: TextView = view.message
}