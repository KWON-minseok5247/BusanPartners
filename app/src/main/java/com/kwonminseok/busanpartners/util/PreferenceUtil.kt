package com.kwonminseok.busanpartners.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferenceUtil(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    // EncryptedSharedPreferences 인스턴스 생성
    private val preferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_prefs_name",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // 문자열 값 가져오기
    fun getString(key: String, defValue: String): String {
        return preferences.getString(key, defValue) ?: defValue
    }

    // 문자열 값 설정하기
    fun setString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

}