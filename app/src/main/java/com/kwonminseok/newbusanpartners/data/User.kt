package com.kwonminseok.newbusanpartners.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String,
    val imagePath: String = "",
    val uid: String = "",
    val gender: String? = null,
    val college: String? = null,
    val introduction: TranslatedText? = TranslatedText(),
    val name: TranslatedText? = TranslatedText(),
    val authentication: AuthenticationInformation = AuthenticationInformation(),
    val universityEmail: String = "",
    // tokenTime이 있어야 관광객들의 경우 7일의 텀을 주고 메시지를 더 이상 못하게 막을 수 있다.
    val tokenTime: String? = "2000-01-01T12:38:11.818609+09:00",
    val chipGroup: TranslatedList? = TranslatedList(),
    val major: TranslatedText? = TranslatedText(),
    val wantToMeet: Boolean = false,
    val blockList: MutableList<String>? = mutableListOf(),
    val chatChannelCount: Int = 0,
    val deviceToken: String = "",
    val language: String = "en",
    val reset: Boolean = false,
    val banList: MutableList<String>? = mutableListOf(),




    ) : Parcelable {
    constructor(): this("","","","", "")
}
