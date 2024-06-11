package com.kwonminseok.busanpartners.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.kwonminseok.busanpartners.data.TranslatedText

object LanguageUtils {

    fun getDeviceLanguage(context: Context): String {
        val language = context.resources.configuration.locales.get(0).language
        return when (language) {
            "ko", "ja", "zh", "zh-TW", "en" -> language
            else -> "en"
        }
    }

    fun getContentIdForTourPlace(context: Context): Int {
        return when (context.resources.configuration.locales.get(0).language) {
            "ko" -> 12
            else -> 76

        }

    }

    fun getContentIdForFestival(context: Context): Int {
        return when (context.resources.configuration.locales.get(0).language) {
            "ko" -> 15
            else -> 85

        }

    }


    fun getBaseUrl(context: Context): String {
        return when (context.resources.configuration.locales.get(0).language) {
            "ko" -> "http://apis.data.go.kr/B551011/KorService1/"
            "en" -> "http://apis.data.go.kr/B551011/EngService1/"
            "zh" -> {
                when (context.resources.configuration.locales.get(0).script) {
                    "Hans" -> "http://apis.data.go.kr/B551011/ChsService1/" // 간체
                    "Hant" -> "http://apis.data.go.kr/B551011/ChtService1/" // 번체
                    else -> "http://apis.data.go.kr/B551011/ChsService1/"
                }
            }

            "ja" -> "http://apis.data.go.kr/B551011/JpnService1/"
            else -> "http://apis.data.go.kr/B551011/EngService1/"

        }

    }
}
