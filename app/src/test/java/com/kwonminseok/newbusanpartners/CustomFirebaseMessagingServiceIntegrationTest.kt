package com.kwonminseok.newbusanpartners

import android.app.NotificationManager
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.messaging.RemoteMessage
import com.kwonminseok.newbusanpartners.util.CustomFirebaseMessagingService
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomFirebaseMessagingServiceIntegrationTest {

    @Test
    fun testOnMessageReceived() {
        val service = CustomFirebaseMessagingService()
        val message = RemoteMessage.Builder("test_sender")
            .addData("cid", "test_channel_id")
            .build()

        service.onMessageReceived(message)

        // 알림이 정상적으로 생성되었는지 확인
        val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assertNotNull(notificationManager.activeNotifications)
    }
}
