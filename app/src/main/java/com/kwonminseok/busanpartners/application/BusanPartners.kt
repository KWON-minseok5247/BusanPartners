package com.kwonminseok.busanpartners.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.BuildConfig.NAVER_CLIENT_ID
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.api.WorldTimeApiService
import com.kwonminseok.busanpartners.db.AppDatabase
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_ID
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_TYPE
import com.kwonminseok.busanpartners.ui.EXTRA_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.EXTRA_PARENT_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import com.kwonminseok.busanpartners.util.MyNotificationHandler
import com.kwonminseok.busanpartners.util.PreferenceUtil
import com.kwonminseok.busanpartners.util.Push
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
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
import io.getstream.chat.android.models.Device
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.PushMessage
import io.getstream.chat.android.models.PushProvider
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

// Hilt를 사용하기 위해서 여기서 힐트를 추가한다.
@HiltAndroidApp
class BusanPartners : Application() {
//    val chatInitializer = ChatInitializer(context = this, autoTranslationEnabled = true)

    companion object {
        lateinit var preferences: PreferenceUtil
        lateinit var chatClient: ChatClient
        lateinit var db: AppDatabase
        lateinit var worldTimeApi: WorldTimeApiService
//        var currentTime: WorldTimeResponse? = null

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
//        BusanFestivalApiService.init(this)
        super.onCreate()
        FirebaseApp.initializeApp(this)

        TourismApiService.init(this)

        WorldTimeApiService.init(this)

        preferences = PreferenceUtil(applicationContext)

        // ChatClient 초기화
        initializeChatClient()
        setupNotificationChannels(this)

//        val pushNotificationEnabled = true
//        val ignorePushMessagesWhenUserOnline = true
//        val pushDeviceGeneratorList: List<PushDeviceGenerator> = ArrayList()
//
//        val notificationConfig = NotificationConfig(
//            pushNotificationEnabled,
//            pushDeviceGenerators = listOf(FirebasePushDeviceGenerator(providerName = "BusanPartners"))
//
//        )

//        customizeNotificationStyle(this, notificationConfig)
//        customizeNotificationStyle(this,notificationConfig )
//        chatInitializer.init(BuildConfig.API_KEY)

        // 24버전 서버 시간
        AndroidThreeTen.init(this)

//        AppDatabase.getDatabase(this)

        // 네이버 지도
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeChatClient() {
        val pushNotificationEnabled = true
        val ignorePushMessagesWhenUserOnline = true
        val pushDeviceGeneratorList: List<PushDeviceGenerator> = ArrayList()

        val notificationConfig = NotificationConfig(
            pushNotificationEnabled,
            pushDeviceGenerators = listOf(FirebasePushDeviceGenerator(providerName = "BusanPartners"))

        )
//        val notificationHandler = NotificationHandlerFactory.createNotificationHandler(
//            context = this,
//            newMessageIntent = {
//                    message: Message,
//                    channel: Channel,
//                ->
//                // Return the intent you want to be triggered when the notification is clicked
//                val intent: Intent = Intent(this, ChannelActivity::class.java).addFlags(
//                        Intent.FLAG_ACTIVITY_NEW_TASK or
//                                Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    )
//                startActivity(intent)
//                intent
//            }
//        )

//        val notificationHandler = object : NotificationHandler {
//            var notificationManager: NotificationManager
//            init {
//                notificationManager =
//                    this@BusanPartners.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            }
//            override fun dismissAllNotifications() {
//            }
//            override fun dismissChannelNotifications(channelType: String, channelId: String) {
//            }
//            override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
//                when (status) {
//                    NotificationPermissionStatus.REQUESTED -> {
//                        // invoked when POST_NOTIFICATIONS permission is requested
//                    }
//                    NotificationPermissionStatus.GRANTED -> {
//                        // invoked when POST_NOTIFICATIONS permission is granted
//                    }
//                    NotificationPermissionStatus.DENIED -> {
//                        // invoked when POST_NOTIFICATIONS permission is denied
//                    }
//                    NotificationPermissionStatus.RATIONALE_NEEDED -> {
//                        // invoked when POST_NOTIFICATIONS permission requires rationale
//                    }
//                }
//            }
//
//            override fun showNotification(channel: Channel, message: Message) {
//                val notificationId = message.id.hashCode() // 알림 ID를 메시지 ID의 해시코드로 설정
//
//                val notification = NotificationCompat.Builder(this@BusanPartners, channel.id)
//                    .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
//                    .setContentTitle("New message in ${channel.name}") // 알림 제목 설정
//                    .setContentText(message.text) // 메시지 텍스트 설정
//                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
//                    .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
//                    .build()
//
//                notificationManager.notify(notificationId, notification)
//            }
//        }

        val notificationChannel: () -> NotificationChannel = {
            val channelId = "chat_channel"
            val channelName = "Chat Messages"
            val importance = NotificationManager.IMPORTANCE_HIGH
            NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for chat messages"
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = this.getSystemService(NotificationManager::class.java)
            // 기존 채널 조회 및 삭제 + 그리고 일단 삭제는 해놨는데 결과가 어떻게 될지는 모르겠다
            // 삭제하는 방법 말고 완전 비활성화하는 방법도 괜찮아보이긴 함.
            val existingChannel: NotificationChannel? = manager.getNotificationChannel(this.getString(R.string.stream_chat_other_notifications_channel_id))
            if (existingChannel != null) {
                manager.deleteNotificationChannel(existingChannel.id)
//                existingChannel.importance = NotificationManager.IMPORTANCE_NONE
//                manager.createNotificationChannel(existingChannel) // 변경사항 업데이트

                Log.e("R.string.stream_chat_other_notifications_channel_id)",R.string.stream_chat_other_notifications_channel_id.toString())

            }
        }

//        val notificationHandler = MyNotificationHandler(this)
        val d = NotificationHandlerFactory.createNotificationHandler(
            this,
            newMessageIntent = { message, channel ->
                HomeActivity.createLaunchIntent(
                    context = this,
                    messageId = message.id,
                    parentMessageId = message.parentId,
                    channelType = channel.type,
                    channelId = channel.id
                )
            },
            notificationChannel = notificationChannel
        )

//        val notificationHandler = NotificationHandlerFactory.createNotificationHandler(
//            context = this,
//            newMessageIntent = { message, channel ->
//                HomeActivity.createLaunchIntent(
//                    context = this,
//                    messageId = message.id,
//                    parentMessageId = message.parentId,
//                    channelType = channel.type,
//                    channelId = channel.id
//                )
//            },
//            notificationChannel = notificationChannel
//        )
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
            appContext = this
        )

        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .notifications(notificationConfig, d)
            .build()
    }
    fun setupNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            // 기존 채널 조회 및 삭제 + 그리고 일단 삭제는 해놨는데 결과가 어떻게 될지는 모르겠다
            // 삭제하는 방법 말고 완전 비활성화하는 방법도 괜찮아보이긴 함.
            val existingChannel: NotificationChannel? = manager.getNotificationChannel(context.getString(R.string.stream_chat_other_notifications_channel_id))
            if (existingChannel != null) {
//                manager.deleteNotificationChannel(existingChannel.id)
                existingChannel.importance = NotificationManager.IMPORTANCE_NONE
                manager.createNotificationChannel(existingChannel) // 변경사항 업데이트

                Log.e("R.string.stream_chat_other_notifications_channel_id)",R.string.stream_chat_other_notifications_channel_id.toString())

            }
        }
    }


//    fun customizeNotificationStyle(context: Context, notificationConfig: NotificationConfig) {
//        val notificationChannelId = ""
//        val notificationId = 1
//
//        class MyNotificationHandler(context: Context) : NotificationHandler {
//            var notificationManager: NotificationManager
//
//
//            init {
//                notificationManager =
//                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
////                val notificationChannel: () -> NotificationChannel = {
////                    val channelId = "chat_channel"
////                    val channelName = "Chat Messages"
////                    val importance = NotificationManager.IMPORTANCE_HIGH
////                    NotificationChannel(channelId, channelName, importance).apply {
////                        description = "Notifications for chat messages"
////                    }
////                }
////                val d = NotificationHandlerFactory.createNotificationHandler(context,
////                    newMessageIntent = { message, channel ->
////                        HomeActivity.createLaunchIntent(
////                            context = context,
////                            messageId = message.id,
////                            parentMessageId = message.parentId,
////                            channelType = channel.type,
////                            channelId = channel.id
////                        )
////                    },
////                    notificationChannel = notificationChannel
////
////                )
//
//            }
//
//            override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
//                when (status) {
//                    NotificationPermissionStatus.REQUESTED -> {}
//                    NotificationPermissionStatus.GRANTED -> {}
//                    NotificationPermissionStatus.DENIED -> {}
//                    NotificationPermissionStatus.RATIONALE_NEEDED -> {}
//                }
//            }
//
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun showNotification(channel: Channel, message: Message) {
//                Log.e("MyNotificationHandler가 실행되나?", message.toString())
//
//
////        val notificationHandler = MyNotificationHandler(this)
//
//
////                val notificationId = message.id.hashCode() // 알림 ID를 메시지 ID의 해시코드로 설정
////                Log.e("notificationId 실행되나?", notificationId.toString())
////
////                val notification = NotificationCompat.Builder(context, channel.id)
//////            .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
////                    .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
////                    .setContentTitle("New message in ${channel.name}") // 알림 제목 설정
////                    .setContentText(message.text) // 메시지 텍스트 설정
////                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
////                    .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
////                    .build()
////
////                notificationManager.notify(notificationId, notification)
//
//                val notification = NotificationCompat.Builder(context, channel.id)
//                    .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
//
//                    .build()
//                notificationManager.notify(notificationId, notification)
//            }
//
//            override fun dismissChannelNotifications(channelType: String, channelId: String) {
//                // Dismiss all notification related with this channel
//            }
//
//            override fun dismissAllNotifications() {
//                // Dismiss all notifications
//            }
//
//            override fun onChatEvent(event: NewMessageEvent): Boolean {
//                return true
//            }
//
//            override fun onPushMessage(message: PushMessage): Boolean {
//                return false
//            }
//        }
//
//        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
//        val statePluginFactory = StreamStatePluginFactory(
//            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
//            appContext = this
//        )
//        val notificationHandler: NotificationHandler = MyNotificationHandler(this)
//
//        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
//            .withPlugins(offlinePluginFactory, statePluginFactory)
//            .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
//            .notifications(notificationConfig, notificationHandler)
//            .build()
//
//
//    }

}


