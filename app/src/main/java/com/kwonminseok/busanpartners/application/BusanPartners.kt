package com.kwonminseok.busanpartners.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.BuildConfig.NAVER_CLIENT_ID
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.api.WorldTimeApiService
import com.kwonminseok.busanpartners.db.AppDatabase
import com.kwonminseok.busanpartners.ui.login.SplashActivity
import com.kwonminseok.busanpartners.util.PreferenceUtil
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import io.getstream.android.push.PushDeviceGenerator
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.models.UploadAttachmentsNetworkType
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        FirebaseApp.initializeApp(this)

//        TourismApiService.init(this)
        TourismAllInOneApiService.init(this)

        WorldTimeApiService.init(this)

        preferences = PreferenceUtil(applicationContext)

        // ChatClient 초기화
        initializeNotificationChannel()
        initializeChatClient()
//        setupNotificationChannels(this)


        // 24버전 서버 시간
        AndroidThreeTen.init(this)

//        AppDatabase.getDatabase(this)

        // 네이버 지도
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID)

    }

    private fun initializeNotificationChannel() {
        val channelId = "chat_channel"
        val channelName = getString(R.string.chat_message)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = getString(R.string.chat_message_info)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun initializeChatClient() {
        val pushNotificationEnabled = true
        val ignorePushMessagesWhenUserOnline = true
        val pushDeviceGeneratorList: List<PushDeviceGenerator> = ArrayList()


        val notificationConfig = NotificationConfig(
            pushNotificationEnabled,
            pushDeviceGenerators = listOf(FirebasePushDeviceGenerator(providerName = "BusanPartners")),

            )



        val notificationChannel: () -> NotificationChannel = {
            val channelId = "chat_channel"
//            val channelId = this.getString(R.string.stream_chat_other_notifications_channel_id)
            val channelName = getString(R.string.chat_message)
            val importance = NotificationManager.IMPORTANCE_HIGH
            NotificationChannel(channelId, channelName, importance).apply {
                description = getString(R.string.chat_message_info)
            }

//            val importance = NotificationManager.IMPORTANCE_HIGH
//            NotificationChannel(channelId, channelName, importance).apply {
//                if (channelId == "stream_GetStreamClientOther") {
//
//
//                }
//                description = "Notifications for chat messages"
//            }


        }
//        val notificationHandler = CustomNotificationHandlerFactory.createNotificationHandler(
//            this,
//            newMessageIntent = { message, channel ->
//                HomeActivity.createLaunchIntent(
//                    context = this,
//                    messageId = message.id,
//                    parentMessageId = message.parentId,
//                    channelType = channel.type,
//                    channelId = channel.id
//                )
//            },
//
//            notificationChannel = notificationChannel
//        )
//        val c = CustomNotificationHandler(this)


//        val notificationHandler = MyNotificationHandler(this)

        val d = NotificationHandlerFactory.createNotificationHandler(
            this,
            newMessageIntent = { message, channel ->
                SplashActivity.createLaunchIntent(
                    context = this,
                    messageId = message.id,
                    parentMessageId = message.parentId,
                    channelType = channel.type,
                    channelId = channel.id
                )
            },
            notificationChannel = notificationChannel
        )


        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
            appContext = this
        )

        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .notifications(notificationConfig, d)
            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING)
            .build()
    }
//    fun setupNotificationChannels(context: Context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "stream_GetStreamClientOther"
//            val channelName = "기타 알림?"
//            val notificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager
//
//            // 기존 채널 삭제
////            notificationManager.deleteNotificationChannel(channelId)
////            notificationManager.cancel()
//
//            // 새로운 채널 생성, 중요도를 IMPORTANCE_NONE으로 설정
//            val newChannel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_NONE
//            ).apply {
//                description = "알림이 사용자에게 전혀 보이지 않도록 설정된 채널"
//            }
//
//            // 새로운 채널 등록
//            notificationManager.createNotificationChannel(newChannel)
//        }
//
//    }


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


