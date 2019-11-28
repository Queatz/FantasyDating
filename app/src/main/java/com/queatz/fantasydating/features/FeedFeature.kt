package com.queatz.fantasydating.features

import androidx.recyclerview.widget.LinearLayoutManager
import com.queatz.fantasydating.Api
import com.queatz.fantasydating.State
import com.queatz.fantasydating.ui.FeedAdapter
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class FeedFeature constructor(private val on: On) {
    private lateinit var adapter: FeedAdapter

    fun start() {
        reload()

        on<ViewFeature>().with {
            feedRecyclerView.layoutManager = LinearLayoutManager(this)

            adapter = FeedAdapter(on)

            feedRecyclerView.adapter = adapter

            on<State>().observe(State.Area.Ui) {
                if (changed(it) { ui.showFeed }) {
                    if (ui.showFeed) {
                        reload()
                    }
                }
            }
        }
    }

    private fun reload() {
        on<Api>().events {
            adapter.items = it.toMutableList()
        }
    }
}
