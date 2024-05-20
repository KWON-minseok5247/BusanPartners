package com.kwonminseok.busanpartners.util

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import io.getstream.chat.android.client.notifications.handler.NotificationHandler

//class MyNotificationListenerService : NotificationListenerService() {
//
//    override fun onNotificationPosted(sbn: StatusBarNotification) {
//        Log.e("NotificationListener", "Notification posted: ${sbn.packageName}, ID: ${sbn.id}, Text: ${sbn.notification.extras.getString("android.text")}")
//    }
//
//    override fun onNotificationRemoved(sbn: StatusBarNotification) {
//        Log.e("NotificationListener", "Notification removed: ${sbn.packageName}, ID: ${sbn.id}")
//    }
//
//    override fun onListenerConnected() {
//        Log.e("NotificationListener", "Notification listener connected")
//        for (sbn in activeNotifications) {
//            Log.e("NotificationListener", "Active notification: ${sbn.packageName}, ID: ${sbn.id}, Text: ${sbn.notification.extras.getString("android.text")}")
//        }
//    }
//}
class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // 알림 ID가 1인 경우 무시
        if (sbn.id == 1) {
            Log.d("NotificationListener", "ID 1 notification ignored: ${sbn.packageName}, ID: ${sbn.id}")
            return
        }

        val notificationText = sbn.notification.extras.getString("android.text")
        Log.e("NotificationListener", "Notification posted: ${sbn.packageName}, ID: ${sbn.id}, Text: $notificationText")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.e("NotificationListener", "Notification removed: ${sbn.packageName}, ID: ${sbn.id}")
    }

    override fun onListenerConnected() {
        Log.e("NotificationListener", "Notification listener connected")
        for (sbn in activeNotifications) {
            if (sbn.id == 1) {
                Log.d("NotificationListener", "ID 1 active notification ignored: ${sbn.packageName}, ID: ${sbn.id}")
                continue
            }
            val notificationText = sbn.notification.extras.getString("android.text")
            Log.e("NotificationListener", "Active notification: ${sbn.packageName}, ID: ${sbn.id}, Text: $notificationText")
        }
    }
}
