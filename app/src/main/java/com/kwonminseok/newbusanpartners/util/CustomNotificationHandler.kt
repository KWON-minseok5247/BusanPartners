package com.kwonminseok.newbusanpartners.util

import android.app.NotificationManager
import android.content.Context
import io.getstream.chat.android.client.events.NewMessageEvent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kwonminseok.newbusanpartners.R
import io.getstream.android.push.permissions.NotificationPermissionStatus
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.PushMessage

class CustomNotificationHandler(private val context: Context) : NotificationHandler {

    var notificationManager: NotificationManager

    init {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onChatEvent(event: NewMessageEvent): Boolean {
        // 특정 조건에 따른 알림 무시
        if (event.message.text.isNullOrEmpty()) {
            Log.e("CustomNotificationHandler", "Ignoring notification with empty text")
            return true // 내부 처리 무시
        }
        return false // 내부 처리 계속 진행
    }

    override fun onPushMessage(message: PushMessage): Boolean {
        // 특정 조건에 따른 알림 무시
        if (message.channelId == "1") {
            Log.e("CustomNotificationHandler", "Ignoring push notification with empty text")
            return true // 내부 처리 무시
        }
        return false // 내부 처리 계속 진행
    }

    override fun showNotification(channel: Channel, message: Message) {
        // 알림 표시 로직
        Log.e("CustomNotificationHandler", "Showing notification: ${message.text}")
        // 필요한 알림 처리 로직 추가
        Log.e("핸들러의 showNotification", "작동을 하네요.")
        val notificationId = message.id.hashCode() // 알림 ID를 메시지 ID의 해시코드로 설정

        val notification = NotificationCompat.Builder(context, channel.id)
//            .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
            .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
            .setContentTitle("New message in ${channel.name}") // 알림 제목 설정
            .setContentText(message.text) // 메시지 텍스트 설정
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
            .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
            .build()

        notificationManager.notify(notificationId, notification)

    }

    override fun dismissChannelNotifications(channelType: String, channelId: String) {
        Log.e("CustomNotificationHandler", "Dismissing notifications for channel: $channelType/$channelId")
        // 알림 해제 로직
    }

    override fun dismissAllNotifications() {
        Log.e("CustomNotificationHandler", "Dismissing all notifications")
        // 모든 알림 해제 로직
    }

    override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
        Log.e("CustomNotificationHandler", "Notification permission status: $status")
        // 권한 상태 처리 로직
    }
}
