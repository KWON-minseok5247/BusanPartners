package com.kwonminseok.busanpartners.ui.message.messageTemp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
//import com.kwonminseok.busanpartners.databinding.FragmentMessageBinding
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.ui.feature.channels.ChannelListFragment
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

@AndroidEntryPoint
class MessageFragmentForCompose : ChannelListFragment() {

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
            //todo 추후 dialog 사이즈 늘리기
            val isMuted =
                chatClient.getCurrentUser()?.channelMutes?.any { it.channel.id == channel.id }
                    ?: false

            val options = if (isMuted) {
                arrayOf("채팅방 알림 켜기", "채팅방 나가기")
            } else {
                arrayOf("채팅방 알림 끄기", "채팅방 나가기")
            }
//                val cid = "$channelType:$channelId"
            AlertDialog.Builder(requireContext())
//                .setTitle("Channel Options")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> if (isMuted) unmuteChat(channel.id) else muteChat(channel.id)
//                        1 -> if (canDelete) chatClient.channel("${channel.type}:${channel.id}").delete().enqueue()
                        1 -> deleteChattingRoom(channel)
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
                    chatClient.channel("${channel.type}:${channel.id}").removeMembers(listOf(userId)).enqueue { result ->
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
//            startActivity(ChannelActivity.newIntent(requireContext(), channel))
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
        if (studentUid != null) {
            val channelClient = chatClient.channel(channelType = "messaging", channelId = "")
            channelClient?.create(
                //6
                memberIds = listOf(studentUid, chatClient.getCurrentUser()!!.id),
                extraData = emptyMap()
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



