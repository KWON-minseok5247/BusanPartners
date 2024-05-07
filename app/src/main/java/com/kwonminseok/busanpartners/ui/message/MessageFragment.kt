package com.kwonminseok.busanpartners.ui.message

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.databinding.FragmentMessageBinding
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.viewmodel.ChatInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.MarkAllReadEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.events.NotificationChannelMutesUpdatedEvent
import io.getstream.chat.android.client.events.NotificationMarkReadEvent
import io.getstream.chat.android.client.events.NotificationMessageNewEvent
import io.getstream.chat.android.client.subscribeFor
import io.getstream.chat.android.client.token.TokenProvider
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.ChannelMute
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.ui.databinding.StreamUiFragmentChannelListBinding
import io.getstream.chat.android.ui.databinding.StreamUiFragmentMessageListBinding
import io.getstream.chat.android.ui.feature.channels.ChannelListActivity
import io.getstream.chat.android.ui.feature.channels.ChannelListFragment
import io.getstream.chat.android.ui.feature.channels.list.ChannelListView
import io.getstream.chat.android.ui.feature.messages.MessageListFragment
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

@AndroidEntryPoint
class MessageFragment : ChannelListFragment()
{

//    private val viewModel: ChatInfoViewModel by viewModels()
//    private var _binding: StreamUiFragmentChannelListBinding? = null
//    private val binding get() = _messageBinding!!

//    private var _binding: StreamUiFragmentChannelListBinding? = null
//    protected override val binding: StreamUiFragmentChannelListBinding get() = _binding!!

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _messageBinding = StreamUiFragmentChannelListBinding.inflate(layoutInflater)
//        return messageBinding.root
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기는 대학생 목록에서 원하는 대학생과 메세지를 보내는 과정
        getStudentChat()

        binding.channelListView.setIsMoreOptionsVisible { channel ->
            // You can determine visibility based on the channel object.
            ContextCompat.getDrawable(requireContext(), R.drawable.pusan_logo)

            false
        }

        binding.channelListHeaderView.setOnActionButtonClickListener {
            // 클릭 이벤트를 비어 있는 블록으로 처리하여 아무 작업도 수행하지 않음

        }



        binding.channelListView.setIsDeleteOptionVisible { channel ->
            // You can determine visibility based on the channel object.
            // Here is the default implementation:
            channel.ownCapabilities.contains("delete-channel")
            false
        }

        binding.channelListView.setChannelLongClickListener { channel ->
            //todo 여기서 삭제나 알림을 끄는 선택지를 제공하면 된다.
            val options = arrayOf("채팅방 알림 끄기", "채팅방 나가기", "33")
            AlertDialog.Builder(requireContext())
//                .setTitle("Channel Options")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> muteChat(channel.id)
                        // "Mute User" 선택 시
                        1 -> unmuteChat(channel.id)
                        2 -> ""

                    }
                }
                .show()
            true
        }

        // ViewModel 바인딩과 UI 업데이트
//        getChatList()


    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _messageBinding = null
//    }


    private fun getChatList() {
        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()

        val channelListFactory: ChannelListViewModelFactory =
            ChannelListViewModelFactory(
                filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`(
                        "members",
                        listOf(chatClient.getCurrentUser()!!.id)
                    ),
                ),
                sort = QuerySortByField.descByName("last_updated"),
                limit = 30,

                )
        val channelListViewModel: ChannelListViewModel by viewModels { channelListFactory }




        channelListHeaderViewModel.bindView(binding.channelListHeaderView, this)
        channelListViewModel.bindView(binding.channelListView, viewLifecycleOwner)

        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity.newIntent(requireContext(), channel))
//            startActivity(ChannelActivity.getIntent(requireContext(), channel.cid))

        }
        binding.channelListView.setIsDeleteOptionVisible { true }

        binding.channelListView.setIsMoreOptionsVisible { channel ->
            // You can determine visibility based on the channel object.
            ContextCompat.getDrawable(requireContext(), R.drawable.pusan_logo)

            false
        }



        binding.channelListView.setIsDeleteOptionVisible { channel ->
            // You can determine visibility based on the channel object.
            // Here is the default implementation:
            channel.ownCapabilities.contains("delete-channel")
            false
        }

        binding.channelListView.setChannelLongClickListener { channel ->
            //todo 여기서 삭제나 알림을 끄는 선택지를 제공하면 된다.
            val options = arrayOf("채팅방 알림 끄기", "채팅방 나가기", "33")
            AlertDialog.Builder(requireContext())
//                .setTitle("Channel Options")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> muteChat(channel.id)
                        // "Mute User" 선택 시
                        1 -> unmuteChat(channel.id)
                        2 -> ""

                    }
                }
                .show()
            true
        }

//        chatClient.subscribeFor(
//            NewMessageEvent::class,
//            NotificationMessageNewEvent::class,
//            MarkAllReadEvent::class,
//            NotificationMarkReadEvent::class
//        ) { event ->
//            when (event) {
//                is NewMessageEvent -> {
//                    val unreadChannels = event.unreadChannels
//                    val totalUnreadCount = event.totalUnreadCount
//                }
//                is NotificationMessageNewEvent -> {
//                    val unreadChannels = event.unreadChannels
//                    val totalUnreadCount = event.totalUnreadCount
//                }
//                is MarkAllReadEvent -> {
//                    val unreadChannels = event.unreadChannels
//                    val totalUnreadCount = event.totalUnreadCount
//                }
//                is NotificationMarkReadEvent -> {
//                    val unreadChannels = event.unreadChannels
//                    val totalUnreadCount = event.totalUnreadCount
//                }
//
//                else -> {}
//            }
//        }


//        subscribeForChannelMutesUpdatedEvents()


    }

    private fun getStudentChat() {


        // 일단 원인은 찾아냈다. -> 채널이 이상하게 꼬인 것 같음.
        val studentUid = arguments?.getString("studentUid", null)
        if (studentUid != null) {
            val channelClient = chatClient.channel(channelType = "messaging", channelId = "")
            channelClient?.create(
                memberIds = listOf(studentUid, chatClient.getCurrentUser()!!.id),
                extraData = emptyMap()
            )?.enqueue { result ->
                if (result.isSuccess) {
                    val newChannel = result.getOrThrow()
                    startActivity(ChannelActivity.newIntent(requireContext(), newChannel))
//                    startActivity(ChannelActivity.getIntent(requireContext(), newChannel.id))

                } else {
                    Log.e(
                        "Channel Creation Failed",
                        result.toString() ?: "Error creating channel"
                    )
                }
            }
        }
    }

    private fun muteChat(channelId: String) {


    chatClient.muteChannel("messaging", channelId).enqueue { result ->
        if (result.isSuccess) {
            Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
        } else {
            Log.e("ChatMute", "Failed to mute: ${result}")
        }
    }

    }
    private fun unmuteChat(channelId: String) {

        chatClient.unmuteChannel("messaging", channelId).enqueue { result ->
            if (result.isSuccess) {
                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ChatMute", "Failed to mute: ${result}")
            }
        }

    }
//    private fun subscribeForChannelMutesUpdatedEvents() {
//        chatClient.subscribeFor<NotificationChannelMutesUpdatedEvent>(viewLifecycleOwner) {
//            viewModel.onAction(ChatInfoViewModel.Action.ChannelMutesUpdated(it.me.channelMutes))
//        }
////        ChatClient.instance().subscribeFor<NotificationChannelMutesUpdatedEvent>(viewLifecycleOwner) {
////            viewModel.onAction(ChatInfoViewModel.Action.ChannelMutesUpdated(it.me.channelMutes))
////        }
//    }


}



