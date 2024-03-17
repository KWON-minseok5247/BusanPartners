package com.kwonminseok.busanpartners

import android.app.Application
import com.kwonminseok.busanpartners.mainScreen.home.BusanFestivalApiService
import com.kwonminseok.busanpartners.util.PreferenceUtil
import dagger.hilt.android.HiltAndroidApp
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

// Hilt를 사용하기 위해서 여기서 힐트를 추가한다.
@HiltAndroidApp
class BusanPartners: Application() {

    companion object{
        lateinit var preferences: PreferenceUtil
        lateinit var chatClient: ChatClient

    }
    override fun onCreate() {
        BusanFestivalApiService.init(this)

        preferences = PreferenceUtil(applicationContext)
        super.onCreate()

        // ChatClient 초기화
        initializeChatClient()

    }
    private fun initializeChatClient() {
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(backgroundSyncEnabled = true, userPresence = true),
            appContext = this
        )

        chatClient = ChatClient.Builder(BuildConfig.API_KEY, this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // 프로덕션에서는 ChatLogLevel.NOTHING을 사용
            .build()
    }
}