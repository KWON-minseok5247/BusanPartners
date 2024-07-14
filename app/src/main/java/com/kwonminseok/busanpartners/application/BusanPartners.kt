package com.kwonminseok.busanpartners.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.util.Log
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
import java.util.Locale

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

    override fun onCreate() {
//        BusanFestivalApiService.init(this)
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        preferences = PreferenceUtil(applicationContext)

        FirebaseApp.initializeApp(this)

//        TourismApiService.init(this)
        TourismAllInOneApiService.init(this)

        WorldTimeApiService.init(this)

        applySavedLocale()

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
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun initializeChatClient() {
        val pushNotificationEnabled = true

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
            notificationChannel = notificationChannel,
        )


        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
            appContext = this
        )

        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.NOTHING) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .notifications(notificationConfig, d)
            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING)
            .build()

    }

    private fun applySavedLocale() {
        val localeString = preferences.getString("selected_locale", "")
        val locale = if (localeString.isEmpty()) {
            Log.e("localeString null", Locale.getDefault().toString())
            Locale.getDefault()
        } else {
            Log.e("localeString", localeString)
            Locale.forLanguageTag(localeString)
        }

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}


