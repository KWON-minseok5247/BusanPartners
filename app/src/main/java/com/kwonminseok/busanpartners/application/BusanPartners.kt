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
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.BuildConfig.NAVER_CLIENT_ID
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
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
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

        TourismApiService.init(this)

        WorldTimeApiService.init(this)

        preferences = PreferenceUtil(applicationContext)

        // ChatClient 초기화
        initializeChatClient()
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
        val notificationConfig = NotificationConfig(
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
//                    .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
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
//        val notificationHandler = MyNotificationHandler(this)

        val notificationHandler = NotificationHandlerFactory.createNotificationHandler(
            context = this,
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
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
            appContext = this
        )

        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .notifications(notificationConfig, notificationHandler)
            .build()

        val guestUser = User(
            id = "5dtxXAmlQgcjAidXFY1bPuGPIjP2",
            name = "권민석",
            image = "https://firebasestorage.googleapis.com/v0/b/busanpartners-86b94.appspot.com/o/user%2F5dtxXAmlQgcjAidXFY1bPuGPIjP2%2FimagePath?alt=media&token=6167f5f4-8ee1-41b9-84e8-6249a8108bdc"
        )
        chatClient.connectUser(
            guestUser,
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNWR0eFhBbWxRZ2NqQWlkWEZZMWJQdUdQSWpQMiJ9.s6CURR7-mTKmZWKQDYNGelRQuja8nFg-pUBHlyadKeY"
        ).enqueue {
            if (it.isSuccess) {
                // 연결 성공
            } else {
                // 연결 실패 처리
            }
        }

    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 알림 처리
        if (ChatClient.instance().getCurrentUser() == null) {
            // ChatClient 초기화 및 사용자 연결 로직

            val guestUser = User(
                id = "5dtxXAmlQgcjAidXFY1bPuGPIjP2",
                name = "권민석",
                image = "https://firebasestorage.googleapis.com/v0/b/busanpartners-86b94.appspot.com/o/user%2F5dtxXAmlQgcjAidXFY1bPuGPIjP2%2FimagePath?alt=media&token=6167f5f4-8ee1-41b9-84e8-6249a8108bdc"
            )
            val client = ChatClient.Builder("api_key", context).build()
            client.connectUser(guestUser, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNWR0eFhBbWxRZ2NqQWlkWEZZMWJQdUdQSWpQMiJ9.s6CURR7-mTKmZWKQDYNGelRQuja8nFg-pUBHlyadKeY").enqueue { result ->
                if (result.isSuccess) {
                    // 연결 성공, 알림 처리 로직 수행
                    intent.extras?.let {
                        val channelType = it.getString(EXTRA_CHANNEL_TYPE)
                        val channelId = it.getString(EXTRA_CHANNEL_ID)
                        val messageId = it.getString(EXTRA_MESSAGE_ID)
                        val parentMessageId = it.getString(EXTRA_PARENT_MESSAGE_ID)

                        if (channelType != null && channelId != null && messageId != null) {
                            val cid = "$channelType:$channelId"

                            // 새로운 인텐트 생성하여 ChannelActivity 시작
                            val newIntent = Intent(context, ChannelActivity::class.java).apply {
                                putExtra("key:cid", cid)
                                putExtra(EXTRA_MESSAGE_ID, messageId)
                                putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
                            }
                            newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  // 필요한 경우 새 태스크로 시작
                            context.startActivity(newIntent)
                        }
                    }                } else {
                    // 연결 실패 처리
                    Log.e("ChatClient", "Failed to connect user")
                }
            }
        } else {
            // 이미 연결된 상태, 알림 처리 로직 수행
            intent.extras?.let {
                val channelType = it.getString(EXTRA_CHANNEL_TYPE)
                val channelId = it.getString(EXTRA_CHANNEL_ID)
                val messageId = it.getString(EXTRA_MESSAGE_ID)
                val parentMessageId = it.getString(EXTRA_PARENT_MESSAGE_ID)

                if (channelType != null && channelId != null && messageId != null) {
                    val cid = "$channelType:$channelId"

                    // 새로운 인텐트 생성하여 ChannelActivity 시작
                    val newIntent = Intent(context, ChannelActivity::class.java).apply {
                        putExtra("key:cid", cid)
                        putExtra(EXTRA_MESSAGE_ID, messageId)
                        putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
                    }
                    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  // 필요한 경우 새 태스크로 시작
                    context.startActivity(newIntent)
                }
            }        }

    }

    private fun connectUser(context: Context) {
        val client = ChatClient.instance()
        val guestUser = User(
            id = "5dtxXAmlQgcjAidXFY1bPuGPIjP2",
            name = "권민석",
            image = "https://firebasestorage.googleapis.com/v0/b/busanpartners-86b94.appspot.com/o/user%2F5dtxXAmlQgcjAidXFY1bPuGPIjP2%2FimagePath?alt=media&token=6167f5f4-8ee1-41b9-84e8-6249a8108bdc"
        )

        client.connectUser(
            guestUser,
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNWR0eFhBbWxRZ2NqQWlkWEZZMWJQdUdQSWpQMiJ9.s6CURR7-mTKmZWKQDYNGelRQuja8nFg-pUBHlyadKeY"
        ).enqueue {
            if (it.isSuccess) {
                // 연결 성공
            } else {
                // 연결 실패 처리
            }
        }
    }

//    private fun handlePushNotification(intent: Intent) {
//        intent.extras?.let {
//            val channelType = it.getString(EXTRA_CHANNEL_TYPE)
//            val channelId = it.getString(EXTRA_CHANNEL_ID)
//            val messageId = it.getString(EXTRA_MESSAGE_ID)
//            val parentMessageId = it.getString(EXTRA_PARENT_MESSAGE_ID)
//
//            if (channelType != null && channelId != null && messageId != null) {
//                val cid = "$channelType:$channelId"
//
//                // 새로운 인텐트 생성하여 ChannelActivity 시작
//                val newIntent = Intent(context, ChannelActivity::class.java).apply {
//                    putExtra("key:cid", cid)
//                    putExtra(EXTRA_MESSAGE_ID, messageId)
//                    putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
//                }
//                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  // 필요한 경우 새 태스크로 시작
//                context.startActivity(newIntent)
//            }
//        }
//    }
}
