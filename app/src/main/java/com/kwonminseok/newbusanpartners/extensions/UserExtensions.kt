package com.kwonminseok.newbusanpartners.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.db.entity.UserEntity

fun User.toEntity(): UserEntity = UserEntity(
    uid = this.uid,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    imagePath = this.imagePath,
    gender = this.gender,
    college = this.college,
    introduction = this.introduction,
    name = this.name,
    authentication = this.authentication,
    universityEmail = this.universityEmail,
    tokenTime = this.tokenTime,
    chipGroup = this.chipGroup,
    major = this.major,
    wantToMeet = this.wantToMeet,
    blockList = this.blockList,
    chatChannelCount = this.chatChannelCount,
    deviceToken = this.deviceToken,
    language = this.language,
    reset = this.reset,
    banList = this.banList
)

// UserEntityExtensions.kt
fun UserEntity.toUser(): User = User(
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    imagePath = this.imagePath,
    uid = this.uid,
    gender = this.gender,
    college = this.college,
    introduction = this.introduction,
    name = this.name,
    authentication = this.authentication,
    universityEmail = this.universityEmail,
    tokenTime = this.tokenTime,
    chipGroup = this.chipGroup,
    major = this.major,
    wantToMeet = this.wantToMeet,
    blockList = this.blockList,
    chatChannelCount = this.chatChannelCount,
    deviceToken = this.deviceToken,
    language = this.language,
    reset = this.reset,
    banList = this.banList


)


//fun Activity.setStatusBarTransparent() {
//    window.apply {
//        setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
//    }
//    if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//    }
//
//}
//fun Activity.setStatusBarTransparent() {
//    window.apply {
//        statusBarColor = Color.TRANSPARENT
//        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//    }
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        window.insetsController?.apply {
//            setSystemBarsAppearance(
//                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
//                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
//            )
//        }
//    } else {
//        window.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                )
//    }
//}

fun Activity.setStatusBarTransparent() {
    window.apply {
        statusBarColor = Color.TRANSPARENT
        navigationBarColor = Color.TRANSPARENT
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.insetsController?.apply {
            setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    } else {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                )
    }
}



fun Activity.setStatusBarVisible() {
    window.apply {
        // 상태바 색상을 원래대로 설정
        statusBarColor = ContextCompat.getColor(this@setStatusBarVisible, R.color.white)
        clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    // API 30 이상에서 시스템 창 맞춤 설정
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.insetsController?.apply {
            // 상태바를 보이게 만듭니다.
            setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    } else {
        // API 30 미만에서는 다른 방법을 사용합니다.
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }
}

// extention.kt
fun Context.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else 0
}




//fun Activity.setStatusBarVisible() {
//    window.apply {
//        clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//    }
//    if (Build.VERSION.SDK_INT >= 30) {    // API 30 에 적용
//        WindowCompat.setDecorFitsSystemWindows(window, true)
//    }
//}