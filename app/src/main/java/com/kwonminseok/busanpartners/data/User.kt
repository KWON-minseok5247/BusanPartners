package com.kwonminseok.busanpartners.data

import android.bluetooth.BluetoothClass.Device.Major
import android.net.Uri
import com.google.android.gms.auth.api.Auth
import com.google.android.material.chip.ChipGroup

data class User(
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String,
    val imagePath: String = "",
    val uid: String = "",
    val gender: String? = null,
    val college: String? = null,
    val introduction: String? = null,
    val name: String? = "",
    val authentication: AuthenticationInformation = AuthenticationInformation(),
    val universityEmail: String = "",
    // tokenTime이 있어야 관광객들의 경우 7일의 텀을 주고 메시지를 더 이상 못하게 막을 수 있다.
    val tokenTime: String? = "",
    val chipGroup: List<String>? = null,
    val major: String? = ""



) {
    constructor(): this("","","","", "")
}
