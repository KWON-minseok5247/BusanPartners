package com.kwonminseok.busanpartners.mainScreen.message


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ActivityChannelBinding
import com.kwonminseok.busanpartners.util.CustomMessageComposerLeadingContent
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.ui.ChatUI
import io.getstream.chat.android.ui.common.state.messages.Edit
import io.getstream.chat.android.ui.common.state.messages.MessageMode
import io.getstream.chat.android.ui.helper.SupportedReactions
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.messages.bindView

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



        // 여기에서 Composer 내부를 수정할 수 있다. 사용 방법은 CustomMessageComposer에서 각 내용을 추가하면 된다.
//        binding.messageComposerView.setLeadingContent(CustomMessageComposerLeadingContent(this))

//        val customOwnCapabilities = setOf(
//            ChannelCapabilities.SEND_MESSAGE,
//            ChannelCapabilities.SEND_LINKS,
//            ChannelCapabilities.UPLOAD_FILE,
////            ChannelCapabilities.SEND_TYPING_EVENTS, // 이걸 삭제하면 giphy 이 기능이 비활성화되는 것 같네
//            // 근데 다시 클릭하니까 활성화가 됨.
//        )
//        val messageComposerState = MessageComposerState(
//            ownCapabilities = customOwnCapabilities
//        )
//
//        binding.messageComposerView.renderState(messageComposerState)
        val reactions = mapOf(
            "thumbs_up" to SupportedReactions.ReactionDrawable(
                inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_up)!!,
                activeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_up)!!
            ),
            "thumbs_down" to SupportedReactions.ReactionDrawable(
                inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_down)!!,
                activeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_thumb_down)!!
            ),
//            "mood_good" to SupportedReactions.ReactionDrawable(
//                inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.ic_mood_good)!!,
//                activeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_mood_good_selected)!!
//            ),
//            "mood_bad" to SupportedReactions.ReactionDrawable(
//                inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.ic_mood_bad)!!,
//                activeDrawable = ContextCompat.getDrawable(this, R.drawable.ic_mood_bad_selected)!!
//            )
        )

        ChatUI.supportedReactions = SupportedReactions(this, reactions)
        binding.messageComposerView.setLeadingContent(
            CustomMessageComposerLeadingContent(this).also {
                it.attachmentsButtonClickListener = { binding.messageComposerView.attachmentsButtonClickListener(

                )
                }
//                it.commandsButtonClickListener = { messageComposerView.commandsButtonClickListener() }
//                it.calendarButtonClickListener = {
//                    // Create an instance of a date picker dialog
//                    val datePickerDialog = MaterialDatePicker.Builder
//                        .datePicker()
//                        .build()
//
//                    // Add an attachment to the message input when the user selects a date
//                    datePickerDialog.addOnPositiveButtonClickListener { date ->
//                        val payload = SimpleDateFormat("MMMM dd, yyyy").format(Date(date))
//                        val attachment = Attachment(
//                            type = "date",
//                            extraData = mutableMapOf("payload" to payload)
//                        )
//                        messageComposerViewModel.addSelectedAttachments(listOf(attachment))
//                    }
//
//                    // Show the date picker dialog on a click on the calendar button
//                    datePickerDialog.show(childFragmentManager, null)
//                }
            }
        )









    }

//    override fun onBackPressed() {
//        val fragmentManager = supportFragmentManager
//        val messageFragment = fragmentManager.findFragmentByTag("MessageFragment")
//
//        // messageFragment가 현재 보이지 않는다면 뒤로가기를 처리하고 아니면 해당 프래그먼트로 이동
//        if (messageFragment == null || !messageFragment.isVisible) {
//            super.onBackPressed()
//        } else {
//            // messageFragment가 현재 보이는 상태라면 해당 프래그먼트로 이동
//            // 예를 들어, replace 메서드를 사용하여 이동할 수 있습니다.
//            fragmentManager.beginTransaction()
//                .replace(R.id.homeHostFragment, messageFragment)
//                .addToBackStack(null) // 백스택에 추가하여 뒤로가기 버튼으로 다시 돌아올 수 있도록 함
//                .commit()
//        }
//    }

//override fun onBackPressed() {
//    val navController = findNavController(R.id.homeHostFragment)
//    // MessageFragment의 ID가 R.id.messageFragment라고 가정
//    val messageFragmentId = R.id.messageFragment
//
//    // 현재 목적지가 이미 MessageFragment인지 확인
//    if (navController.currentDestination?.id == messageFragmentId) {
//        super.onBackPressed()
//    } else {
//        // MessageFragment로 네비게이트
//        navController.popBackStack() // 현재 스택을 클리어
//        navController.navigate(messageFragmentId)
//    }
//}


    companion object {
        private const val CID_KEY = "key:cid"

        fun newIntent(context: Context, channel: Channel): Intent =
            Intent(context, ChannelActivity::class.java).putExtra(CID_KEY, channel.cid)
    }
}
