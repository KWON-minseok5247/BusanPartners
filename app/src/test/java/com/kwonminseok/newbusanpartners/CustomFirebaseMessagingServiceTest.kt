package com.kwonminseok.newbusanpartners

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.kwonminseok.newbusanpartners.util.CustomFirebaseMessagingService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CustomFirebaseMessagingServiceTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var notificationManager: NotificationManager

    @Mock
    private lateinit var pendingIntent: PendingIntent

    @Mock
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private lateinit var service: CustomFirebaseMessagingService

    @Before
    fun setUp() {
        service = spy(CustomFirebaseMessagingService())

        // Mock system services
        `when`(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)

        // Mock PendingIntent and NotificationCompat.Builder
        `when`(PendingIntent.getActivity(any(), anyInt(), any(Intent::class.java), anyInt())).thenReturn(pendingIntent)
        `when`(NotificationCompat.Builder(any(), anyString())).thenReturn(notificationBuilder)
    }

    @Test
    fun testShowNotification() {
        // Prepare a mock RemoteMessage
        val data = mapOf("cid" to "test_channel_id")
        val remoteMessage = mock(RemoteMessage::class.java)
        `when`(remoteMessage.data).thenReturn(data)

        // Call the method under test
        service.showNotification(mock(NotificationChannel::class.java), remoteMessage)

        // Verify interactions
        verify(notificationManager).notify(anyInt(), any())
        verify(notificationBuilder).setSmallIcon(anyInt())
        verify(notificationBuilder).setContentTitle(anyString())
        verify(notificationBuilder).setContentText(anyString())
        verify(notificationBuilder).setContentIntent(pendingIntent)
    }
}
