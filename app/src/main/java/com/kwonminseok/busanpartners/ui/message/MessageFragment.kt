package com.kwonminseok.busanpartners.ui.message

import android.annotation.SuppressLint
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
//import com.kwonminseok.busanpartners.databinding.FragmentMessageBinding
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
import io.getstream.chat.android.ui.viewmodel.search.SearchViewModel
import io.getstream.result.call.Call

@AndroidEntryPoint
class MessageFragment : ChannelListFragment() {

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

        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity.newIntent(requireContext(), channel))

        }

        binding.channelListView.setIsDeleteOptionVisible { channel ->
            // You can determine visibility based on the channel object.
            // Here is the default implementation:
            channel.ownCapabilities.contains("delete-channel")
            false
        }

//                binding.channelListView.setChannelItemClickListener { channel ->
//            startActivity(ChannelActivity.createIntent(requireContext(), channel.cid))
////                    val intent = Intent(requireContext(), ChannelActivity::class.java)
////                    intent.putExtra("channelId", channel.id) // 채널 ID를 ChannelActivity로 전달
////                    startActivity(intent)
//
////            startActivity(ChannelActivity.getIntent(requireContext(), channel.cid))
//
//        }

//        binding.channelListView.setChannelItemClickListener { channel ->
//            val intent = Intent(requireContext(), ChannelActivity::class.java)
//            intent.putExtra("channelId", channel.cid) // 채널 ID를 ChannelActivity로 전달
//            startActivity(intent)
//        }

        binding.channelListView.setChannelLongClickListener { channel ->
            //todo 추후 dialog 사이즈 늘리기
            val isMuted =
                chatClient.getCurrentUser()?.channelMutes?.any { it.channel.id == channel.id }
                    ?: false
            // 차단 상태 확인 (임시로 false로 설정, 실제 로직 필요)
//            val isBlocked = checkIfUserIsBlocked(otherUser?.id, channel.cid)
//            val isBlocked = checkIfUserIsBlocked(channel, chatClient.getCurrentUser()!!.id)
            val isBlocked = false
            Log.e("channel", channel.toString())

            Log.e("isBlocked", isBlocked.toString())

            val options = when {
                isMuted && isBlocked -> arrayOf("채팅방 알림 켜기", "사용자 차단 해제", "채팅방 나가기")
                !isMuted && isBlocked -> arrayOf("채팅방 알림 끄기", "사용자 차단 해제", "채팅방 나가기")
                isMuted && !isBlocked -> arrayOf("채팅방 알림 켜기", "사용자 차단", "채팅방 나가기")
                else -> arrayOf("채팅방 알림 끄기", "사용자 차단", "채팅방 나가기")
            }

//                val cid = "$channelType:$channelId"
            AlertDialog.Builder(requireContext())
//                .setTitle("Channel Options")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> if (isMuted) unmuteChat(channel.id) else muteChat(channel.id)
//                        1 -> if (canDelete) chatClient.channel("${channel.type}:${channel.id}").delete().enqueue()
                        1 -> if (isBlocked) unBlockUser(channel) else blockUser(channel)
                        2 -> deleteChattingRoom(channel)
//                        1 -> if (canDelete) chatClient.channel("${channel.type}:${channel.id}").removeMembers(chatClient.getCurrentUser()?.id ?: "").enqueue()

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
    private fun deleteChattingRoom(channel: Channel) {
        val userId = chatClient.getCurrentUser()?.id
        AlertDialog.Builder(requireContext())
            .setTitle("채팅방 나가기")
            .setMessage("정말로 채팅방을 나가시겠습니까?")
            .setPositiveButton("예") { dialog, which ->
                if (userId != null) {
                    chatClient.channel("${channel.type}:${channel.id}")
                        .removeMembers(listOf(userId)).enqueue { result ->
                        if (result.isSuccess) {
                            Log.e("사용자가 채널을 성공적으로 나갔습니다.", "확인")
                        } else {
                            Log.e("사용자가 채널.", "실패")

                        }
                    }
                }

            }
            .setNegativeButton("아니오") { dialog, which ->
                // 아무 작업 없이 다이얼로그 닫기
                Log.e("정상적으로", "아니오.")

            }
            .show()

    }

    private fun checkIfUserIsBlocked(channel: Channel, userId: String): Boolean {
        val member = channel.members.find { it.user.id == userId }
        return member?.user?.banned ?: false
    }


    @SuppressLint("CheckResult")
    private fun blockUser(channel: Channel) {


        Log.e("blockUser", "blockUser가 실행되었습니다.")
        val currentUserId = ChatClient.instance().getCurrentUser()?.id
        val otherUser = channel.members.firstOrNull { it.user.id != currentUserId }?.user
        Log.e("otherUser", otherUser.toString())

        if (otherUser != null) {
            chatClient.channel("messaging", channel.id).shadowBanUser(otherUser.id,
                "유저의 선택으로 밴되었습니다", null)
                .enqueue { result ->
                    if (result.isSuccess) {
                        // 차단 성공
                        Log.e("BanUser", "User $otherUser.id banned successfully")
                    } else {
                        // 차단 실패
                        Log.e("BanUser", "${result.errorOrNull().toString()}")
                    }
                }


//            chatClient.banUser(otherUser.id, "messaging", channel.id, "유저의 선택으로 밴되었습니다.", null)
//                .enqueue {
//                    Toast.makeText(
//                        requireContext(),
//                        "${otherUser.name}님이 차단되었습니다.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
        }

    }

    private fun unBlockUser(channel: Channel) {
        Log.e("unBlockUser", "unBlockUser 실행되었습니다.")

        val currentUserId = ChatClient.instance().getCurrentUser()?.id
        val otherUser = channel.members.firstOrNull { it.user.id != currentUserId }?.user
        Log.e("otherUser", otherUser.toString())
        if (otherUser != null) {
            chatClient.unbanUser(otherUser.id, "messaging", channel.id)
                .enqueue {
                    Toast.makeText(
                        requireContext(),
                        "${otherUser.name}님이 차단 해제되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }


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

    }

    private fun getStudentChat() {

        // 일단 원인은 찾아냈다. -> 채널이 이상하게 꼬인 것 같음.
        val studentUid = arguments?.getString("studentUid", null)
        val name = arguments?.getString("name", "Chatting Room")
        if (studentUid != null) {
            val channelClient = chatClient.channel(channelType = "messaging", channelId = "")
            channelClient?.create(
                //6
                memberIds = listOf(studentUid, chatClient.getCurrentUser()!!.id),
//                extraData = emptyMap()
                extraData = mapOf("name" to name.toString())
            )?.enqueue { result ->
                if (result.isSuccess) {
                    val newChannel = result.getOrThrow()
//                    startActivity(ChannelActivity.newIntent(requireContext(), newChannel))
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
//            Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ChatMute", "Failed to mute: ${result}")
            }
        }

    }

    private fun unmuteChat(channelId: String) {

        chatClient.unmuteChannel("messaging", channelId).enqueue { result ->
            if (result.isSuccess) {
//                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
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



