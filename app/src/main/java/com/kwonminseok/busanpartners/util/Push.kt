package com.kwonminseok.busanpartners.util

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kwonminseok.busanpartners.BuildConfig
import io.getstream.android.push.PushDeviceGenerator
import io.getstream.android.push.firebase.FirebaseMessagingDelegate.handleRemoteMessage
import io.getstream.android.push.firebase.FirebaseMessagingDelegate.registerFirebaseToken
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.android.push.permissions.NotificationPermissionStatus
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory.createNotificationHandler
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Device
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.PushMessage
import io.getstream.chat.android.models.PushProvider


/**
 * @see [Push Notifications](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/)
 */
class Push {
    /**
     * @see [Overview](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/.overview)
     */
    fun configureNotification(context: Context?, notificationHandler: NotificationHandler?) {
        val pushNotificationEnabled = true
        val ignorePushMessagesWhenUserOnline = true
        val pushDeviceGeneratorList: List<PushDeviceGenerator> = ArrayList()
        val notificationConfig = NotificationConfig(
            pushNotificationEnabled,
            listOf(FirebasePushDeviceGenerator(providerName = "BusanPartners")),
        )
        if (context != null) {
            if (notificationHandler != null) {
                ChatClient.Builder(BuildConfig.API_KEY, context)
                    .notifications(notificationConfig, notificationHandler)
                    .build()
            }
        }
    }

    /**
     * @see [Using our NotificationHandlerFactory](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/.using-our-notificationhandlerfactory)
     */
    fun customNotificationHandler(context: Context?) {
        val pushNotificationEnabled = true
        val ignorePushMessagesWhenUserOnline = true
        val pushDeviceGeneratorList: List<PushDeviceGenerator> = ArrayList()
        val notificationConfig = NotificationConfig(
            pushNotificationEnabled,
            listOf(FirebasePushDeviceGenerator(providerName = "BusanPartners"))
        )
        val notificationHandler = createNotificationHandler(
            context!!, { message: Message?, channel: Channel? ->
                // Return the intent you want to be triggered when the notification is clicked
                val intent = Intent()
                intent
            })
        ChatClient.Builder(BuildConfig.API_KEY, context)
            .notifications(notificationConfig, notificationHandler)
            .build()
    }

    /**
     * @see [Customizing Notification Style](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/.customize-notification-style)
     */
    fun customizeNotificationStyle(context: Context, notificationConfig: NotificationConfig?) {
        val notificationChannelId = ""
        val notificationId = 1

        class MyNotificationHandler(context: Context) : NotificationHandler {
            var notificationManager: NotificationManager

            init {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
                when (status) {
                    NotificationPermissionStatus.REQUESTED -> {}
                    NotificationPermissionStatus.GRANTED -> {}
                    NotificationPermissionStatus.DENIED -> {}
                    NotificationPermissionStatus.RATIONALE_NEEDED -> {}
                }
            }

            override fun showNotification(channel: Channel, message: Message) {
                val notification = NotificationCompat.Builder(context, notificationChannelId)
                    .build()
                notificationManager.notify(notificationId, notification)
            }

            override fun dismissChannelNotifications(channelType: String, channelId: String) {
                // Dismiss all notification related with this channel
            }

            override fun dismissAllNotifications() {
                // Dismiss all notifications
            }

            override fun onChatEvent(event: NewMessageEvent): Boolean {
                return true
            }

            override fun onPushMessage(message: PushMessage): Boolean {
                return false
            }
        }

        val notificationHandler: NotificationHandler = MyNotificationHandler(context)
        if (notificationConfig != null) {
            ChatClient.Builder(BuildConfig.API_KEY, context)
                .notifications(notificationConfig, notificationHandler)
                .build()
        }
    }

    /**
     * @see [Dismissing Notifications](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/.dismissing-notifications)
     */
    fun dismissingNotifications() {
        ChatClient.instance().dismissChannelNotifications("messaging", "general")
    }

    /**
     * @see [Multi Bundle](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/.multi-bundle)
     */
    fun multiBundle() {
        Device(
            "token-generated-by-provider", PushProvider.FIREBASE,  // your push provider
            "providerName"
        )
    }

    /**
     * @see [Firebase Cloud Messaging](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/firebase/)
     */
    internal inner class Firebase {
        /**
         * @see [Receiving Notifications in the Client](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/firebase/.receiving-notifications-in-the-client)
         */
        fun configureFirebaseNotifications(context: Context?) {
            val pushNotificationEnabled = true
            val ignorePushMessagesWhenUserOnline = true
//            val pushDeviceGeneratorList = listOf<PushDeviceGenerator>(
//                FirebasePushDeviceGenerator(
//                    "providerName"
//                )
//            )
            val notificationConfig = NotificationConfig(
                pushNotificationEnabled,
                listOf(FirebasePushDeviceGenerator(providerName = "BusanPartners"))
            )
            if (context != null) {
                ChatClient.Builder(BuildConfig.API_KEY, context)
                    .notifications(notificationConfig)
                    .build()
            }
        }

        /**
         * @see [Using a Custom Firebase Messaging Service](https://getstream.io/chat/docs/sdk/android/client/guides/push-notifications/firebase/.using-a-custom-firebase-messaging-service)
         */
        inner class CustomFirebaseMessagingService : FirebaseMessagingService() {
            override fun onNewToken(token: String) {
                // Update device's token on Stream backend
                try {
                    registerFirebaseToken(token, "providerName")
                } catch (exception: IllegalStateException) {
                    // ChatClient was not initialized
                }
            }

            override fun onMessageReceived(message: RemoteMessage) {
                try {
                    if (handleRemoteMessage(message)) {
                        // RemoteMessage was from Stream and it is already processed
                    } else {
                        // RemoteMessage wasn't sent from Stream and it needs to be handled by you
                    }
                } catch (exception: IllegalStateException) {
                    // ChatClient was not initialized
                }
            }
        }
    }
}
