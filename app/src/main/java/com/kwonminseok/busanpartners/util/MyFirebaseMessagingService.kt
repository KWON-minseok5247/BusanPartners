package com.kwonminseok.busanpartners.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import io.getstream.android.push.PushDeviceGenerator
import io.getstream.android.push.firebase.FirebaseMessagingDelegate
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.android.push.permissions.NotificationPermissionStatus
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.PushMessage
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
//        super.onNewToken(token)
        // Update device's token on Stream backend
        try {
            FirebaseMessagingDelegate.registerFirebaseToken(token, providerName = "BusanPartners")
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        try {
            if (FirebaseMessagingDelegate.handleRemoteMessage(remoteMessage)) {
                // RemoteMessage was from Stream and it is already processed
            } else {
                // RemoteMessage wasn't sent from Stream and it needs to be handled by you
            }
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
        }
    }


}

