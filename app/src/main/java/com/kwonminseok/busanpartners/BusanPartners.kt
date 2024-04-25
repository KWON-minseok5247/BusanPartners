package com.kwonminseok.busanpartners

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kwonminseok.busanpartners.BuildConfig.NAVER_CLIENT_ID
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.api.WorldTimeApiService
import com.kwonminseok.busanpartners.db.AppDatabase
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import com.kwonminseok.busanpartners.util.PreferenceUtil
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.android.push.permissions.NotificationPermissionStatus
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

// Hilt를 사용하기 위해서 여기서 힐트를 추가한다.
@HiltAndroidApp
class BusanPartners: Application() {

    companion object{
        lateinit var preferences: PreferenceUtil
        lateinit var chatClient: ChatClient
        lateinit var db: AppDatabase
        lateinit var worldTimeApi: WorldTimeApiService
//        var currentTime: WorldTimeResponse? = null

    }
    override fun onCreate() {
//        BusanFestivalApiService.init(this)
        super.onCreate()

        TourismApiService.init(this)

        WorldTimeApiService.init(this)

        preferences = PreferenceUtil(applicationContext)

        // ChatClient 초기화
        initializeChatClient()

        // 24버전 서버 시간
        AndroidThreeTen.init(this)

//        AppDatabase.getDatabase(this)

        // 네이버 지도
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID)




    }
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

        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
            appContext = this
        )

        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .notifications(notificationConfig)
            .build()
    }

}