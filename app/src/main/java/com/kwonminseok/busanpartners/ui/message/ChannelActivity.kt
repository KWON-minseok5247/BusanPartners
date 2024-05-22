package com.kwonminseok.busanpartners.ui.message

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.google.android.material.datepicker.MaterialDatePicker
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_ID
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_TYPE
import com.kwonminseok.busanpartners.ui.EXTRA_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.EXTRA_PARENT_MESSAGE_ID
import com.naver.maps.geometry.LatLng
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.utils.message.isDeleted
import io.getstream.chat.android.compose.state.mediagallerypreview.MediaGalleryPreviewResultType
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentPickerItemState
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentsPickerMode
import io.getstream.chat.android.compose.state.messages.attachments.CustomPickerMode
import io.getstream.chat.android.compose.state.messages.attachments.StatefulStreamMediaRecorder
import io.getstream.chat.android.compose.ui.attachments.StreamAttachmentFactories
import io.getstream.chat.android.compose.ui.components.avatar.ChannelAvatar
import io.getstream.chat.android.compose.ui.components.composer.MessageInput
import io.getstream.chat.android.compose.ui.components.messageoptions.defaultMessageOptionsState
import io.getstream.chat.android.compose.ui.components.reactionpicker.ReactionsPicker
import io.getstream.chat.android.compose.ui.components.selectedmessage.SelectedMessageMenu
import io.getstream.chat.android.compose.ui.components.selectedmessage.SelectedReactionsMenu
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.messages.attachments.AttachmentsPicker
import io.getstream.chat.android.compose.ui.messages.attachments.factory.AttachmentsPickerTabFactories
import io.getstream.chat.android.compose.ui.messages.attachments.factory.AttachmentsPickerTabFactory
import io.getstream.chat.android.compose.ui.messages.composer.MessageComposer
import io.getstream.chat.android.compose.ui.messages.header.DefaultMessageListHeaderCenterContent
import io.getstream.chat.android.compose.ui.messages.header.MessageListHeader
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.theme.ChatTheme.shapes
import io.getstream.chat.android.compose.ui.util.rememberMessageListState
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User
import io.getstream.chat.android.ui.common.state.messages.MessageAction
import io.getstream.chat.android.ui.common.state.messages.MessageMode
import io.getstream.chat.android.ui.common.state.messages.Reply
import io.getstream.chat.android.ui.common.state.messages.composer.AttachmentMetaData
import io.getstream.chat.android.ui.common.state.messages.list.DeletedMessageVisibility
import io.getstream.chat.android.ui.common.state.messages.list.SelectedMessageOptionsState
import io.getstream.chat.android.ui.common.state.messages.list.SelectedMessageReactionsPickerState
import io.getstream.chat.android.ui.common.state.messages.list.SelectedMessageReactionsState
import io.getstream.sdk.chat.audio.recording.DefaultStreamMediaRecorder
import io.getstream.sdk.chat.audio.recording.StreamMediaRecorder
import java.text.SimpleDateFormat
import java.util.Date

class ChannelActivity : BaseConnectedActivity() {
    private var cid: String = ""  // 채팅방 ID를 저장하는 변수

    private val factory by lazy {

        MessagesViewModelFactory(
            context = this,
            channelId = requireNotNull(intent.getStringExtra("key:cid")),
//            channelId = channelId,
            deletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE,

            messageId = intent.getStringExtra("messageId"),
            parentMessageId = intent.getStringExtra("parentMessageId"),
        )
    }

    private val listViewModel by viewModels<io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel>(
        factoryProducer = { factory })
    private val user = BusanPartners.chatClient.getCurrentUser()

    private val attachmentsPickerViewModel by viewModels<AttachmentsPickerViewModel>(factoryProducer = { factory })
    private val composerViewModel by viewModels<MessageComposerViewModel>(
        factoryProducer = { factory })

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cid = intent.getStringExtra("key:cid") ?: ""


        // 지도 기능을 추가하기 위한 코드
        val customFactories = listOf(locationAttachmentFactory)
//        val customFactories = listOf(dateAttachmentFactory)
        val defaultFactories = StreamAttachmentFactories.defaultFactories()

        // 예시 채팅방에 접속했을 때!
        if (requireNotNull(cid == "messaging:ExampleChat")) {
            setContent {

                ChatTheme(
                    attachmentFactories = customFactories + defaultFactories,
                ) {
                    MessagesScreen(viewModelFactory = factory)
//                MyCustomUi()
                    val isShowingAttachments = attachmentsPickerViewModel.isShowingAttachments
                    val selectedMessageState = listViewModel.currentMessagesState.selectedMessageState
//                val user by listViewModel.user.collectAsState()
                    val lazyListState = rememberMessageListState()

                    val context = LocalContext.current
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val textState = remember { mutableStateOf("") }


// LocationActivityResultContract를 사용하여 런처를 기억
                    val locationLauncher = rememberLauncherForActivityResult(
                        contract = LocationActivityResultContract()
                    ) { result ->
                        result?.let { location ->
                            val attachment = Attachment(
                                type = "location",
                                extraData = mutableMapOf(
                                    "latitude" to location.latitude,
                                    "longitude" to location.longitude,
                                )
                            )
                            Log.e("attachment", attachment.toString())
                            composerViewModel.addSelectedAttachments(listOf(attachment))

                        }

                    }



                    Box(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            topBar = {
                                val connectionState by listViewModel.connectionState.collectAsState()

                                MessageListHeader(
                                    trailingContent = {
                                        Image(
                                        painter = painterResource(id = R.drawable.ic_location), // 고정된 이미지 리소스 ID
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(40.dp) // onChannelAvatar와 동일한 크기
                                            .clickable {
                                                locationLauncher.launch(null)
                                            }
                                    )},
//                                    leadingContent = {
//
//
//
//                                    },
                                    channel = listViewModel.channel,
                                    currentUser = user,
                                    connectionState = connectionState,
                                    onBackPressed = { finish() },
                                    onHeaderTitleClick = {
                                    },
                                    onChannelAvatarClick = {

//                                        locationLauncher.launch(null)

//                                    locationActivityResultLauncher.launch(null)

                                    },


                                    )

                            },
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                CustomMessageComposer(composerViewModel,
                                )
//                            CustomMessageComposer(
////                                onDateSelected = { date ->
////                                    // 2
////                                    val payload = SimpleDateFormat("MMMM dd, yyyy").format(Date(date))
////                                    val attachment = Attachment(
////                                        type = "date",
////                                        extraData = mutableMapOf("payload" to payload)
////                                    )
////
////                                    // 3
////                                    composerViewModel.addSelectedAttachments(listOf(attachment))
////                                }
////                            )
////                            CustomMessageComposer(
////                                composerViewModel,
////                                attachmentsPickerViewModel
////                            )
////                            MyCustomComposer()
////                            MessageComposer(
////                                modifier = Modifier
////                                    .fillMaxWidth()
////                                    .wrapContentHeight(),
////                                viewModel = composerViewModel,
////                                onAttachmentsClick = { attachmentsPickerViewModel.changeAttachmentState(true) },
////
////                                statefulStreamMediaRecorder = statefulStreamMediaRecorder,
////                            )
                            },
                        ) {
                            val currentState = listViewModel.currentMessagesState

                            MessageList(
                                modifier = Modifier
                                    .padding(it)
                                    .background(ChatTheme.colors.appBackground)
                                    .fillMaxSize(),
                                viewModel = listViewModel,
                                messagesLazyListState = if (listViewModel.isInThread) rememberMessageListState(parentMessageId = currentState.parentMessageId) else lazyListState,
                                onThreadClick = { message ->
                                    composerViewModel.setMessageMode(MessageMode.MessageThread(message))
                                    listViewModel.openMessageThread(message)
                                },
                                onMediaGalleryPreviewResult = { result ->
                                    when (result?.resultType) {
                                        MediaGalleryPreviewResultType.QUOTE -> {
                                            val message = listViewModel.getMessageById(result.messageId)

                                            if (message != null) {
                                                composerViewModel.performMessageAction(Reply(message))
                                            }
                                        }

                                        MediaGalleryPreviewResultType.SHOW_IN_CHAT -> {
                                        }

                                        null -> Unit
                                    }
                                },
                            )
                        }



                        if (isShowingAttachments) {
                            val defaultTabFactories = AttachmentsPickerTabFactories.defaultFactories(takeImageEnabled = true, recordVideoEnabled = false, filesTabEnabled = false)
                            val tabFactories = defaultTabFactories
//                        val tabFactories = defaultTabFactories

                            AttachmentsPicker(
                                attachmentsPickerViewModel = attachmentsPickerViewModel,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .height(350.dp),
                                onAttachmentsSelected = { attachments ->
                                    attachmentsPickerViewModel.changeAttachmentState(false)
                                    composerViewModel.addSelectedAttachments(attachments)
                                },
                                onDismiss = {
                                    attachmentsPickerViewModel.changeAttachmentState(false)
                                    attachmentsPickerViewModel.dismissAttachments()
                                },
                                tabFactories = tabFactories
                            )
                        }

                        if (selectedMessageState != null) {
                            val selectedMessage = selectedMessageState.message
                            val messageActions = listViewModel.messageActions

                            when (selectedMessageState) {
                                is SelectedMessageOptionsState -> {
//                                    SelectedMessageMenu(
//                                        modifier = Modifier
//                                            .align(Alignment.Center)
//                                            .padding(horizontal = 20.dp)
//                                            .wrapContentSize(),
//                                        shape = shapes.attachment,
//                                        messageOptions = defaultMessageOptionsState(
//                                            selectedMessage = selectedMessage,
//                                            currentUser = user,
//                                            isInThread = listViewModel.isInThread,
//                                            ownCapabilities = selectedMessageState.ownCapabilities,
//                                        ),
//                                        message = selectedMessage,
//                                        ownCapabilities = selectedMessageState.ownCapabilities,
//                                        onMessageAction = { action ->
//                                            Log.e("CustomMessageList", "onMessageAction: $action")
//
//                                            composerViewModel.performMessageAction(action)
//                                            listViewModel.performMessageAction(action)
//                                        },
//                                        onShowMoreReactionsSelected = {
//                                            listViewModel.selectExtendedReactions(selectedMessage)
//                                        },
//                                        onDismiss = { listViewModel.removeOverlay() },
//
//                                        )
                                }

                                is SelectedMessageReactionsState -> {
//                                    SelectedReactionsMenu(
//                                        modifier = Modifier
//                                            .align(Alignment.Center)
//                                            .padding(horizontal = 20.dp)
//                                            .wrapContentSize(),
//                                        shape = shapes.attachment,
//                                        message = selectedMessage,
//                                        currentUser = user,
//                                        onMessageAction = { action ->
//                                            composerViewModel.performMessageAction(action)
//                                            listViewModel.performMessageAction(action)
//                                        },
//                                        onShowMoreReactionsSelected = {
//                                            listViewModel.selectExtendedReactions(selectedMessage)
//                                        },
//                                        onDismiss = { listViewModel.removeOverlay() },
//                                        ownCapabilities = selectedMessageState.ownCapabilities,
//                                    )
                                }

                                is SelectedMessageReactionsPickerState -> {
//                                    ReactionsPicker(
//                                        modifier = Modifier
//                                            .align(Alignment.Center)
//                                            .padding(horizontal = 20.dp)
//                                            .wrapContentSize(),
//                                        shape = shapes.attachment,
//                                        message = selectedMessage,
//                                        onMessageAction = { action ->
//                                            composerViewModel.performMessageAction(action)
//                                            listViewModel.performMessageAction(action)
//                                        },
//                                        onDismiss = { listViewModel.removeOverlay() },
//                                    )
                                }

                                else -> Unit
                            }
                        }
                    }
//                MyComposeScreen("messaging:!members-zpdKwxmT5xg3bH4HXljyiB0_EWX9Vno99BXhn8fzt40", chatClient)

                }
            }

            } else { // 일반적인 채팅일 때
            setContent {

                ChatTheme(
                    attachmentFactories = customFactories + defaultFactories,
                ) {
                    MessagesScreen(viewModelFactory = factory)
//                MyCustomUi()
                    val isShowingAttachments = attachmentsPickerViewModel.isShowingAttachments
                    val selectedMessageState = listViewModel.currentMessagesState.selectedMessageState
//                val user by listViewModel.user.collectAsState()
                    val lazyListState = rememberMessageListState()

                    val context = LocalContext.current
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val textState = remember { mutableStateOf("") }


// LocationActivityResultContract를 사용하여 런처를 기억
                    val locationLauncher = rememberLauncherForActivityResult(
                        contract = LocationActivityResultContract()
                    ) { result ->
                        result?.let { location ->
                            val attachment = Attachment(
                                type = "location",
                                extraData = mutableMapOf(
                                    "latitude" to location.latitude,
                                    "longitude" to location.longitude,
                                )
                            )
                            composerViewModel.addSelectedAttachments(listOf(attachment))

                        }
                    }



                    Box(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            topBar = {
                                val connectionState by listViewModel.connectionState.collectAsState()

                                MessageListHeader(
                                    trailingContent = {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_location), // 고정된 이미지 리소스 ID
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(40.dp) // onChannelAvatar와 동일한 크기
                                                .clickable {
                                                    locationLauncher.launch(null)
                                                }
                                        )},

                                    channel = listViewModel.channel,
                                    currentUser = user,
                                    connectionState = connectionState,
                                    onBackPressed = { finish() },
                                    onHeaderTitleClick = {
                                    },
                                    onChannelAvatarClick = {
//                                        locationLauncher.launch(null)

//                                    locationActivityResultLauncher.launch(null)

                                    },

                                    )

                            },
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                CustomMessageComposer(composerViewModel,
                                )
//                            CustomMessageComposer(
////                                onDateSelected = { date ->
////                                    // 2
////                                    val payload = SimpleDateFormat("MMMM dd, yyyy").format(Date(date))
////                                    val attachment = Attachment(
////                                        type = "date",
////                                        extraData = mutableMapOf("payload" to payload)
////                                    )
////
////                                    // 3
////                                    composerViewModel.addSelectedAttachments(listOf(attachment))
////                                }
////                            )
////                            CustomMessageComposer(
////                                composerViewModel,
////                                attachmentsPickerViewModel
////                            )
////                            MyCustomComposer()
////                            MessageComposer(
////                                modifier = Modifier
////                                    .fillMaxWidth()
////                                    .wrapContentHeight(),
////                                viewModel = composerViewModel,
////                                onAttachmentsClick = { attachmentsPickerViewModel.changeAttachmentState(true) },
////
////                                statefulStreamMediaRecorder = statefulStreamMediaRecorder,
////                            )
                            },
                        ) {
                            val currentState = listViewModel.currentMessagesState

                            MessageList(
                                modifier = Modifier
                                    .padding(it)
                                    .background(ChatTheme.colors.appBackground)
                                    .fillMaxSize(),
                                viewModel = listViewModel,
                                messagesLazyListState = if (listViewModel.isInThread) rememberMessageListState(parentMessageId = currentState.parentMessageId) else lazyListState,
                                onThreadClick = { message ->
                                    composerViewModel.setMessageMode(MessageMode.MessageThread(message))
                                    listViewModel.openMessageThread(message)
                                },
                                onMediaGalleryPreviewResult = { result ->
                                    when (result?.resultType) {
                                        MediaGalleryPreviewResultType.QUOTE -> {
                                            val message = listViewModel.getMessageById(result.messageId)

                                            if (message != null) {
                                                composerViewModel.performMessageAction(Reply(message))
                                            }
                                        }

                                        MediaGalleryPreviewResultType.SHOW_IN_CHAT -> {
                                        }

                                        null -> Unit
                                    }
                                },
                            )
                        }



                        if (isShowingAttachments) {
                            val defaultTabFactories = AttachmentsPickerTabFactories.defaultFactories(takeImageEnabled = true, recordVideoEnabled = false, filesTabEnabled = false)
                            val tabFactories = defaultTabFactories
//                        val tabFactories = defaultTabFactories

                            AttachmentsPicker(
                                attachmentsPickerViewModel = attachmentsPickerViewModel,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .height(350.dp),
                                onAttachmentsSelected = { attachments ->
                                    attachmentsPickerViewModel.changeAttachmentState(false)
                                    composerViewModel.addSelectedAttachments(attachments)
                                },
                                onDismiss = {
                                    attachmentsPickerViewModel.changeAttachmentState(false)
                                    attachmentsPickerViewModel.dismissAttachments()
                                },
                                tabFactories = tabFactories
                            )
                        }

                        if (selectedMessageState != null) {
                            val selectedMessage = selectedMessageState.message
                            val messageActions = listViewModel.messageActions

                            when (selectedMessageState) {
                                is SelectedMessageOptionsState -> {
                                    SelectedMessageMenu(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 20.dp)
                                            .wrapContentSize(),
                                        shape = shapes.attachment,
                                        messageOptions = defaultMessageOptionsState(
                                            selectedMessage = selectedMessage,
                                            currentUser = user,
                                            isInThread = listViewModel.isInThread,
                                            ownCapabilities = selectedMessageState.ownCapabilities,
                                        ),
                                        message = selectedMessage,
                                        ownCapabilities = selectedMessageState.ownCapabilities,
                                        onMessageAction = { action ->
                                            Log.e("CustomMessageList", "onMessageAction: $action")

                                            composerViewModel.performMessageAction(action)
                                            listViewModel.performMessageAction(action)
                                        },
                                        onShowMoreReactionsSelected = {
                                            listViewModel.selectExtendedReactions(selectedMessage)
                                        },
                                        onDismiss = { listViewModel.removeOverlay() },

                                        )
                                }

                                is SelectedMessageReactionsState -> {
                                    SelectedReactionsMenu(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 20.dp)
                                            .wrapContentSize(),
                                        shape = shapes.attachment,
                                        message = selectedMessage,
                                        currentUser = user,
                                        onMessageAction = { action ->
                                            composerViewModel.performMessageAction(action)
                                            listViewModel.performMessageAction(action)
                                        },
                                        onShowMoreReactionsSelected = {
                                            listViewModel.selectExtendedReactions(selectedMessage)
                                        },
                                        onDismiss = { listViewModel.removeOverlay() },
                                        ownCapabilities = selectedMessageState.ownCapabilities,
                                    )
                                }

                                is SelectedMessageReactionsPickerState -> {
                                    ReactionsPicker(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 20.dp)
                                            .wrapContentSize(),
                                        shape = shapes.attachment,
                                        message = selectedMessage,
                                        onMessageAction = { action ->
                                            composerViewModel.performMessageAction(action)
                                            listViewModel.performMessageAction(action)
                                        },
                                        onDismiss = { listViewModel.removeOverlay() },
                                    )
                                }

                                else -> Unit
                            }
                        }
                    }
//                MyComposeScreen("messaging:!members-zpdKwxmT5xg3bH4HXljyiB0_EWX9Vno99BXhn8fzt40", chatClient)

                }
            }
        }


    }

        @Composable
    fun CustomMessageComposer(
        messageComposerViewModel: MessageComposerViewModel,
//        onDateSelected: (Long) -> Unit,

        ) {
        val activity = LocalContext.current as AppCompatActivity

        MessageComposer(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            viewModel = messageComposerViewModel,
            onValueChange = { composerViewModel.setMessageInput(it) },
            onAttachmentRemoved = { composerViewModel.removeSelectedAttachment(it) },

            integrations = { // here
                IconButton(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp),
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more_dots),
                            contentDescription = null,
                            tint = ChatTheme.colors.textLowEmphasis
                        )
                    },
                    onClick = {
//                        MaterialDatePicker.Builder
//                            .datePicker()
//                            .build()
//                            .apply {
//                                show(activity.supportFragmentManager, null)
//                                addOnPositiveButtonClickListener {
//                                    onDateSelected(it)
//                                }
//                            }

                        attachmentsPickerViewModel.changeAttachmentState(showAttachments = true)
                    }
                )
            }
        )
    }




    override fun onBackPressed() {
        // Handle any custom behavior here if needed

        // Call the super method to handle the default back button action
        super.onBackPressed()
        finish()
    }


    override fun onResume() {
        super.onResume()
        ActivityState.currentActivity = this::class.java.simpleName
        ActivityState.currentChannelId = cid

        cancelChatRoomNotification(cid)
    }

    override fun onPause() {
        super.onPause()
        ActivityState.currentActivity = null
        ActivityState.currentChannelId = null
    }


    override fun onDestroy() {
        super.onDestroy()
//        finishAffinity()
    }


    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, ChannelActivity::class.java).putExtra(CID_KEY, channel.cid)

        fun createLaunchIntent(
            context: Context,
            messageId: String,
            parentMessageId: String?,
            channelType: String,
            channelId: String,
        ) = Intent(context, ChannelActivity::class.java).apply {
            putExtra(EXTRA_CHANNEL_ID, channelId)
            putExtra(EXTRA_CHANNEL_TYPE, channelType)
            putExtra(EXTRA_MESSAGE_ID, messageId)
            putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
        }
    }

    private fun cancelChatRoomNotification(cid: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = cid.hashCode()

        notificationManager.cancel(notificationId)  // 채팅방 ID를 사용하여 해당 채팅방 알림 취소
    }


    class LocationActivityResultContract : ActivityResultContract<Void?, LatLng?>() {
        override fun createIntent(context: Context, input: Void?): Intent {
            // ShareLocationActivity를 시작하기 위한 인텐트 생성
            return Intent(context, ShareLocationActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): LatLng? {
            // 결과 처리
            if (resultCode == RESULT_OK && intent != null) {
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                Log.e("latitude, longitude", "${latitude} ${longitude}")
                return LatLng(latitude, longitude)
            }
            return null

        }
    }

//    class LocationActivityResultContract : ActivityResultContract<Void?, LocationResult?>() {
//        override fun createIntent(context: Context, input: Void?): Intent {
//            return Intent(context, ShareLocationActivity::class.java)
//        }
//
//        override fun parseResult(resultCode: Int, intent: Intent?): LocationResult? {
//            if (resultCode == Activity.RESULT_OK && intent != null) {
//                val latitude = intent.getDoubleExtra("latitude", 0.0)
//                val longitude = intent.getDoubleExtra("longitude", 0.0)
//                val snapshotUri = intent.getStringExtra("snapshot_uri")
//                Log.e("latitude, longitude, snapshotUri", "$latitude, $longitude, $snapshotUri")
//                return LocationResult(LatLng(latitude, longitude), snapshotUri)
//            }
//            return null
//        }
//    }
//
//    data class LocationResult(val location: LatLng, val snapshotUri: String?)



}