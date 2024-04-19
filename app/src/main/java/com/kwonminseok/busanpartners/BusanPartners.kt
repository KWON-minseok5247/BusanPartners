package com.kwonminseok.busanpartners

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kwonminseok.busanpartners.BuildConfig.NAVER_CLIENT_ID
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.api.WorldTimeApiService
import com.kwonminseok.busanpartners.api.WorldTimeResponse
import com.kwonminseok.busanpartners.mainScreen.home.BusanFestivalApiService
import com.kwonminseok.busanpartners.util.MyNotificationHandler
import com.kwonminseok.busanpartners.util.PreferenceUtil
import com.kwonminseok.busanpartners.util.Push
import com.kwonminseok.busanpartners.viewmodel.TimeViewModel
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
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
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Hilt를 사용하기 위해서 여기서 힐트를 추가한다.
@HiltAndroidApp
class BusanPartners: Application() {

    companion object{
        lateinit var preferences: PreferenceUtil
        lateinit var chatClient: ChatClient


        lateinit var worldTimeApi: WorldTimeApiService
//        var currentTime: WorldTimeResponse? = null

    }
    override fun onCreate() {
//        BusanFestivalApiService.init(this)


        TourismApiService.init(this)

        WorldTimeApiService.init(this)

        preferences = PreferenceUtil(applicationContext)
        super.onCreate()

        // ChatClient 초기화
        initializeChatClient()

        // 24버전 서버 시간
        AndroidThreeTen.init(this)




        // 네이버 지도
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID)




    }
    private fun initializeChatClient() {
        val notificationConfig = NotificationConfig(
            pushDeviceGenerators = listOf(FirebasePushDeviceGenerator(providerName = "BusanPartners"))

        )
        val notificationHandler = object : NotificationHandler {
            var notificationManager: NotificationManager
            init {
                notificationManager =
                    this@BusanPartners.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            override fun dismissAllNotifications() {
            }
            override fun dismissChannelNotifications(channelType: String, channelId: String) {
            }
            override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
                when (status) {
                    NotificationPermissionStatus.REQUESTED -> {
                        // invoked when POST_NOTIFICATIONS permission is requested
                    }
                    NotificationPermissionStatus.GRANTED -> {
                        // invoked when POST_NOTIFICATIONS permission is granted
                    }
                    NotificationPermissionStatus.DENIED -> {
                        // invoked when POST_NOTIFICATIONS permission is denied
                    }
                    NotificationPermissionStatus.RATIONALE_NEEDED -> {
                        // invoked when POST_NOTIFICATIONS permission requires rationale
                    }
                }            }

            override fun showNotification(channel: Channel, message: Message) {
                val notificationId = message.id.hashCode() // 알림 ID를 메시지 ID의 해시코드로 설정

                val notification = NotificationCompat.Builder(this@BusanPartners, channel.id)
                    .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
                    .setContentTitle("New message in ${channel.name}") // 알림 제목 설정
                    .setContentText(message.text) // 메시지 텍스트 설정
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
                    .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
                    .build()

                notificationManager.notify(notificationId, notification)
            }
        }

        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
            appContext = this
        )

        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .notifications(notificationConfig,notificationHandler)
            .build()
    }



//    private fun fetchCurrentTime() {
//        // Global scope는 일반적으로 권장되지 않지만, Application context에서는 사용 가능합니다.
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                currentTime = worldTimeApi.getSeoulTime()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}