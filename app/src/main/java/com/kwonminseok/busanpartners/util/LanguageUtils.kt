package com.kwonminseok.busanpartners.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.preferences
import com.kwonminseok.busanpartners.data.TranslatedText
import java.util.Locale

object LanguageUtils {

    fun getDeviceLanguage(context: Context): String {
        val savedLanguage = preferences.getString("selected_locale", "")
        if (savedLanguage.isNotEmpty()) {
            val locale = createLocale(savedLanguage)
            Log.e("selected_locale2", getLanguageWithScript(locale))
            return getLanguageWithScript(locale)
        } else {
            val locale = context.resources.configuration.locales[0]
            Log.e("selected_locale2", getLanguageWithScript(locale))

            return getLanguageWithScript(locale)
        }
    }

    private fun createLocale(languageTag: String): Locale {
        return if (languageTag == "zh-CN") {
            Locale.Builder().setLanguage("zh").setScript("Hans").setRegion("CN").build()
        } else if (languageTag == "zh-TW") {
            Locale.Builder().setLanguage("zh").setScript("Hant").setRegion("TW").build()
        } else {
            Locale.forLanguageTag(languageTag)
        }
    }


    private fun getLanguageWithScript(locale: Locale): String {
        Log.e("getLanguageWithScript", "language: ${locale.language}, script: ${locale.script}")
        return when (locale.language) {
            "zh" -> when (locale.script) {
                "Hans" -> "zh-CN" // 간체
                "Hant" -> "zh-TW" // 번체
                else -> "zh-CN" // 기본값 간체
            }
            else -> locale.language
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
            "zh-CN" -> "http://apis.data.go.kr/B551011/ChsService1/" // 간체
            "zh-TW" -> "http://apis.data.go.kr/B551011/ChtService1/" // 번체
            "es" -> "http://apis.data.go.kr/B551011/SpnService1/"
            "ja" -> "http://apis.data.go.kr/B551011/JpnService1/"
            else -> "http://apis.data.go.kr/B551011/EngService1/"
        }
    }
}
