package com.kwonminseok.busanpartners.ui.message

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.databinding.FragmentMessageBinding
import com.kwonminseok.busanpartners.viewmodel.ChatInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.NotificationChannelMutesUpdatedEvent
import io.getstream.chat.android.client.subscribeFor
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.ui.feature.channels.ChannelListFragment
import io.getstream.chat.android.ui.feature.channels.list.ChannelListView
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

@AndroidEntryPoint
class MessageFragment : Fragment()
{
    private val viewModel: ChatInfoViewModel by viewModels()

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private var client: ChatClient? = BusanPartners.chatClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기는 대학생 목록에서 원하는 대학생과 메세지를 보내는 과정
        getStudentChat()

        // ViewModel 바인딩과 UI 업데이트
        getChatList()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getChatList() {
        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()

        val channelListFactory: ChannelListViewModelFactory =
            ChannelListViewModelFactory(
                filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`(
                        "members",
                        listOf(client?.getCurrentUser()!!.id)
                    ),
                ),
                sort = QuerySortByField.descByName("last_updated"),
                limit = 30,

                )
        val channelListViewModel: ChannelListViewModel by viewModels { channelListFactory }


        channelListHeaderViewModel.bindView(binding.channelListHeaderView, this)
        channelListViewModel.bindView(binding.channelListView, this)

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

//        subscribeForChannelMutesUpdatedEvents()


    }

    private fun getStudentChat() {


        val studentUid = arguments?.getString("studentUid", null)
        if (studentUid != null) {
            val channelClient = client?.channel(channelType = "messaging", channelId = "")
            channelClient?.create(
                memberIds = listOf(studentUid, client?.getCurrentUser()!!.id),
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
    private fun subscribeForChannelMutesUpdatedEvents() {
        chatClient.subscribeFor<NotificationChannelMutesUpdatedEvent>(viewLifecycleOwner) {
            viewModel.onAction(ChatInfoViewModel.Action.ChannelMutesUpdated(it.me.channelMutes))
        }
//        ChatClient.instance().subscribeFor<NotificationChannelMutesUpdatedEvent>(viewLifecycleOwner) {
//            viewModel.onAction(ChatInfoViewModel.Action.ChannelMutesUpdated(it.me.channelMutes))
//        }
    }


}
