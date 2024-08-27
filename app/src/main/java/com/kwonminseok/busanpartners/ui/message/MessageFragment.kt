package com.kwonminseok.busanpartners.ui.message

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alertview.lib.AlertView
import com.alertview.lib.OnItemClickListener
import com.google.firebase.functions.FirebaseFunctions
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.extensions.setStatusBarTransparent
import com.kwonminseok.busanpartners.extensions.setStatusBarVisible
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.repository.TimeRepository
//import com.kwonminseok.busanpartners.databinding.FragmentMessageBinding
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.ui.login.SplashActivity.Companion.currentUser
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ChatInfoViewModel
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.events.MarkAllReadEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.events.NotificationChannelMutesUpdatedEvent
import io.getstream.chat.android.client.events.NotificationMarkReadEvent
import io.getstream.chat.android.client.events.NotificationMessageNewEvent
import io.getstream.chat.android.client.subscribeFor
import io.getstream.chat.android.client.token.TokenProvider
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.ChannelCapabilities
import io.getstream.chat.android.models.ChannelMute
import io.getstream.chat.android.models.FilterObject
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.ui.databinding.StreamUiFragmentChannelListBinding
import io.getstream.chat.android.ui.databinding.StreamUiFragmentMessageListBinding
import io.getstream.chat.android.ui.feature.channels.ChannelListActivity
import io.getstream.chat.android.ui.feature.channels.ChannelListFragment
import io.getstream.chat.android.ui.feature.channels.header.ChannelListHeaderView
import io.getstream.chat.android.ui.feature.channels.list.ChannelListView
import io.getstream.chat.android.ui.feature.messages.MessageListFragment
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView
import io.getstream.chat.android.ui.viewmodel.search.SearchViewModel
import io.getstream.result.call.Call
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private val TAG = "MessageFragment"

@AndroidEntryPoint
class MessageFragment : ChannelListFragment() {

    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
    private val uid = BusanPartners.preferences.getString("uid", "")
    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
    lateinit var user: com.kwonminseok.busanpartners.data.User
    private var originalPaddingTop: Int = 0

    private val viewModel: ChannelListViewModel by viewModels {
        ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(uid))
            ),
            sort = QuerySortByField.descByName("last_message_at"),
        )
    }

    private val userViewModel: UserViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_messageFragment_to_homeFragment)
                }
            }
        )


        originalPaddingTop = binding.root.paddingTop
        if (chatClient.getCurrentUser() == null) {

            Log.e(TAG, chatClient.toString())
//            Toast.makeText(requireContext(), "setupUserStream이 실행되었습니다.", Toast.LENGTH_SHORT).show()
            setupUserStream()

        } else {
            getStudentChat()
            setupChannelListViewModel()
        }


    }

    private fun setupChannelListViewModel() {
        viewModel.bindView(binding.channelListView, viewLifecycleOwner)
        // Other viewModel setup code
        val loadingView =
            LayoutInflater.from(context).inflate(R.layout.channel_list_loading_view, null)

        binding.channelListView.setLoadingView(
            loadingView,
            FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        )

        binding.channelListView.setIsMoreOptionsVisible { channel ->
            // You can determine visibility based on the channel object.
            ContextCompat.getDrawable(requireContext(), R.drawable.pusan_logo)
            false
        }


//        binding.channelListHeaderView.setOnActionButtonClickListener {
//            // 클릭 이벤트를 비어 있는 블록으로 처리하여 아무 작업도 수행하지 않음
//        }
//        val headerView = view?.findViewById<ChannelListHeaderView>(R.id.channelListHeaderView)
//        headerView?.visibility = View.GONE

        binding.channelListView.setChannelItemClickListener { channel ->
            if (channel.members.size <= 1) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.user_left_chat),
                    Toast.LENGTH_SHORT
                ).show()
            }
//            startActivity(ChannelActivity.getIntent(requireContext(), channel.cid))

            startActivity(ChannelActivity.newIntent(requireContext(), channel))
//            startActivity(ChannelActivityForCompose.newIntent(requireContext(), channel))
        }

        binding.channelListView.setIsDeleteOptionVisible { channel ->
            // You can determine visibility based on the channel object.
            // Here is the default implementation:

            channel.ownCapabilities.contains("delete-channel")
            false
        }

        binding.channelListView.setChannelLongClickListener { channel ->
            val chatroomIds = listOf(
                "chatroom_es",
                "chatroom_en",
                "chatroom_jp",
                "chatroom_cn",
                "chatroom_tw",
                "chatroom_kr"
            )
            if (channel.id in chatroomIds) {
                true
            } else {
                Log.e("channel", channel.members.toString())
                val isMuted =
                    chatClient.getCurrentUser()?.channelMutes?.any { it.channel.id == channel.id }
                        ?: false
                val options = when {
                    isMuted -> arrayOf(
                        getString(R.string.unmute_chat_notifications),
                        getString(R.string.leave_chat),
                        getString(R.string.block_button_text)
                    )

                    else -> arrayOf(
                        getString(R.string.mute_chat_notifications),
                        getString(R.string.leave_chat),
                        getString(R.string.block_button_text)

                    )
                }
                AlertDialog.Builder(requireContext())
                    .setItems(options) { dialog, which ->
                        when (which) {
                            0 -> if (isMuted) unmuteChat(channel.id) else muteChat(channel.id)
                            1 -> deleteChattingRoom(channel)
                            2 -> banUser(channel)

//                            1 -> if (isBlocked) unBlockUser(channel) else blockUser(channel)

                        }
                    }
                    .show()
                true
            }
        }
    }

    private fun banUser(channel: Channel) {


        AlertView.Builder()
            .setContext(requireActivity())
            .setStyle(AlertView.Style.Alert)
            .setTitle(getString(R.string.block_alert_title))
            .setMessage(getString(R.string.block_alert_message))
            .setDestructive(getString(R.string.confirmation))
            .setOthers(arrayOf(getString(R.string.cancel)))
            .setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(o: Any?, position: Int) {
                    if (position == 0) { // 확인 버튼 위치 확인
                        chatClient.channel("${channel.type}:${channel.id}")
                            .hide(true).enqueue { hideResult ->
                                if (hideResult.isSuccess) {
                                    val studentUid =
                                        channel.members.find { it.getUserId() != chatClient.getCurrentUser()?.id }
                                            ?.getUserId()
                                    if (studentUid != null) {
                                        val banList = currentUser?.banList?.toMutableList() ?: mutableListOf()
                                        if (!banList.contains(studentUid)) {
                                            banList.add(studentUid)
                                        }

                                        currentUser = currentUser?.copy(blockList = banList)
                                        userViewModel.updateUser(currentUser!!)

                                        userViewModel.setCurrentUser(
                                            mapOf(
                                                "banList" to banList
                                            )
                                        )

                                        Toast.makeText(requireContext(), getString(R.string.block_success), Toast.LENGTH_SHORT).show()

                                    } else {
                                        Log.e("BanUser", "No valid user found to ban.")
                                    }


                                } else {
                                    Log.e(
                                        "채널 숨기기 실패",
                                        hideResult.errorOrNull()?.message ?: "알 수 없는 오류"
                                    )
                                }
                            }
                    } else {
                        (o as AlertView).dismiss() // 다른 버튼 클릭 시 AlertView 닫기
                    }
                }

            })
            .build()
            .setCancelable(true)
            .show()






    }

    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)

        if (currentServerTimeToDateTime != null && currentServerTimeToDateTime <= tokenTimeToDateTime) {
            val myUser = User.Builder()
                .withId(user.uid)
                .withName(user.name?.ko ?: "Guest")
                .withImage(user.imagePath)
                .build()

            if (token == "") {
                lifecycleScope.launch {
                    getNewToken()
                    connectClient(myUser)
                }
            } else {
                connectClient(myUser)
            }
        } else {
            val guestUser = User(id = "guestID", name = "guestID", image = "https://bit.ly/2TIt8NR")
            chatClient.connectUser(guestUser, BuildConfig.GUEST_ID_TOKEN).enqueue { result ->
                if (result.isSuccess) {
                    setupChannelListViewModel()
                }
            }
        }
    }

    private fun connectClient(myUser: User) {
        val tokenProvider = object : TokenProvider {
            override fun loadToken(): String =
                BusanPartners.preferences.getString(Constants.TOKEN, "")
        }

        chatClient.connectUser(myUser, tokenProvider).enqueue { result ->
            if (result.isSuccess) {
                setupChannelListViewModel()
            }
        }
    }

    private fun setupUserStream() {
        lifecycleScope.launch {
            TimeRepository.fetchCurrentTime()
            currentServerTime = TimeRepository.currentTime?.datetime
            BusanPartners.preferences.setString("uid", user.uid)
            connectUserToStream(user)
        }
    }

    private suspend fun getNewToken(): String = suspendCoroutine { continuation ->
        val functions = FirebaseFunctions.getInstance("asia-northeast3")
        functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
            .call()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.data as String
                    BusanPartners.preferences.setString(Constants.TOKEN, token)
                    continuation.resume(token)
                } else {
                    val exception = task.exception ?: RuntimeException("Unknown token fetch error")
                    continuation.resumeWithException(exception)
                }
            }
    }

    private fun getStudentChat() {
        val studentUid = arguments?.getString("studentUid", null)
        val name = arguments?.getString("name", "Chatting Room")
        if (studentUid != null) {
            val channelClient = chatClient.channel(channelType = "messaging", channelId = "")
            channelClient.create(
                memberIds = listOf(studentUid, chatClient.getCurrentUser()!!.id),
                extraData = mapOf("name" to name.toString())
            ).enqueue { result ->
                if (result.isSuccess) {
                    //TODO 채팅방은 동시에 3개까지 활성화할 수 있습니다.

                    val blockList = currentUser?.blockList?.toMutableList() ?: mutableListOf()
                    if (!blockList.contains(studentUid)) {
                        blockList.add(studentUid)
                    }
                    val count = currentUser?.chatChannelCount?.plus(1) ?: 1

                    currentUser = currentUser?.copy(blockList = blockList, chatChannelCount = count)

                    userViewModel.updateUser(currentUser!!)


                    userViewModel.setCurrentUser(
                        mapOf(
                            "blockList" to blockList,
                            "chatChannelCount" to count
                        )
                    )

                } else {
                    Log.e("Channel Creation Failed", result.toString() ?: "Error creating channel")
                }
            }
        }
    }


    private fun muteChat(channelId: String) {
        chatClient.muteChannel("messaging", channelId).enqueue { result ->
            if (!result.isSuccess) {
                Log.e("ChatMute", "Failed to mute: ${result}")
            }
        }
    }

    private fun unmuteChat(channelId: String) {
        chatClient.unmuteChannel("messaging", channelId).enqueue { result ->
            if (!result.isSuccess) {
                Log.e("ChatMute", "Failed to mute: ${result}")
            }
        }
    }


    private fun deleteChattingRoom(channel: Channel) {
        val userId = chatClient.getCurrentUser()?.id

        AlertView.Builder()
            .setContext(requireActivity())
            .setStyle(AlertView.Style.Alert)
            .setTitle(getString(R.string.leave_chat_room_title))
            .setMessage(getString(R.string.leave_chat_room_message))
            .setDestructive(getString(R.string.confirmation))
            .setOthers(arrayOf(getString(R.string.cancel)))
            .setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(o: Any?, position: Int) {
                    if (position == 0) { // 확인 버튼 위치 확인
                        if (channel.members.size <= 1) { // 멤버가 1명일 때
                            chatClient.channel("${channel.type}:${channel.id}")
                                .delete().enqueue() { deleteResult ->
                                    if (!deleteResult.isSuccess) {
                                        Log.e(
                                            "사용자가 채널을 삭제시키는데 실패했습니다.",
                                            deleteResult.errorOrNull().toString()
                                        )
                                        if (userId != null) {
                                            chatClient.channel("${channel.type}:${channel.id}")
                                                .removeMembers(
                                                    listOf(userId),
//                                        Message(text = "The other person left the chat room.")
                                                )
                                                .enqueue { hideResult ->
                                                    if (hideResult.isSuccess) {
                                                        Log.e("Chat", "성공적으로 채팅방에서 나갔습니다.")

                                                    }

                                                }


                                        }
                                    } else {
                                        Log.e("Chat", "채널 삭제에 성공했습니다.")

                                    }

                                }
                        } else { // 멤버가 2명인 경우
                            if (userId != null) { //TODO 여기도 번역이 되어 있어야 함.
                                chatClient.channel("${channel.type}:${channel.id}").removeMembers(
                                    listOf(userId),
                                    Message(text = "The other person left the chat room.")
                                )
                                    .enqueue { hideResult ->
                                        if (hideResult.isSuccess) {
                                            Log.e("Chat", "성공적으로 채팅방에서 나갔습니다.")
                                        }

                                    }


                            }
                        }
                    } else {
                        (o as AlertView).dismiss() // 다른 버튼 클릭 시 AlertView 닫기
                    }
                }

            })
            .build()
            .setCancelable(true)
            .show()


    }

    override fun onResume() {
        super.onResume()
//        hideBottomNavigationView()
        requireActivity().setStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            view.updatePadding(top = originalPaddingTop + statusBarHeight)
            insets
        }


    }


    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        super.onPause()
//        showBottomNavigationView()
        requireActivity().setStatusBarVisible()
        binding.root.updatePadding(top = originalPaddingTop)

    }

}
