package com.kwonminseok.busanpartners.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import io.getstream.android.push.firebase.FirebaseMessagingDelegate
import io.getstream.android.push.firebase.FirebaseMessagingDelegate.handleRemoteMessage
import io.getstream.chat.android.client.ChatClient
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

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        try {
            if (handleRemoteMessage(message)) {
                // RemoteMessage was from Stream and it is already processed
                Log.e("FCM", "Stream Chat notification handled")

            } else {
                // RemoteMessage wasn't sent from Stream and it needs to be handled by you
                Log.e("FCM", "Handle non-Stream notification or data message")

            }
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
            Log.e("FCM", "ChatClient was not initialized")

        }


//
//        try {
//            if (handleRemoteMessage(message)) {
//                // RemoteMessage was from Stream and it is already processed
//                Log.w("FCM", "Stream Chat notification handled")
//                val guestUser = User(
//                    id = "5dtxXAmlQgcjAidXFY1bPuGPIjP2",
//                    name = "권민석",
//                    image = "https://firebasestorage.googleapis.com/v0/b/busanpartners-86b94.appspot.com/o/user%2F5dtxXAmlQgcjAidXFY1bPuGPIjP2%2FimagePath?alt=media&token=6167f5f4-8ee1-41b9-84e8-6249a8108bdc"
//                )
//                BusanPartners.chatClient.connectUser(
//                    guestUser,
//                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNWR0eFhBbWxRZ2NqQWlkWEZZMWJQdUdQSWpQMiJ9.s6CURR7-mTKmZWKQDYNGelRQuja8nFg-pUBHlyadKeY"
//                ).enqueue {
//                    if (it.isSuccess) {
//                        // 연결 성공
//                    } else {
//                        // 연결 실패 처리
//                    }
//                }
//
//            } else {
//                // RemoteMessage wasn't sent from Stream and it needs to be handled by you
//                Log.w("FCM", "Handle non-Stream notification or data message")
//
//            }
//        } catch (exception: IllegalStateException) {
//            // ChatClient was not initialized
//        }

        // 이 방법이 Stream Chat 자체의 푸시 알림 처리 기능을 활용하는 방법인 것 같다.


        Log.e("CustomFirebaseMessagingService 메시지", message.data.toString())


//        val notificationHandler = MyNotificationHandler(applicationContext)
//        notificationHandler.showNotification(channel, msg)


//                // 애플리케이션 컨텍스트를 가져옵니다.
//                val context = applicationContext
//
//                // 알림 관리자를 시스템 서비스로부터 가져옵니다.
//                val notificationManager =
//                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//                // 알림 채널을 설정합니다. Android Oreo 이상 필요.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    val channel = NotificationChannel(
//                        "channel_id",
//                        "Channel Name",
//                        NotificationManager.IMPORTANCE_HIGH
//                    )
//                    notificationManager.createNotificationChannel(channel)
//                }
//
//                val channelName = message.data["sender"] ?: "Default Channel"
//                val messageId = message.data["message_id"] ?: "1"
//                val messageText = message.data["messaging"] ?: "You have a new message!"
//
//                // 알림 ID를 메시지 ID의 해시코드로 설정
//                val notificationId = messageId.hashCode()
//
//                // 알림을 생성합니다.
//                val notification = NotificationCompat.Builder(context, "channel_id")
//                    .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
//                    .setContentTitle("New message in $channelName") // 알림 제목 설정
//                    .setContentText(messageText) // 메시지 텍스트 설정
//                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
//                    .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
//                    .build()
//
//                // 알림을 발송합니다.
//                notificationManager.notify(notificationId, notification)
//
//                handleNotification(message)


    }
}
