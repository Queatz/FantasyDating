package com.queatz.fantasydating

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.queatz.fantasydating.features.NotificationFeature
import com.queatz.on.On

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val on = On()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val on = On()
        on<ContextFeature>().context = application
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            on<PushNotifications>().handle(data)
        }
    }

    override fun onNewToken(token: String) {
        on<NotificationFeature>().set(token)
    }
}