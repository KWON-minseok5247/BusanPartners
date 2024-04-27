package com.kwonminseok.busanpartners.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import io.getstream.android.push.firebase.FirebaseMessagingDelegate
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
//        super.onNewToken(token)
        // Update device's token on Stream backend
        try {
            FirebaseMessagingDelegate.registerFirebaseToken(token, providerName = "BusanPartners")
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 애플리케이션 컨텍스트를 가져옵니다.
        val context = applicationContext

        // 알림 관리자를 시스템 서비스로부터 가져옵니다.
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널을 설정합니다. Android Oreo 이상 필요.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Firebase 메시지에서 필요한 데이터를 추출합니다.
        val channelName = remoteMessage.data["channel_name"] ?: "Default Channel"
        val messageId = remoteMessage.data["message_id"] ?: "1"
        val messageText = remoteMessage.data["message_text"] ?: "You have a new message!"

        // 알림 ID를 메시지 ID의 해시코드로 설정
        val notificationId = messageId.hashCode()

        // 알림을 생성합니다.
        val notification = NotificationCompat.Builder(context, "my_channel_id")
            .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
            .setContentTitle("New message in $channelName") // 알림 제목 설정
            .setContentText(messageText) // 메시지 텍스트 설정
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
            .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
            .build()

        // 알림을 발송합니다.
        notificationManager.notify(notificationId, notification)






        // ChatClient와 사용자 연결 확인
        if (chatClient.getCurrentUser() == null) {
            // 사용자 연결이 필요함
            reconnectUser()
            handleNotification(remoteMessage)

        } else {
            // 이미 연결된 상태에서 처리
            handleNotification(remoteMessage)
        }
    }

    private fun reconnectUser() {

        val guestUser = User(
            id = "5dtxXAmlQgcjAidXFY1bPuGPIjP2",
            name = "권민석",
            image = "https://firebasestorage.googleapis.com/v0/b/busanpartners-86b94.appspot.com/o/user%2F5dtxXAmlQgcjAidXFY1bPuGPIjP2%2FimagePath?alt=media&token=6167f5f4-8ee1-41b9-84e8-6249a8108bdc"
        )
        chatClient.connectUser(
            guestUser,
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNWR0eFhBbWxRZ2NqQWlkWEZZMWJQdUdQSWpQMiJ9.s6CURR7-mTKmZWKQDYNGelRQuja8nFg-pUBHlyadKeY"
        ).enqueue {
            if (it.isSuccess) {
                // 연결 성공
            } else {
                // 연결 실패 처리
            }
        }
    }

    private fun handleNotification(remoteMessage: RemoteMessage) {
        // 알림 처리 로직
// 로그를 통해 알림 처리가 시작됐음을 확인
        Log.e("알림처리", "알림 처리 시작")

        // Firebase 메시지에서 데이터 추출
        remoteMessage.data.let {
            Log.e("mapamapmap", it.toString())
            val channelType = it["channel_type"]
            val channelId = it["channel_id"]
            val messageId = it["message_id"]
            val parentMessageId = it["parentMessageId"]
            Log.e("channelType", channelType.toString())
            Log.e("channelId", channelId.toString())
            Log.e("messageId", messageId.toString())
            Log.e("parentMessageId", parentMessageId.toString())


            // 필요한 정보가 모두 있는지 확인
            if (channelType != null && channelId != null && messageId != null) {
                val cid = "$channelType:$channelId"

                // 새로운 인텐트 생성하여 ChannelActivity 시작
                val newIntent = Intent(applicationContext, ChannelActivity::class.java).apply {
                    putExtra("key:cid", cid)
                    putExtra("EXTRA_MESSAGE_ID", messageId)
                    putExtra("EXTRA_PARENT_MESSAGE_ID", parentMessageId)
                }
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // 필요한 경우 새 태스크로 시작
                applicationContext.startActivity(newIntent)
            } else {
                Log.e("알림처리", "필요한 정보가 누락됨")
            }
        }    }
}

