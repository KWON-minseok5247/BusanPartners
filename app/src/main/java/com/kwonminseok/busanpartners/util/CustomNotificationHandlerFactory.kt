package com.kwonminseok.busanpartners.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.kwonminseok.busanpartners.R
import io.getstream.android.push.permissions.NotificationPermissionHandler
import io.getstream.android.push.permissions.NotificationPermissionStatus
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message

//public object CustomNotificationHandlerFactory {
//
//    @SuppressLint("NewApi")
//    @JvmOverloads
//    @JvmStatic
//    public fun createNotificationHandler(
//        context: Context,
//        newMessageIntent: ((message: Message, channel: Channel) -> Intent)? = null,
//        notificationChannel: (() -> NotificationChannel)? = null,
//        permissionHandler: NotificationPermissionHandler? = null,
//    ): NotificationHandler {
//        val notificationChannelFun = notificationChannel ?: getDefaultNotificationChannel(context)
//        val newMessageIntentFun = newMessageIntent ?: getDefaultNewMessageIntentFun(context)
//
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            SimpleNotificationHandler(
//                context,
//                newMessageIntentFun,
//                notificationChannelFun,
//                permissionHandler,
//            )
//        } else {
//            SimpleNotificationHandler(context, newMessageIntentFun, notificationChannelFun, permissionHandler)
//        }
//    }
//
//    private fun getDefaultNewMessageIntentFun(
//        context: Context,
//    ): (message: Message, channel: Channel) -> Intent {
//        return { _, _ -> createDefaultNewMessageIntent(context) }
//    }
//
//    private fun createDefaultNewMessageIntent(context: Context): Intent =
//        context.packageManager!!.getLaunchIntentForPackage(context.packageName)!!
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun getDefaultNotificationChannel(context: Context): (() -> NotificationChannel) {
//        return {
//            NotificationChannel(
//                context.getString(R.string.stream_chat_notification_channel_id),
//                context.getString(R.string.stream_chat_notification_channel_name),
//                NotificationManager.IMPORTANCE_DEFAULT,
//            ).apply {
//                // "기타 알림" 채널을 Importance.NONE으로 설정
//                if (context.getString(R.string.stream_chat_notification_channel_name) == "기타 알림") {
//                    importance = NotificationManager.IMPORTANCE_NONE
//                }
//            }
//        }
//    }
//}
//
//class SimpleNotificationHandler(
//    private val context: Context,
//    private val newMessageIntent: (message: Message, channel: Channel) -> Intent,
//    private val notificationChannel: () -> NotificationChannel,
//    private val permissionHandler: NotificationPermissionHandler?
//) : NotificationHandler {
//
//    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//    override fun dismissAllNotifications() {
//        notificationManager.cancelAll()
//    }
//
//    override fun dismissChannelNotifications(channelType: String, channelId: String) {
//        notificationManager.cancelAll()
//    }
//
//    override fun showNotification(channel: Channel, message: Message) {
//        val intent = newMessageIntent(message, channel)
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val notification = NotificationCompat.Builder(context, context.getString(R.string.stream_chat_notification_channel_id))
//            .setSmallIcon(R.drawable.pukyong_logo)
//            .setContentTitle(channel.name)
//            .setContentText(message.text)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        notificationManager.notify(message.id.hashCode(), notification)
//    }
//
//    override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
//        when (status) {
//            NotificationPermissionStatus.REQUESTED -> {
//                // 권한 요청 시 필요한 작업 수행
//            }
//            NotificationPermissionStatus.GRANTED -> {
//                // 권한이 승인되었을 때 필요한 작업 수행
//                createNotificationChannel()
//            }
//            NotificationPermissionStatus.DENIED -> {
//                // 권한이 거부되었을 때의 처리
//            }
//            NotificationPermissionStatus.RATIONALE_NEEDED -> {
//                // 권한 요청을 위한 추가 설명이 필요할 때의 처리
//            }
//        }
//    }
//
//
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = notificationChannel()
//            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//}

//public object CustomNotificationHandlerFactory {
//
//    @SuppressLint("NewApi")
//    @JvmOverloads
//    @JvmStatic
//    public fun createNotificationHandler(
//        context: Context,
//        newMessageIntent: ((message: Message, channel: Channel) -> Intent)? = null,
//        notificationChannel: (() -> NotificationChannel)? = null,
//        permissionHandler: NotificationPermissionHandler? = null,
//    ): NotificationHandler {
//
//        val notificationChannelFun = notificationChannel ?: getDefaultNotificationChannel(context)
//
//        val newMessageIntentFun = newMessageIntent ?: getDefaultNewMessageIntentFun(context)
//
//
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            MessagingStyleNotificationHandler(
//                context,
//                newMessageIntentFun,
//                notificationChannelFun,
//                userIconBuilder,
//                permissionHandler,
//            )
//        } else {
//            ChatNotificationHandler(context, newMessageIntentFun, notificationChannelFun)
//        }
////        return SimpleNotificationHandler(
////            context,
////            newMessageIntentFun,
////            notificationChannelFun,
////            permissionHandler
////        )
//    }
//
//    private fun getDefaultNewMessageIntentFun(
//        context: Context,
//    ): (message: Message, channel: Channel) -> Intent {
//        return { _, _ -> createDefaultNewMessageIntent(context) }
//    }
//
//    private fun createDefaultNewMessageIntent(context: Context): Intent =
//        context.packageManager!!.getLaunchIntentForPackage(context.packageName)!!
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun getDefaultNotificationChannel(context: Context): (() -> NotificationChannel) {
//        return {
//            NotificationChannel(
//                context.getString(R.string.stream_chat_notification_channel_id),
//                context.getString(R.string.stream_chat_notification_channel_name),
//                NotificationManager.IMPORTANCE_DEFAULT,
//            ).apply {
//                // "기타 알림" 채널을 Importance.NONE으로 설정
//                if (context.getString(R.string.stream_chat_notification_channel_name) == "기타 알림") {
//                    importance = NotificationManager.IMPORTANCE_NONE
//                    Log.e("importance", "none???")
//                }
//            }
//        }
//    }
//}
//
//class SimpleNotificationHandler(
//    private val context: Context,
//    private val newMessageIntent: (message: Message, channel: Channel) -> Intent,
//    private val notificationChannel: () -> NotificationChannel,
//    private val permissionHandler: NotificationPermissionHandler?
//) : NotificationHandler {
//
//    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//    override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
//        when (status) {
//            NotificationPermissionStatus.REQUESTED -> {
//                // 권한 요청 시 필요한 작업 수행
//            }
//            NotificationPermissionStatus.GRANTED -> {
//                // 권한이 승인되었을 때 필요한 작업 수행
//                createNotificationChannel()
//            }
//            NotificationPermissionStatus.DENIED -> {
//                // 권한이 거부되었을 때의 처리
//            }
//            NotificationPermissionStatus.RATIONALE_NEEDED -> {
//                // 권한 요청을 위한 추가 설명이 필요할 때의 처리
//            }
//        }
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = notificationChannel()
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    override fun dismissAllNotifications() {
//        notificationManager.cancelAll()
//    }
//
//    override fun dismissChannelNotifications(channelType: String, channelId: String) {
//        // 특정 채널의 알림을 취소하려면 각 알림에 채널 ID를 태그로 추가한 후 해당 태그를 기반으로 알림을 취소
//        // 이 예제에서는 간단히 모든 알림을 취소
//        notificationManager.cancelAll()
//    }
//
//    override fun showNotification(channel: Channel, message: Message) {
//        val intent = newMessageIntent(message, channel)
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE 추가
//        )
//
//        val notification = NotificationCompat.Builder(context, context.getString(R.string.stream_chat_notification_channel_id))
//            .setSmallIcon(R.drawable.pukyong_logo)
//            .setContentTitle(channel.name)
//            .setContentText(message.text)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        notificationManager.notify(message.id.hashCode(), notification)
//    }
//}
public object CustomNotificationHandlerFactory {


    @SuppressLint("NewApi")
    @JvmOverloads
    @JvmStatic
    public fun createNotificationHandler(
        context: Context,
        newMessageIntent: ((message: Message, channel: Channel) -> Intent)? = null,
        notificationChannel: (() -> NotificationChannel)? = null,
        permissionHandler: NotificationPermissionHandler? = null,
    ): NotificationHandler {
        Log.e("notificationChannel", notificationChannel.toString())
        val notificationChannelFun = notificationChannel ?: getDefaultNotificationChannel(context)
        Log.e("notificationChannelFun", notificationChannelFun.toString())

        val newMessageIntentFun = newMessageIntent ?: getDefaultNewMessageIntentFun(context)


        return SimpleNotificationHandler(
            context,
            newMessageIntentFun,
            notificationChannelFun,
            permissionHandler
        )
    }

    private fun getDefaultNewMessageIntentFun(
        context: Context,
    ): (message: Message, channel: Channel) -> Intent {
        return { _, _ -> createDefaultNewMessageIntent(context) }
    }

    private fun createDefaultNewMessageIntent(context: Context): Intent =
        context.packageManager!!.getLaunchIntentForPackage(context.packageName)!!

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDefaultNotificationChannel(context: Context): (() -> NotificationChannel) {

        Log.e("stream_chat_notification_channel_id", "${R.string.stream_chat_notification_channel_id} ${R.string.stream_chat_notification_channel_name}")

        return {
            val channelId = "chat_channel_one"
            val channelName = "Chat Messages_one"
            val importance = NotificationManager.IMPORTANCE_HIGH
            NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for chat messages one"
            }

//            NotificationChannel(
//                context.getString(R.string.stream_chat_notification_channel_id),
//                context.getString(R.string.stream_chat_notification_channel_name),
//                NotificationManager.IMPORTANCE_DEFAULT,
//            ).apply {
//                // "기타 알림" 채널을 Importance.NONE으로 설정
//                if (context.getString(R.string.stream_chat_notification_channel_name) == "기타 알림") {
//                    importance = NotificationManager.IMPORTANCE_NONE
//                    Log.e("importance", "none???")
//                }
//            }

        }
    }
}

class SimpleNotificationHandler(
    private val context: Context,
    private val newMessageIntent: (message: Message, channel: Channel) -> Intent,
    private val notificationChannel: () -> NotificationChannel,
    private val permissionHandler: NotificationPermissionHandler? = getDefaultNotificationPermissionHandler(context)
) : NotificationHandler {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
        Log.e("SimpleNotificationHandler", "Notification permission status: $status")
        Log.e("SimpleNotificationHandler notificationChannel", "${notificationChannel.toString()}")
        when (status) {
            NotificationPermissionStatus.REQUESTED -> {
                // 권한 요청 시 필요한 작업 수행
                Log.e("SimpleNotificationHandler", "Notification permission requested")
            }
            NotificationPermissionStatus.GRANTED -> {
                // 권한이 승인되었을 때 필요한 작업 수행
                Log.e("SimpleNotificationHandler", "Notification permission granted")
                createNotificationChannel()
            }
            NotificationPermissionStatus.DENIED -> {
                // 권한이 거부되었을 때의 처리
                Log.e("SimpleNotificationHandler", "Notification permission denied")
            }
            NotificationPermissionStatus.RATIONALE_NEEDED -> {
                // 권한 요청을 위한 추가 설명이 필요할 때의 처리
                Log.e("SimpleNotificationHandler", "Notification permission rationale needed")
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationChannel()
            notificationManager.createNotificationChannel(channel)
            Log.e("SimpleNotificationHandler", "Notification channel created: ${channel.id}")
        }
    }



    override fun dismissAllNotifications() {
        notificationManager.cancelAll()
    }

    override fun dismissChannelNotifications(channelType: String, channelId: String) {
        // 특정 채널의 알림을 취소하려면 각 알림에 채널 ID를 태그로 추가한 후 해당 태그를 기반으로 알림을 취소
        // 이 예제에서는 간단히 모든 알림을 취소
        notificationManager.cancelAll()
    }

    override fun showNotification(channel: Channel, message: Message) {

        Log.e("showNotification", "Showing notification for message: ${message.text}")
        val intent = newMessageIntent(message, channel)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE 추가
        )

        val notification = NotificationCompat.Builder(context, "chat_channel")
            .setSmallIcon(R.drawable.pukyong_logo)
            .setContentTitle(channel.name)
            .setContentText(message.text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(message.id.hashCode(), notification)
    }
}

private fun getDefaultNotificationPermissionHandler(context: Context): NotificationPermissionHandler {
    return object : NotificationPermissionHandler {
        override fun onPermissionGranted() {
            Log.e("NotificationPermissionHandler", "Permission granted")
        }

        override fun onPermissionDenied() {
            Log.e("NotificationPermissionHandler", "Permission denied")
        }

        override fun onPermissionRationale() {
            Log.e("NotificationPermissionHandler", "Permission rationale needed")
        }

        override fun onPermissionRequested() {
            Log.e("NotificationPermissionHandler", "Permission requested")
        }
    }
}