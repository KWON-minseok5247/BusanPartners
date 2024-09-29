package com.kwonminseok.newbusanpartners.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.ui.HomeActivity
import com.kwonminseok.newbusanpartners.ui.login.SplashActivity
import com.kwonminseok.newbusanpartners.ui.message.ActivityState
import com.kwonminseok.newbusanpartners.ui.message.ChannelActivity
import io.getstream.android.push.firebase.FirebaseMessagingDelegate


class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Update device's token on Stream backend
        try {
            FirebaseMessagingDelegate.registerFirebaseToken(token, providerName = "BusanPartners")
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//        Log.e("remoteMessage", remoteMessage.data.toString())
//
//        if (remoteMessage.data["type"] == "message.new") {
//            // 알림 생성 로직
//            handleRemoteMessage(remoteMessage)
//
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        try {
            Log.e("CustomFirebaseMessagingService 메시지", message.data.toString())
            val manager = getSystemService(NotificationManager::class.java)
            val cid = message.data["cid"] ?: return

            // 현재 활동 상태 확인
            //                if (ActivityState.currentActivity == ChannelActivity::class.java.simpleName && ActivityState.currentChannelId == cid) {
//                if (ActivityState.currentActivity == ChannelActivity::class.java.simpleName && ActivityState.currentActivity == HomeActivity::class.java.simpleName) {
//                    Log.e("FCM", "현재 채팅방에 있음, 알림 생략")
//                    return  // 현재 채팅방에 있으면 알림을 생략
//                }
            if (ActivityState.currentActivity == ChannelActivity::class.java.simpleName) {
                Log.e("FCM", "현재 채팅방에 있음, 알림 생략")
                return  // 현재 채팅방에 있으면 알림을 생략
            }

            if (ActivityState.currentActivity == HomeActivity::class.java.simpleName) {
                Log.e("FCM", "홈 화면에 있음, 알림 생략")
                return  // 홈 화면에 있으면 알림을 생략
            }
            val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val notificationsEnabled = sharedPreferences.getBoolean("all_notifications_enabled", true)

            if (!notificationsEnabled) {
                Log.e("CustomFirebaseMessagingService", "Notifications are disabled")
                return  // 사용자가 알림을 비활성화한 경우 알림을 표시하지 않음
            }


            // 모든 알림 채널을 확인하고, 지정된 채널의 알림만 처리
            manager.notificationChannels.forEach { channel ->
                Log.e("channel", channel.toString())
                showNotification(channel, message)  // "Chat Messages" 채널에 대한 메시지 처리
                return  // 처리 후 반복 종료
            }


        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
            Log.e("FCM", "ChatClient was not initialized")

        }
        // 이 방법이 Stream Chat 자체의 푸시 알림 처리 기능을 활용하는 방법인 것 같다.
    }

fun showNotification(channel: NotificationChannel, message: RemoteMessage) {
    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val cid = message.data["cid"] ?: throw IllegalArgumentException("CID is missing in the message data")
    Log.e("CustomFirebaseMessagingService cid", cid)

    // cid의 해시코드를 알림 ID로 사용
    val notificationId = cid.hashCode()

    val launchIntent = SplashActivity.createLaunchIntent(
        context = this,
        messageId = message.data["message_id"] ?: "",
        parentMessageId = message.data["parentMessageId"] ?: "",
        channelType = message.data["channel_type"] ?: "",
        channelId = message.data["cid"] ?: ""
    )
    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    val pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


    val notification = NotificationCompat.Builder(this, channel.id)
        .setSmallIcon(R.drawable.ic_stat_call_white)
        .setContentTitle(getString(R.string.new_message_from_contact))
        .setContentText(getString(R.string.tap_to_check))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setColor(resources.getColor(R.color.notification_bg_color))
        .setColorized(true)
        .setContentIntent(pendingIntent)
        .setGroup("CHAT_MESSAGES_GROUP")  // 그룹 ID 설정
        .build()

    notificationManager.notify(notificationId, notification)

    // 그룹 요약 알림 생성
    val summaryNotification = NotificationCompat.Builder(this, channel.id)
        .setSmallIcon(R.drawable.ic_stat_call_white)
        .setContentTitle(getString(R.string.new_messages))
        .setContentText(getString(R.string.multiple_messages_received))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setColor(resources.getColor(R.color.notification_bg_color))
        .setGroup("CHAT_MESSAGES_GROUP")
        .setGroupSummary(true)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)  // 요약 알림에도 ContentIntent 설정
        .build()

    notificationManager.notify(0, summaryNotification)  // 고정된 ID로 그룹 요약 알림 표시
}
}
