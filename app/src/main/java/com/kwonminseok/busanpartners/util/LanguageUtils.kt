package com.kwonminseok.busanpartners.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.preferences
import com.kwonminseok.busanpartners.data.TranslatedText
import java.util.Locale

object LanguageUtils {

    fun getDeviceLanguage(context: Context): String {
        val savedLanguage = preferences.getString("selected_locale", Locale.getDefault().toLanguageTag())
        return if (savedLanguage.isNotEmpty()) {
            Locale.forLanguageTag(savedLanguage).language
        } else {
            val systemLanguage = context.resources.configuration.locales.get(0).language
            when (systemLanguage) {
                "ko", "ja", "zh", "en", "es" -> systemLanguage
                else -> "en"
            }
        }
    }

    fun getContentIdForTourPlace(context: Context): Int {
        val language = getDeviceLanguage(context)
        Log.e("getContentIdForTourPlace", language)
        return when (language) {
            "ko" -> 12
            else -> 76
        }
    }

    fun getContentIdForFestival(context: Context): Int {
        val language = getDeviceLanguage(context)
        Log.e("getContentIdForFestival", language)
        return when (language) {
            "ko" -> 15
            else -> 85
        }
    }

    fun getBaseUrl(context: Context): String {
        val language = getDeviceLanguage(context)
        return when (language) {
            "ko" -> "http://apis.data.go.kr/B551011/KorService1/"
            "en" -> "http://apis.data.go.kr/B551011/EngService1/"
            "zh" -> {
                when (context.resources.configuration.locales.get(0).script) {
                    "Hans" -> "http://apis.data.go.kr/B551011/ChsService1/" // 간체
                    "Hant" -> "http://apis.data.go.kr/B551011/ChtService1/" // 번체
                    else -> "http://apis.data.go.kr/B551011/ChsService1/"
                }
            }
            "es" -> "http://apis.data.go.kr/B551011/SpnService1/"
            "ja" -> "http://apis.data.go.kr/B551011/JpnService1/"
            else -> "http://apis.data.go.kr/B551011/EngService1/"
        }
    }
}

