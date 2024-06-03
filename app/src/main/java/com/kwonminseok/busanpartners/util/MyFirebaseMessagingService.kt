//
//package com.kwonminseok.busanpartners.util
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import android.os.Build
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.core.app.NotificationCompat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import com.kwonminseok.busanpartners.R
//import com.kwonminseok.busanpartners.ui.login.SplashActivity
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        remoteMessage.data.isNotEmpty().let {
//            // Handle the message within 10 seconds
//            val title = remoteMessage.data["name"] ?: "Unknown"
//            val message = remoteMessage.data["message"] ?: "No message"
//
//            // Format the notification content
//            val notificationContent = "$title: $message"
//
//            showNotification(notificationContent)
//        }
//    }
//
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
//
//
////    private fun sendNotification(messageBody: String) {
////        val channelId = "1"
////        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
////        val notificationBuilder = NotificationCompat.Builder(this, channelId)
////            .setSmallIcon(R.drawable.pukyong_logo)
////            .setContentTitle("New Message")
////            .setContentText(messageBody)
////            .setAutoCancel(true)
////            .setSound(defaultSoundUri)
////
////        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
////            notificationManager.createNotificationChannel(channel)
////        }
////
////        notificationManager.notify(0, notificationBuilder.build())
////    }
//}
