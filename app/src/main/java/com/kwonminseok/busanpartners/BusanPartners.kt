package com.kwonminseok.busanpartners

import android.app.Application
import androidx.fragment.app.viewModels
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kwonminseok.busanpartners.BuildConfig.NAVER_CLIENT_ID
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.api.WorldTimeApiService
import com.kwonminseok.busanpartners.api.WorldTimeResponse
import com.kwonminseok.busanpartners.mainScreen.home.BusanFestivalApiService
import com.kwonminseok.busanpartners.util.PreferenceUtil
import com.kwonminseok.busanpartners.viewmodel.TimeViewModel
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
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