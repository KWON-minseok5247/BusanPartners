package com.kwonminseok.busanpartners.ui.message

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.kwonminseok.busanpartners.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.FragmentMessageBinding
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
    private var client: ChatClient? = BusanPartners.chatClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기는 대학생 목록에서 원하는 대학생과 메세지를 보내는 과정
        getStudentChat()

        // ViewModel 바인딩과 UI 업데이트
        getChatList()


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
        }

        binding.channelListView.setIsMoreOptionsVisible { channel ->
            // You can determine visibility based on the channel object.
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_setting)

            true
        }

        binding.channelListView.setIsDeleteOptionVisible { channel ->
            // You can determine visibility based on the channel object.
            // Here is the default implementation:
            channel.ownCapabilities.contains("delete-channel")
        }
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
                } else {
                    Log.e(
                        "Channel Creation Failed",
                        result.toString() ?: "Error creating channel"
                    )
                }
            }
        }
    }

}
