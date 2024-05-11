package com.kwonminseok.busanpartners.ui.message


import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.databinding.ActivityChannelBinding
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_ID
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_TYPE
import com.kwonminseok.busanpartners.ui.EXTRA_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.EXTRA_PARENT_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.naver.maps.geometry.LatLng
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.querysort.QuerySorter
import io.getstream.chat.android.ui.ChatUI
import io.getstream.chat.android.ui.common.state.messages.Edit
import io.getstream.chat.android.ui.common.state.messages.MessageMode
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.AttachmentPreviewFactoryManager
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.FileAttachmentPreviewFactory
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.MediaAttachmentPreviewFactory
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactoryManager
import io.getstream.chat.android.ui.helper.SupportedReactions
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.messages.bindView

//class ChannelActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // 1 - Load the ID of the selected channel
//        val channelId = intent.getStringExtra(KEY_CHANNEL_ID)!!
//
//        // 2 - Add the MessagesScreen to your UI
//        setContent {
//            ChatTheme {
//                MessagesScreen(
//                    viewModelFactory = MessagesViewModelFactory(
//                        context = this,
//                        channelId = channelId,
//                        messageLimit = 30
//                    ),
//                    onBackPressed = { finish() }
//                )
//            }
//        }
//    }
//
//    // 3 - Create an intent to start this Activity, with a given channelId
//    companion object {
//        private const val KEY_CHANNEL_ID = "channelId"
//
//        fun getIntent(context: Context, channelId: String): Intent {
//            return Intent(context, ChannelActivity::class.java).apply {
//                putExtra(KEY_CHANNEL_ID, channelId)
//            }
//        }
//    }
//}





private val TAG = "ChannelActivity"
class ChannelActivity : AppCompatActivity() {
    private var cid: String = ""  // 채팅방 ID를 저장하는 변수
    private lateinit var binding: ActivityChannelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 0 - inflate binding
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)


        cid = checkNotNull(intent.getStringExtra(CID_KEY)) {
            "Specifying a channel id is required when starting ChannelActivity"
        }
        Log.e(TAG, cid)



        // 예시를 보여줄 때 -> 아직 제대로 인증이 되지 않았을 경우, 예시 채팅방만 볼 수 있다.
        if (cid == "messaging:ExampleChat") {
            // Step 1 - Create three separate ViewModels for the views so it's easy
            //          to customize them individually
            val factory = MessageListViewModelFactory(this, cid)
            val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
            val messageListViewModel: MessageListViewModel by viewModels { factory }
            val messageComposerViewModel: MessageComposerViewModel by viewModels { factory }

            // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
            messageListHeaderViewModel.bindView(binding.messageListHeaderView, this)
            messageListViewModel.bindView(binding.messageListView, this)
            messageComposerViewModel.bindView(binding.messageComposerView, this)


            // Step 3 - Let both MessageListHeaderView and MessageComposerView know when we open a thread
            //MessageThread 모드는 특정 메시지 스레드에 대한 메시지를 작성하는 것을 의미,
            //Normal 모드는 단체 채팅방에서 전반적인 메시지를 작성하는 상태를 나타냅니다.
            messageListViewModel.mode.observe(this) { mode ->
                when (mode) {
                    is MessageMode.MessageThread -> {
                        messageListHeaderViewModel.setActiveThread(mode.parentMessage)
                        messageComposerViewModel.setMessageMode(MessageMode.MessageThread(mode.parentMessage))
                    }

                    is MessageMode.Normal -> {
                        messageListHeaderViewModel.resetThread()
                        messageComposerViewModel.leaveThread()
                    }
                }
            }


            // Step 4 - Let the message input know when we are editing a message
            binding.messageListView.setMessageEditHandler { message ->
                messageComposerViewModel.performMessageAction(Edit(message))
            }

            // Step 5 - Handle navigate up state
            messageListViewModel.state.observe(this) { state ->
                if (state is MessageListViewModel.State.NavigateUp) {
                    finish()
                }
            }

            // Step 6 - Handle back button behaviour correctly when you're in a thread
            val backHandler = {
                messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
            }
            binding.messageListHeaderView.setBackButtonClickListener(backHandler)
            onBackPressedDispatcher.addCallback(this) {
                backHandler()
            }

            // 사용자가 스레드 답글을 만들 수 있는지 여부
            binding.messageListView.setThreadsEnabled(false)
            // 사용자가 메시지를 편집할 수 있는지 여부.
            binding.messageListView.setEditMessageEnabled(false)
            binding.messageListView.setDeleteMessageEnabled(false)
            binding.messageListView.setReactionsEnabled(false)

            // 매 채팅마다 프로필 사진을 보여주는 코드
            binding.messageListView.setShowAvatarPredicate { messageItem ->
                messageItem.isTheirs
            }


            val reactions = mapOf(
                "thumbs_up" to SupportedReactions.ReactionDrawable(
                    inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_up)!!,
                    activeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_up)!!
                ),
                "thumbs_down" to SupportedReactions.ReactionDrawable(
                    inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_down)!!,
                    activeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_down)!!
                ),
            )
//        binding.messageListView.setAttachmentFactoryManager()
            val locationActivityResultLauncher =
                registerForActivityResult(LocationActivityResultContract()) { result ->
                    // 여기에서 결과 처리, 예를 들면 받아온 LatLng 객체 사용
                    result?.let { location ->

                        val attachment = Attachment(
                            type = "location",
                            extraData = mutableMapOf(
                                "latitude" to location.latitude,
                                "longitude" to location.longitude,
                            )
                        )

                        val message = Message(
                            cid = cid,
                            text = "지도 공유",
                            attachments = mutableListOf(attachment)
                        )
                        Log.e("message", message.toString())

                        BusanPartners.chatClient.channel(cid).sendMessage(message).enqueue { result ->
                            if (result.isSuccess) {
                                // 메시지 전송 성공 처리


                                Log.e("지도 사진 처리 성공", "성공")
                            } else {
                                // 메시지 전송 실패 처리
                                Log.e("지도 사진 처리 실패", result.toString())

                            }
                        }

                    }
                }

            ChatUI.supportedReactions = SupportedReactions(this, reactions)
            binding.messageComposerView.setLeadingContent(
                CustomMessageComposerLeadingContent(this).also {
                    it.attachmentsButtonClickListener = {
                        binding.messageComposerView.attachmentsButtonClickListener(

                        )
                    }

                }
            )



            ChatUI.attachmentPreviewFactoryManager = AttachmentPreviewFactoryManager(
                attachmentPreviewFactories = listOf(
//                DateAttachmentPreviewFactory(),
                    MediaAttachmentPreviewFactory(),
                    FileAttachmentPreviewFactory()
                )
            )

            ChatUI.attachmentFactoryManager = AttachmentFactoryManager(
                attachmentFactories = listOf(
//                DateAttachmentFactory(),
                    LocationAttachmentViewFactory(lifecycleOwner = this)
                )
            )
        } else { // 평범한 대화일 경우
            // Step 1 - Create three separate ViewModels for the views so it's easy
            //          to customize them individually
            val factory = MessageListViewModelFactory(this, cid)
            val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
            val messageListViewModel: MessageListViewModel by viewModels { factory }
            val messageComposerViewModel: MessageComposerViewModel by viewModels { factory }

            // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
            messageListHeaderViewModel.bindView(binding.messageListHeaderView, this)
            messageListViewModel.bindView(binding.messageListView, this)
            messageComposerViewModel.bindView(binding.messageComposerView, this)


            // Step 3 - Let both MessageListHeaderView and MessageComposerView know when we open a thread
            //MessageThread 모드는 특정 메시지 스레드에 대한 메시지를 작성하는 것을 의미,
            //Normal 모드는 단체 채팅방에서 전반적인 메시지를 작성하는 상태를 나타냅니다.
            messageListViewModel.mode.observe(this) { mode ->
                when (mode) {
                    is MessageMode.MessageThread -> {
                        messageListHeaderViewModel.setActiveThread(mode.parentMessage)
                        messageComposerViewModel.setMessageMode(MessageMode.MessageThread(mode.parentMessage))
                    }

                    is MessageMode.Normal -> {
                        messageListHeaderViewModel.resetThread()
                        messageComposerViewModel.leaveThread()
                    }
                }
            }


            // Step 4 - Let the message input know when we are editing a message
            binding.messageListView.setMessageEditHandler { message ->
                messageComposerViewModel.performMessageAction(Edit(message))
            }

            // Step 5 - Handle navigate up state
            messageListViewModel.state.observe(this) { state ->
                if (state is MessageListViewModel.State.NavigateUp) {
                    finish()
                }
            }

            // Step 6 - Handle back button behaviour correctly when you're in a thread
            val backHandler = {
                messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
            }
            binding.messageListHeaderView.setBackButtonClickListener(backHandler)
            onBackPressedDispatcher.addCallback(this) {
                backHandler()
            }

            // 사용자가 스레드 답글을 만들 수 있는지 여부
            binding.messageListView.setThreadsEnabled(false)
            // 사용자가 메시지를 편집할 수 있는지 여부.
            binding.messageListView.setEditMessageEnabled(false)

//            // 매 채팅마다 프로필 사진을 보여주는 코드
//            binding.messageListView.setShowAvatarPredicate { messageItem ->
//                messageItem.isTheirs
//            }


            val reactions = mapOf(
                "thumbs_up" to SupportedReactions.ReactionDrawable(
                    inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_up)!!,
                    activeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_up)!!
                ),
                "thumbs_down" to SupportedReactions.ReactionDrawable(
                    inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_down)!!,
                    activeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_down)!!
                ),
            )
//        binding.messageListView.setAttachmentFactoryManager()


            val locationActivityResultLauncher =
                registerForActivityResult(LocationActivityResultContract()) { result ->
                    // 여기에서 결과 처리, 예를 들면 받아온 LatLng 객체 사용
                    result?.let { location ->

                        val attachment = Attachment(
                            type = "location",
                            extraData = mutableMapOf(
                                "latitude" to location.latitude,
                                "longitude" to location.longitude,
                            )
                        )

                        val message = Message(
                            cid = cid,
                            text = "지도 공유",
                            attachments = mutableListOf(attachment)
                        )
                        Log.e("message", message.toString())

                        BusanPartners.chatClient.channel(cid).sendMessage(message).enqueue { result ->
                            if (result.isSuccess) {
                                // 메시지 전송 성공 처리


                                Log.e("지도 사진 처리 성공", "성공")
                            } else {
                                // 메시지 전송 실패 처리
                                Log.e("지도 사진 처리 실패", result.toString())

                            }
                        }

                    }
                }


            ChatUI.supportedReactions = SupportedReactions(this, reactions)
            binding.messageComposerView.setLeadingContent(
                CustomMessageComposerLeadingContent(this).also {
                    it.attachmentsButtonClickListener = {
                        binding.messageComposerView.attachmentsButtonClickListener(

                        )
                    }





                    it.locationButtonClickListener = {
//                    val intent = Intent(this, ShareLocationActivity::class.java)
//                    intent.putExtra("key:cid", cid)
//
//                    startActivity(intent)

                        // ShareLocationActivity를 시작하고 결과를 기다립니다.
                        locationActivityResultLauncher.launch(null)

                    }


//
                }
            )



            ChatUI.attachmentPreviewFactoryManager = AttachmentPreviewFactoryManager(
                attachmentPreviewFactories = listOf(
//                DateAttachmentPreviewFactory(),

                    MediaAttachmentPreviewFactory(),
                    FileAttachmentPreviewFactory()
                )
            )

            ChatUI.attachmentFactoryManager = AttachmentFactoryManager(
                attachmentFactories = listOf(
//                DateAttachmentFactory(),
                    LocationAttachmentViewFactory(lifecycleOwner = this)
                )
            )
        }


        cancelChatRoomNotification(cid)

    }



    override fun onResume() {
        super.onResume()
        cancelChatRoomNotification(cid)
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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
            if (resultCode == Activity.RESULT_OK && intent != null) {
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                return LatLng(latitude, longitude)
            }
            return null

        }
    }



}
