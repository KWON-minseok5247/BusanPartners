package com.kwonminseok.busanpartners.application


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.chat.android.ui.ChatUI

class ChatInitializer(
    private val context: Context,
    private val autoTranslationEnabled: Boolean,
) {

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNUSED_VARIABLE")
    fun init(apiKey: String) {
        FirebaseApp.initializeApp(context)
        val notificationConfig =
            NotificationConfig(
                pushDeviceGenerators = listOf(
                    FirebasePushDeviceGenerator(providerName = "BusanPartners"),
                ),
            )
        val notificationChannel: () -> NotificationChannel = {
            val channelId = "chat_channel"
            val channelName = "Chat Messages"
            val importance = NotificationManager.IMPORTANCE_HIGH
            NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for chat messages"
            }
        }

        val notificationHandler = NotificationHandlerFactory.createNotificationHandler(
            context = context,
            newMessageIntent = { message, channel ->
                HostActivity.createLaunchIntent(
                    context = context,
                    messageId = message.id,
                    parentMessageId = message.parentId,
                    channelType = channel.type,
                    channelId = channel.id
                )
            },
            notificationChannel = notificationChannel
        )


//        val notificationHandler = NotificationHandlerFactory.createNotificationHandler(
//            context = context,
//            notificationConfig = notificationConfig,
//            newMessageIntent = {
//                    message: Message,
//                    channel: Channel,
//                ->
//                HostActivity.createLaunchIntent(
//                    context = context,
//                    messageId = message.id,
//                    parentMessageId = message.parentId,
//                    channelType = channel.type,
//                    channelId = channel.id,
//                )
//            },
//        )
        val logLevel = if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING

        val offlinePlugin = StreamOfflinePluginFactory(context)

        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = context,
        )

        val client = ChatClient.Builder(apiKey, context)
            .notifications(notificationConfig, notificationHandler)
            .logLevel(logLevel)
            .withPlugins(offlinePlugin, statePluginFactory)
            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING)
//            .apply {
//                if (BuildConfig.DEBUG) {
//                    this.debugRequests(true)
//                        .clientDebugger(CustomChatClientDebugger())
//                }
//            }
            .build()

//        // Using markdown as text transformer
//        ChatUI.autoTranslationEnabled = autoTranslationEnabled
//        ChatUI.messageTextTransformer = MarkdownTextTransformer(context) { item ->
//            if (autoTranslationEnabled) {
//                client.getCurrentUser()?.language?.let { language ->
//                    item.message.getTranslation(language).ifEmpty { item.message.text }
//                } ?: item.message.text
//            } else {
//                item.message.text
//            }
//        }

        // ChatUI.channelAvatarRenderer = ChannelAvatarRenderer { _, channel, _, targetProvider ->
        //     val targetView: AvatarImageView = targetProvider.regular()
        //     if (channel.image.isBlank()) {
        //         targetView.setAvatar(R.drawable.ic_channel_avatar)
        //     } else {
        //         targetView.setAvatar(channel.image)
        //     }
        // }

        // TransformStyle.messageComposerStyleTransformer = StyleTransformer { defaultStyle ->
        //     defaultStyle.copy(
        //         audioRecordingHoldToRecordText = "Bla bla bla",
        //         audioRecordingSlideToCancelText = "Wash to cancel",
        //     )
        // }

//        ChatUI.decoratorProviderFactory = CustomDecoratorProviderFactory() + DecoratorProviderFactory.defaultFactory()
    }
}
object HostActivity {
    fun createLaunchIntent(
        context: Context,
        messageId: String,
        parentMessageId: String?,
        channelType: String,
        channelId: String
    ): Intent {
        val intent = Intent(context, ChannelActivity::class.java)
        intent.putExtra("messageId", messageId)
        intent.putExtra("parentMessageId", parentMessageId)
        intent.putExtra("channelType", channelType)
        intent.putExtra("channelId", channelId)
        return intent
    }
}

