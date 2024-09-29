package com.kwonminseok.newbusanpartners.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kwonminseok.newbusanpartners.BuildConfig
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.api.TourismAllInOneApiService
import com.kwonminseok.newbusanpartners.api.WorldTimeApiService
import com.kwonminseok.newbusanpartners.db.AppDatabase
import com.kwonminseok.newbusanpartners.ui.login.SplashActivity
import com.kwonminseok.newbusanpartners.util.PreferenceUtil
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
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
import kotlin.system.exitProcess

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

        // 앱이 오랫동안 작동을 안하다가 들어갔을 경우
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(thread, throwable)
        }

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
////        preferences = PreferenceUtil(applicationContext)
////
////        FirebaseApp.initializeApp(this)
//////        TourismApiService.init(this)
////        TourismAllInOneApiService.init(this)
////
////        WorldTimeApiService.init(this)
////
////        applySavedLocale()
////
////        // ChatClient 초기화
////        initializeNotificationChannel()
////        initializeChatClient()
//////        setupNotificationChannels(this)
////
////
////        // 24버전 서버 시간
////        AndroidThreeTen.init(this)
////
//////        AppDatabase.getDatabase(this)
////
////        // 네이버 지도
////        NaverMapSdk.getInstance(this).client =
////            NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID)

        try {
            FirebaseApp.initializeApp(this)
            preferences = PreferenceUtil(applicationContext)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            TourismAllInOneApiService.init(this)
            WorldTimeApiService.init(this)
            applySavedLocale()
            initializeNotificationChannel()
            initializeChatClient()
            AndroidThreeTen.init(this)
            NaverMapSdk.getInstance(this).client =
                NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_CLIENT_ID)
        } catch (e: Exception) {
            Log.e("BusanPartners", "Initialization error", e)
        }
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
            .logLevel(ChatLogLevel.DEBUG) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .notifications(notificationConfig, d)
            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING)
            .build()

    }

    private fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        Log.e("BusanPartners", "Uncaught exception: ${throwable.message}", throwable)

        // HomeActivity 재시작
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

        // 앱을 종료합니다.
        exitProcess(1)
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


