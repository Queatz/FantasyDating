package com.queatz.fantasydating.features

import androidx.recyclerview.widget.LinearLayoutManager
import com.queatz.fantasydating.Event
import com.queatz.fantasydating.R
import com.queatz.fantasydating.ui.FeedAdapter
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class FeedFeature constructor(private val on: On) {
    fun start() {
        on<ViewFeature>().with {
            feedRecyclerView.layoutManager = LinearLayoutManager(this)

            val adapter = FeedAdapter(on)

            feedRecyclerView.adapter = adapter

            adapter.items = listOf(
                "YOU MATCHED WITH AMY",
                "JING SENT YOU 3 MESSAGES",
                "YOU MATCHED WITH JING",
                "WELCOME TO FANTASY DATING",
                "YOU MATCHED WITH AMY",
                "JING SENT YOU 3 MESSAGES",
                "YOU MATCHED WITH JING",
                "WELCOME TO FANTASY DATING",
                "YOU MATCHED WITH AMY",
                "JING SENT YOU 3 MESSAGES",
                "YOU MATCHED WITH JING",
                "WELCOME TO FANTASY DATING",
                "YOU MATCHED WITH AMY",
                "JING SENT YOU 3 MESSAGES",
                "YOU MATCHED WITH JING",
                "WELCOME TO FANTASY DATING"
            ).map {
                Event(name = resources.getString(R.string.link, it))
            }.toMutableList()
        }
    }
}
