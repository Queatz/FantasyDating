package com.queatz.fantasydating

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.queatz.on.On


class Background : BroadcastReceiver() {

    private val on = On()

    companion object {
        const val ExtraPersonId = "person"
        const val ResultKeyMessage = "message"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        intent ?: return

        on<ContextFeature>().context = context

        if (intent.hasExtra(ExtraPersonId)) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null) {
                val person = intent.getStringExtra(ExtraPersonId)
                val replyMessage = remoteInput.getCharSequence(ResultKeyMessage)

                if (replyMessage == null || replyMessage.isEmpty()) {
                    return
                }

                if (person == null) {
                    return
                }

                on<Api>().sendMessage(person, MessageRequest(replyMessage.toString())) { successResult ->
                    if (successResult.success) {
                        on<Say>().say(R.string.message_sent)
                        on<PushNotifications>().hide("${person}/message")
                    } else {
                        on<Say>().say(R.string.message_not_sent)
                    }
                } error { on<Say>().say(R.string.message_not_sent) }
            }
        }
    }
}
