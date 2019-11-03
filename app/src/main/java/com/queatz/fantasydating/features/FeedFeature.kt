package com.queatz.fantasydating.features

import com.queatz.fantasydating.R
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*

class FeedFeature constructor(private val on: On) {
    fun start() {
        on<ViewFeature>().with {
            notification1.text = resources.getString(R.string.link, "YOU MATCHED WITH AMY")
            notification2.text = resources.getString(R.string.link, "JING SENT YOU 3 MESSAGES")
            notification3.text = resources.getString(R.string.link, "YOU MATCHED WITH JING")
            notification4.text = resources.getString(R.string.link, "WELCOME TO FANTASY DATING")
        }
    }

}
