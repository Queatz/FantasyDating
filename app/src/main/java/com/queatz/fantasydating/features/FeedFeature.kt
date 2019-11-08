package com.queatz.fantasydating.features

import android.util.Log
import com.queatz.fantasydating.Api
import com.queatz.fantasydating.Json
import com.queatz.fantasydating.MessageRequest
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

            notification1.setOnClickListener {
                on<Api>().sendMessage("75190185", MessageRequest(
                    "Love you honey"
                )) {
                    Log.d("KOTLIN HTTP MAGIC", "send message = " + on<Json>().to(it))
                }
            }

            notification2.setOnClickListener {
                on<Api>().sendMessage("75042189", MessageRequest(
                    "Love you honey"
                )) {
                    Log.d("KOTLIN HTTP MAGIC", "send message = " + on<Json>().to(it))
                }
            }
        }
    }
}
