package com.kwonminseok.busanpartners

import android.app.Application
import com.kwonminseok.busanpartners.mainScreen.home.BusanFestivalApiService
import com.kwonminseok.busanpartners.util.PreferenceUtil
import dagger.hilt.android.HiltAndroidApp

// Hilt를 사용하기 위해서 여기서 힐트를 추가한다.
@HiltAndroidApp
class BusanPartners: Application() {

    companion object{
        lateinit var preferences: PreferenceUtil
    }
    override fun onCreate() {
        BusanFestivalApiService.init(this)

        preferences = PreferenceUtil(applicationContext)
        super.onCreate()

    }
}