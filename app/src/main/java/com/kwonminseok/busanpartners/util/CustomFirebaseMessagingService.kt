package com.kwonminseok.busanpartners.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.ui.login.SplashActivity
import com.kwonminseok.busanpartners.ui.message.ActivityState
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import io.getstream.android.push.firebase.FirebaseMessagingDelegate
import io.getstream.android.push.firebase.FirebaseMessagingDelegate.handleRemoteMessage
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

                    Log.e("NotificationChannelCheck", "ID: ${channel.id}, Name: ${channel.name}, Importance: ${channel.importance}")
                    // 채널의 이름이 "Chat Messages"이고 중요도가 4 이상인 경우에만 처리
                    if (channel.name == "채팅 메세지" && channel.importance >= 4) {
                        Log.e("넘었다", "ID: ${channel.id}, Name: ${channel.name}, Importance: ${channel.importance}")
                        showNotification(channel, message)  // "Chat Messages" 채널에 대한 메시지 처리
//                        handleRemoteMessage(message)
                        return  // 처리 후 반복 종료
                    }
                }


            } else {

            }
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
            Log.e("FCM", "ChatClient was not initialized")

        }
        // 이 방법이 Stream Chat 자체의 푸시 알림 처리 기능을 활용하는 방법인 것 같다.
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun showNotification(channel: NotificationChannel, message: RemoteMessage) {
//        val notificationManager =
//            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // RemoteMessage에서 cid 추출
//        val cid = message.data["cid"] ?: throw IllegalArgumentException("CID is missing in the message data")
//        Log.e("CustomFirebaseMessagingService cid", cid)
//
//        // cid의 해시코드를 알림 ID로 사용
////        val notificationId = cid.hashCode()
//        val notificationId = 1
////        val notificationId = 1 // 알림 ID를 메시지 ID의 해시코드로 설정
//
//        // 특정 화면으로 이동하는 Intent 생성
//        val launchIntent = SplashActivity.createLaunchIntent(
//            context = this,
//            messageId = message.data["message_id"] ?: "",
//            parentMessageId = message.data["parentMessageId"] ?: "",
//            channelType = message.data["channel_type"] ?: "",
//            channelId = message.data["cid"] ?: ""
//        )
//        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        val pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//
//
//        val notification = NotificationCompat.Builder(this, channel.id)
////            .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
//            .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
//            .setContentTitle("상대방으로부터 메세지가 왔습니다!") // 알림 제목 설정
//            .setContentText("알림을 눌러 확인해보세요.") // 메시지 텍스트 설정
//            .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
//            .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
//            .setContentIntent(pendingIntent)  // 알림 클릭 시 실행할 Intent 설정
//            .build()
//
//        notificationManager.notify(notificationId, notification)
//
//
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun showNotification(channel: NotificationChannel, message: RemoteMessage) {
//        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val cid = message.data["cid"] ?: throw IllegalArgumentException("CID is missing in the message data")
//        Log.e("CustomFirebaseMessagingService cid", cid)
//
//        // cid의 해시코드를 알림 ID로 사용
//        val notificationId = cid.hashCode()
//
//        val launchIntent = SplashActivity.createLaunchIntent(
//            context = this,
//            messageId = message.data["message_id"] ?: "",
//            parentMessageId = message.data["parentMessageId"] ?: "",
//            channelType = message.data["channel_type"] ?: "",
//            channelId = message.data["cid"] ?: ""
//        )
//        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        val pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//        val notification = NotificationCompat.Builder(this, channel.id)
//            .setSmallIcon(R.drawable.pukyong_logo)
//            .setContentTitle("상대방으로부터 메세지가 왔습니다!")
//            .setContentText("알림을 눌러 확인해보세요.")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .build()
//
//        notificationManager.notify(notificationId, notification)
//    }
@RequiresApi(Build.VERSION_CODES.O)
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
        .setSmallIcon(R.drawable.white_logo_transparent_background)
        .setContentTitle("상대방으로부터 메세지가 왔습니다!")
        .setContentText("알림을 눌러 확인해보세요.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setGroup("CHAT_MESSAGES_GROUP")  // 그룹 ID 설정
        .build()

    notificationManager.notify(notificationId, notification)

    // 그룹 요약 알림 생성
    val summaryNotification = NotificationCompat.Builder(this, channel.id)
        .setSmallIcon(R.drawable.white_logo_transparent_background)
        .setContentTitle("새로운 메시지")
        .setContentText("여러 메시지가 도착했습니다.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setGroup("CHAT_MESSAGES_GROUP")
        .setGroupSummary(true)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(0, summaryNotification)  // 고정된 ID로 그룹 요약 알림 표시
}

}
