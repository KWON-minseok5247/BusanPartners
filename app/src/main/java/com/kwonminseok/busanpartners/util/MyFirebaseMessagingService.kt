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

//        try {
//            if (FirebaseMessagingDelegate.handleRemoteMessage(remoteMessage)) {
//                // RemoteMessage was from Stream and it is already processed
//            } else {
//                // RemoteMessage wasn't sent from Stream and it needs to be handled by you
//            }
//        } catch (exception: IllegalStateException) {
//            // ChatClient was not initialized
//        }


        // 애플리케이션 컨텍스트를 가져옵니다.
        val context = applicationContext

        // 알림 관리자를 시스템 서비스로부터 가져옵니다.
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널을 설정합니다. Android Oreo 이상 필요.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "Channel Name",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Firebase 메시지에서 필요한 데이터를 추출합니다.
        val pushNotificationEnabled = true
        val ignorePushMessagesWhenUserOnline = true
        val pushDeviceGeneratorList: List<PushDeviceGenerator> = ArrayList()

        val notificationConfig = NotificationConfig(
            pushNotificationEnabled,
            pushDeviceGenerators = listOf(FirebasePushDeviceGenerator(providerName = "BusanPartners"))

        )

        customizeNotificationStyle(this,notificationConfig)









        // 얘가 진짜 제대로 알림을 전달하는 과정임.
         fun showNotification(channel: Channel, message: Message) {
            Log.e("MyNotificationHandler가 실행되나?", "제대로???")

            val notificationId = message.id.hashCode() // 알림 ID를 메시지 ID의 해시코드로 설정

            val notification = NotificationCompat.Builder(context, channel.id)
//            .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
                .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
                .setContentTitle("New message in ${channel.name}") // 알림 제목 설정
                .setContentText(message.text) // 메시지 텍스트 설정
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
                .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
                .build()

            notificationManager.notify(notificationId, notification)

//        val notification = NotificationCompat.Builder(context, notificationChannelId)
//            .build()
//        notificationManager.notify(notificationId, notification)

        }
        fun customizeNotificationStyle(context: Context, notificationConfig: NotificationConfig) {
            val notificationChannelId = ""
            val notificationId = 1

            class MyNotificationHandler(context: Context) : NotificationHandler {
                lateinit var notificationManager: NotificationManager


                init {
                    this@MyNotificationHandler.notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//                val notificationChannel: () -> NotificationChannel = {
//                    val channelId = "chat_channel"
//                    val channelName = "Chat Messages"
//                    val importance = NotificationManager.IMPORTANCE_HIGH
//                    NotificationChannel(channelId, channelName, importance).apply {
//                        description = "Notifications for chat messages"
//                    }
//                }
//                val d = NotificationHandlerFactory.createNotificationHandler(context,
//                    newMessageIntent = { message, channel ->
//                        HomeActivity.createLaunchIntent(
//                            context = context,
//                            messageId = message.id,
//                            parentMessageId = message.parentId,
//                            channelType = channel.type,
//                            channelId = channel.id
//                        )
//                    },
//                    notificationChannel = notificationChannel
//
//                )

                }

                override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
                    when (status) {
                        NotificationPermissionStatus.REQUESTED -> {}
                        NotificationPermissionStatus.GRANTED -> {}
                        NotificationPermissionStatus.DENIED -> {}
                        NotificationPermissionStatus.RATIONALE_NEEDED -> {}
                    }
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun showNotification(channel: Channel, message: Message) {
                    Log.e("MyNotificationHandler가 실행되나?", message.toString())


//        val notificationHandler = MyNotificationHandler(this)


//                val notificationId = message.id.hashCode() // 알림 ID를 메시지 ID의 해시코드로 설정
//                Log.e("notificationId 실행되나?", notificationId.toString())
//
//                val notification = NotificationCompat.Builder(context, channel.id)
////            .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
//                    .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
//                    .setContentTitle("New message in ${channel.name}") // 알림 제목 설정
//                    .setContentText(message.text) // 메시지 텍스트 설정
//                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
//                    .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
//                    .build()
//
//                notificationManager.notify(notificationId, notification)

                    val notification = NotificationCompat.Builder(context, channel.id)
                        .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정

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

            val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
            val statePluginFactory = StreamStatePluginFactory(
                config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
                appContext = this
            )
            val notificationHandler: NotificationHandler = MyNotificationHandler(this)

            chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
                .withPlugins(offlinePluginFactory, statePluginFactory)
                .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
                .notifications(notificationConfig, notificationHandler)
                .build()


        }


//        val channelId = remoteMessage.data["channel_id"] ?: ""
//        val channelType = remoteMessage.data["channel_type"] ?: ""
//        val messageId = remoteMessage.data["message_id"] ?: ""
//        val messageText = remoteMessage.data["message"] ?: ""
//        val channelName = remoteMessage.data["channel_name"] ?: "Unknown Channel" // 이 값을 어떻게 설정할지는 추가 정보가 필요합니다.
//
//        // Channel과 Message 객체 생성
//        val channel = Channel(id = channelId, type = channelType, name = channelName)
//        val message = Message(id = messageId, text = messageText)
//        Log.e("channel", channel.toString())
//        Log.e("message", message.toString())






//        val channelName = remoteMessage.data["sender"] ?: "Default Channel"
//        val messageId = remoteMessage.data["message_id"] ?: "1"
//        val messageText = remoteMessage.data["messaging"] ?: "You have a new message!"
//
//        // 알림 ID를 메시지 ID의 해시코드로 설정
//        val notificationId = messageId.hashCode()
//
//        // 알림을 생성합니다.
//        val notification = NotificationCompat.Builder(context, "channel_id")
//            .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
//            .setContentTitle("New message in $channelName") // 알림 제목 설정
//            .setContentText(messageText) // 메시지 텍스트 설정
//            .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
//            .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
//            .build()
//
//        // 알림을 발송합니다.
//        notificationManager.notify(notificationId, notification)

        handleNotification(remoteMessage)

//         ChatClient와 사용자 연결 확인
//        if (chatClient.getCurrentUser() == null) {
//            // 사용자 연결이 필요함
//            reconnectUser()
//            handleNotification(remoteMessage)
//
//        } else {
//            // 이미 연결된 상태에서 처리
//            handleNotification(remoteMessage)
//        }
    }
    fun customizeNotificationStyle(context: Context, notificationConfig: NotificationConfig) {
        val notificationChannelId = ""
        val notificationId = 1

        class MyNotificationHandler(context: Context) : NotificationHandler {
            var notificationManager: NotificationManager


            init {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//                val notificationChannel: () -> NotificationChannel = {
//                    val channelId = "chat_channel"
//                    val channelName = "Chat Messages"
//                    val importance = NotificationManager.IMPORTANCE_HIGH
//                    NotificationChannel(channelId, channelName, importance).apply {
//                        description = "Notifications for chat messages"
//                    }
//                }
//                val d = NotificationHandlerFactory.createNotificationHandler(context,
//                    newMessageIntent = { message, channel ->
//                        HomeActivity.createLaunchIntent(
//                            context = context,
//                            messageId = message.id,
//                            parentMessageId = message.parentId,
//                            channelType = channel.type,
//                            channelId = channel.id
//                        )
//                    },
//                    notificationChannel = notificationChannel
//
//                )

            }

            override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
                when (status) {
                    NotificationPermissionStatus.REQUESTED -> {}
                    NotificationPermissionStatus.GRANTED -> {}
                    NotificationPermissionStatus.DENIED -> {}
                    NotificationPermissionStatus.RATIONALE_NEEDED -> {}
                }
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun showNotification(channel: Channel, message: Message) {
                Log.e("MyNotificationHandler가 실행되나?", message.toString())


//        val notificationHandler = MyNotificationHandler(this)


//                val notificationId = message.id.hashCode() // 알림 ID를 메시지 ID의 해시코드로 설정
//                Log.e("notificationId 실행되나?", notificationId.toString())
//
//                val notification = NotificationCompat.Builder(context, channel.id)
////            .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
//                    .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
//                    .setContentTitle("New message in ${channel.name}") // 알림 제목 설정
//                    .setContentText(message.text) // 메시지 텍스트 설정
//                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
//                    .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
//                    .build()
//
//                notificationManager.notify(notificationId, notification)

                val notification = NotificationCompat.Builder(context, channel.id)
                    .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정

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

        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
            appContext = this
        )
        val notificationHandler: NotificationHandler = MyNotificationHandler(this)

        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .notifications(notificationConfig, notificationHandler)
            .build()


    }


    private fun handleNotification(remoteMessage: RemoteMessage) {
        // 알림 처리 로직
// 로그를 통해 알림 처리가 시작됐음을 확인
        Log.e("알림처리", "알림 처리 시작")

        // Firebase 메시지에서 데이터 추출
        remoteMessage.data.let {
            Log.e("mapamapmap", it.toString())
            val channelType = it["channel_type"]
            val channelId = it["channel_id"]
            val messageId = it["message_id"]
            val parentMessageId = it["parentMessageId"]
            Log.e("channelType", channelType.toString())
            Log.e("channelId", channelId.toString())
            Log.e("messageId", messageId.toString())
            Log.e("parentMessageId", parentMessageId.toString())


            // 필요한 정보가 모두 있는지 확인
            if (channelType != null && channelId != null && messageId != null) {
                val cid = "$channelType:$channelId"

                // 새로운 인텐트 생성하여 ChannelActivity 시작
                val newIntent = Intent(applicationContext, ChannelActivity::class.java).apply {
                    putExtra("key:cid", cid)
                    putExtra("EXTRA_MESSAGE_ID", messageId)
                    putExtra("EXTRA_PARENT_MESSAGE_ID", parentMessageId)
                }
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // 필요한 경우 새 태스크로 시작
                applicationContext.startActivity(newIntent)
            } else {
                Log.e("알림처리", "필요한 정보가 누락됨")
            }
        }
    }

}

