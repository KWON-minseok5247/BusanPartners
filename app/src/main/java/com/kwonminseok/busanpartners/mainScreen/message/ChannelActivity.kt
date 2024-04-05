package com.kwonminseok.busanpartners.mainScreen.message


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ActivityChannelBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
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
import java.text.SimpleDateFormat
import java.util.Date

class ChannelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChannelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 0 - inflate binding
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cid = checkNotNull(intent.getStringExtra(CID_KEY)) {
            "Specifying a channel id is required when starting ChannelActivity"
        }
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


        // 좌표가 수신됐는지 확인하는 용도
        val temporaryLatitude = intent.getDoubleExtra("latitude", 0.0)

        // 데이터를 기반으로 필요한 로직 수행
        if (temporaryLatitude != 0.0) {
            // 로직 수행...
            val latitude = intent.getDoubleExtra("latitude", 35.1798159)
            val longitude = intent.getDoubleExtra("longitude", 129.0750222)

            val attachment = Attachment(
                type = "location",
                extraData = mutableMapOf(
                    "latitude" to latitude,
                    "longitude" to longitude,
                )
            )

            val message = Message(
                cid = cid,
                text = "Shared location",
                attachments = mutableListOf(attachment)
            )

            ChatClient.instance().channel(cid).sendMessage(message).enqueue { result ->
                if (result.isSuccess) {
                    // 메시지 전송 성공 처리
                    Log.e("지도 사진 처리 성공", "성공")
                } else {
                    // 메시지 전송 실패 처리
                    Log.e("지도 사진 처리 실패", result.toString())

                }
            }

        }





        ChatUI.supportedReactions = SupportedReactions(this, reactions)
        binding.messageComposerView.setLeadingContent(
            CustomMessageComposerLeadingContent(this).also {
                it.attachmentsButtonClickListener = { binding.messageComposerView.attachmentsButtonClickListener(

                )
                }
                it.locationButtonClickListener = {
                    val intent = Intent(this, ShareLocationActivity::class.java)
                    intent.putExtra("key:cid", cid)

                    startActivity(intent)

                }

                it.calendarButtonClickListener = {
                    // Create an instance of a date picker dialog
                    val datePickerDialog = MaterialDatePicker.Builder
                        .datePicker()
                        .build()

                    // Add an attachment to the message input when the user selects a date
                    datePickerDialog.addOnPositiveButtonClickListener { date ->
                        val payload = SimpleDateFormat("MMMM dd, yyyy").format(Date(date))
                        val attachment = Attachment(
                            type = "date",
                            extraData = mutableMapOf("payload" to payload)
                        )
                        messageComposerViewModel.addSelectedAttachments(listOf(attachment))
                    }

                    // Show the date picker dialog on a click on the calendar button
                    datePickerDialog.show(supportFragmentManager, null)
                }

//
            }
        )



        ChatUI.attachmentPreviewFactoryManager = AttachmentPreviewFactoryManager(
            attachmentPreviewFactories = listOf(
                DateAttachmentPreviewFactory(),

                MediaAttachmentPreviewFactory(),
                FileAttachmentPreviewFactory()
            )
        )

        ChatUI.attachmentFactoryManager = AttachmentFactoryManager(
            attachmentFactories = listOf(
                DateAttachmentFactory(),
                LocationAttachmentViewFactory()
            )
        )





    }


    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, ChannelActivity::class.java).putExtra(CID_KEY, channel.cid)
    }
}
