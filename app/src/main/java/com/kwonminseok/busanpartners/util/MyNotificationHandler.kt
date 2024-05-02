//package com.kwonminseok.busanpartners.util
//
//import android.app.NotificationManager
//import android.content.Context
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.kwonminseok.busanpartners.R
//import io.getstream.android.push.permissions.NotificationPermissionStatus
//import io.getstream.chat.android.client.events.NewMessageEvent
//import io.getstream.chat.android.client.notifications.handler.NotificationHandler
//import io.getstream.chat.android.models.Channel
//import io.getstream.chat.android.models.Message
//import io.getstream.chat.android.models.PushMessage
//
//class MyNotificationHandler(private val context: Context) : NotificationHandler {
//    val notificationChannelId = ""
//    val notificationId = 1
//
////    private val notificationManager: NotificationManager by lazy {
////        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////    }
//
//    var notificationManager: NotificationManager
//
//    init {
//        notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    }
//
//    override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
//        when (status) {
//            NotificationPermissionStatus.REQUESTED -> {
//                // invoked when POST_NOTIFICATIONS permission is requested
//            }
//            NotificationPermissionStatus.GRANTED -> {
//                // invoked when POST_NOTIFICATIONS permission is granted
//            }
//            NotificationPermissionStatus.DENIED -> {
//                // invoked when POST_NOTIFICATIONS permission is denied
//            }
//            NotificationPermissionStatus.RATIONALE_NEEDED -> {
//                // invoked when POST_NOTIFICATIONS permission requires rationale
//            }
//        }
//    }
//
//    override fun showNotification(channel: Channel, message: Message) {
//
//        Log.e("핸들러의 showNotification", "작동을 하네요.")
//        val notificationId = message.id.hashCode() // 알림 ID를 메시지 ID의 해시코드로 설정
//
//        val notification = NotificationCompat.Builder(context, channel.id)
////            .setSmallIcon(R.drawable.stream_ic_notification) // 알림 아이콘 설정
//            .setSmallIcon(R.drawable.pukyong_logo) // 알림 아이콘 설정
//            .setContentTitle("New message in ${channel.name}") // 알림 제목 설정
//            .setContentText(message.text) // 메시지 텍스트 설정
//            .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선 순위 설정
//            .setAutoCancel(true) // 탭하면 알림이 자동으로 취소되도록 설정
//            .build()
//
//        notificationManager.notify(notificationId, notification)
//
////        val notification = NotificationCompat.Builder(context, notificationChannelId)
////            .build()
////        notificationManager.notify(notificationId, notification)
//
//    }
//
//    override fun dismissChannelNotifications(channelType: String, channelId: String) {
//        // Dismiss all notification related with this channel
//    }
//
//    override fun dismissAllNotifications() {
//        // Dismiss all notifications
//    }
//    override fun onChatEvent(event: NewMessageEvent): Boolean {
//        return true
//    }
//
//    override fun onPushMessage(message: PushMessage): Boolean {
//        return false
//    }
//
//}
//
//
