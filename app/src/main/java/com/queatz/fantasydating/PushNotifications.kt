package com.queatz.fantasydating

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.queatz.fantasydating.features.NavigationFeature
import com.queatz.fantasydating.features.TopFeature
import com.queatz.on.On

open class PushNotification constructor(val action: String)

data class MessagePushNotification constructor(
    val name: String,
    val id: String,
    val message: String
) : PushNotification("message")

data class BossPushNotification constructor(
    val reports: Int,
    val approvals: Int
) : PushNotification("boss")

class PushNotifications constructor(private val on: On) {

    fun handle(data: Map<String, String>) {
        if (data.containsKey("action")) {
            when (data["action"]) {
                "message" -> {
                    showMessageNotification(on<Json>().from(on<Json>().to(data), MessagePushNotification::class))
                }
                "boss" -> {
                    showBossNotification(on<Json>().from(on<Json>().to(data), BossPushNotification::class))
                }
            }
        }
    }

    private fun showBossNotification(boss: BossPushNotification) {
        val context = on<ContextFeature>().context

        val intent = Intent(context, MainActivity::class.java)

        val contentIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_NOTIFICATION,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        show(contentIntent, null, null,
            context.getString(R.string.app_name),
            "${boss.approvals} Approvals, ${boss.reports} Reports",
            "boss", false)
    }

    private fun showMessageNotification(message: MessagePushNotification) {
        if (on<TopFeature>().topPerson == message.id) {
            on<TopFeature>().caught()
            return
        }

        val context = on<ContextFeature>().context

        val intent = Intent(context, MessagesActivity::class.java)
        intent.action = Intent.ACTION_VIEW

        intent.putExtra(NavigationFeature.ExtraPersonId, message.id)

        val contentIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_NOTIFICATION,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val remoteInput = RemoteInput.Builder(Background.ResultKeyMessage)
            .setLabel(context.getString(R.string.reply))
            .build()

        val backgroundIntent = Intent(context, Background::class.java)
        backgroundIntent.putExtra(Background.ExtraPersonId, message.id)


        show(contentIntent, backgroundIntent, remoteInput,
            message.name,
            message.message,
            "${message.id}/message", true)
    }

    fun hide(notificationTag: String) {
        val notificationManager = NotificationManagerCompat.from(on<ContextFeature>().context)
        notificationManager.cancel(notificationTag, NOTIFICATION_ID)
    }

    private fun show(contentIntent: PendingIntent, backgroundIntent: Intent?,
                     remoteInput: RemoteInput?,
                     name: String,
                     message: String,
                     notificationTag: String,
                     sound: Boolean) {
        val context = on<ContextFeature>().context

        val channel = NotificationChannel(
            notificationChannel(),
            context.getString(R.string.notifications_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lightColor = context.getColor(R.color.colorPrimary)
            enableLights(true)
            vibrationPattern = longArrayOf(200)
            enableVibration(true)
        }

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, notificationChannel()).also {
            if (sound) {
                it.setDefaults(Notification.DEFAULT_ALL)
            } else {
                it.setDefaults(Notification.DEFAULT_LIGHTS)
            }

            it.setSmallIcon(R.drawable.icon)
            it.setContentTitle(name)
            it.setContentText(message)
            it.setAutoCancel(true)
            it.setLights(context.getColor(R.color.colorPrimary), 1000, 1000)
            it.setContentIntent(contentIntent)
        }

        if (remoteInput != null) {
            val replyPendingIntent = PendingIntent.getBroadcast(context,
                REQUEST_CODE_NOTIFICATION,
                backgroundIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            val action = NotificationCompat.Action.Builder(R.drawable.icon,
                on<ContextFeature>().context.getString(R.string.reply), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

            builder.addAction(action)
        }

        val newMessageNotification = builder.build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationTag, NOTIFICATION_ID, newMessageNotification)
    }

    private fun notificationChannel(): String {
        return on<ContextFeature>().context.getString(R.string.notification_channel)
    }

    companion object {
        private const val NOTIFICATION_ID = 0
        private const val REQUEST_CODE_NOTIFICATION = 101
    }
}