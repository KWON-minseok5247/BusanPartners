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
import androidx.lifecycle.lifecycleScope
import com.google.firebase.functions.FirebaseFunctions
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
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
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private val TAG = "MessageFragment"

//@AndroidEntryPoint
//class MessageFragment : ChannelListFragment() {
//    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
//    private val uid = BusanPartners.preferences.getString("uid", "")
//    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
//    lateinit var user: com.kwonminseok.busanpartners.data.User
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        if (chatClient.getCurrentUser() == null) {
//            Log.e(TAG, "setupUserStream이 실행되었습니다.")
//            Toast.makeText(requireContext(), "setupUserStream이 실행되었습니다.", Toast.LENGTH_SHORT).show()
//            setupUserStream()
//
//        } else {
//            getStudentChat()
//            setupChannelListViewModel()
//        }
//    }
//
//    private fun setupChannelListViewModel() {
////        val factory = ChannelListViewModelFactory(
////            requireContext(),
////            Filters.and(Filters.eq("type", "messaging"), Filters.in_("members", listOf(uid))),
////            ChannelListViewModel.DEFAULT_SORT,
////            null,
////            false
////        )
////        val viewModel: ChannelListViewModel by viewModels { factory }
////
////        viewModel.bindView(binding.channelListView, viewLifecycleOwner)
////
//        // Other viewModel setup code
//
//        binding.channelListView.setIsMoreOptionsVisible { channel ->
//            // You can determine visibility based on the channel object.
//            ContextCompat.getDrawable(requireContext(), R.drawable.pusan_logo)
//
//            false
//        }
//
//        binding.channelListHeaderView.setOnActionButtonClickListener {
//            // 클릭 이벤트를 비어 있는 블록으로 처리하여 아무 작업도 수행하지 않음
//
//        }
//
//        binding.channelListView.setChannelItemClickListener { channel ->
//            if (channel.members.size == 1) {
//                Toast.makeText(requireContext(), "상대방이 채팅방을 떠났습니다.", Toast.LENGTH_SHORT).show()
//            }
//            startActivity(ChannelActivity.newIntent(requireContext(), channel))
//
//        }
//
//        binding.channelListView.setIsDeleteOptionVisible { channel ->
//            // You can determine visibility based on the channel object.
//            // Here is the default implementation:
//            channel.ownCapabilities.contains("delete-channel")
//            false
//        }
//
////                binding.channelListView.setChannelItemClickListener { channel ->
////            startActivity(ChannelActivity.createIntent(requireContext(), channel.cid))
//////                    val intent = Intent(requireContext(), ChannelActivity::class.java)
//////                    intent.putExtra("channelId", channel.id) // 채널 ID를 ChannelActivity로 전달
//////                    startActivity(intent)
////
//////            startActivity(ChannelActivity.getIntent(requireContext(), channel.cid))
////
////        }
//
////        binding.channelListView.setChannelItemClickListener { channel ->
////            val intent = Intent(requireContext(), ChannelActivity::class.java)
////            intent.putExtra("channelId", channel.cid) // 채널 ID를 ChannelActivity로 전달
////            startActivity(intent)
////        }
//
//        binding.channelListView.setChannelLongClickListener { channel ->
//            if (channel.id == "ExampleChat") { // 예시 채팅방을 나갈 수 없도록 함.
//                true
//            } else {
//                val isMuted =
//                    BusanPartners.chatClient.getCurrentUser()?.channelMutes?.any { it.channel.id == channel.id }
//                        ?: false
//                // 차단 상태 확인 (임시로 false로 설정, 실제 로직 필요)
////            val isBlocked = checkIfUserIsBlocked(otherUser?.id, channel.cid)
////            val isBlocked = checkIfUserIsBlocked(channel, chatClient.getCurrentUser()!!.id)
//                val isBlocked = false
//
//                val options = when {
//                    isMuted && isBlocked -> arrayOf("채팅방 알림 켜기", "사용자 차단 해제", "채팅방 나가기")
//                    !isMuted && isBlocked -> arrayOf("채팅방 알림 끄기", "사용자 차단 해제", "채팅방 나가기")
//                    isMuted && !isBlocked -> arrayOf("채팅방 알림 켜기", "사용자 차단", "채팅방 나가기")
//                    else -> arrayOf("채팅방 알림 끄기", "사용자 차단", "채팅방 나가기")
//                }
//
////                val cid = "$channelType:$channelId"
//                AlertDialog.Builder(requireContext())
////                .setTitle("Channel Options")
//                    .setItems(options) { dialog, which ->
//                        when (which) {
//                            0 -> if (isMuted) unmuteChat(channel.id) else muteChat(channel.id)
////                        1 -> if (canDelete) chatClient.channel("${channel.type}:${channel.id}").delete().enqueue()
//                            1 -> if (isBlocked) unBlockUser(channel) else blockUser(channel)
//                            2 -> deleteChattingRoom(channel)
////                        1 -> if (canDelete) chatClient.channel("${channel.type}:${channel.id}").removeMembers(chatClient.getCurrentUser()?.id ?: "").enqueue()
//
//                        }
//                    }
//                    .show()
//                true
//            }
//            //todo 추후 dialog 사이즈 늘리기
//
//        }
//    }
//
//    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
//        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
//        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)
//
//        if (currentServerTimeToDateTime != null && currentServerTimeToDateTime <= tokenTimeToDateTime) {
//            val myUser = User.Builder()
//                .withId(user.uid)
//                .withName(user.name?.ko ?: "Guest")
//                .withImage(user.imagePath)
//                .build()
//
//            if (token == "") {
//                lifecycleScope.launch {
//                    getNewToken()
//                    connectClient(myUser)
//                }
//            } else {
//                connectClient(myUser)
//            }
//        } else {
//            val guestUser = User(id = "guestID", name = "guestID", image = "https://bit.ly/2TIt8NR")
//            chatClient.connectUser(guestUser, BuildConfig.GUEST_ID_TOKEN).enqueue { result ->
//                if (result.isSuccess) {
//                    setupChannelListViewModel()
//                }
//            }
//        }
//    }
//
//    private fun connectClient(myUser: User) {
//        val tokenProvider = object : TokenProvider {
//            override fun loadToken(): String = BusanPartners.preferences.getString(Constants.TOKEN, "")
//        }
//
//        chatClient.connectUser(myUser, tokenProvider).enqueue { result ->
//            if (result.isSuccess) {
//                setupChannelListViewModel()
//            }
//        }
//    }
//
//    private fun setupUserStream() {
//        lifecycleScope.launch {
//            TimeRepository.fetchCurrentTime()
//            currentServerTime = TimeRepository.currentTime?.datetime
//            BusanPartners.preferences.setString("uid", user.uid)
//            connectUserToStream(user)
//        }
//    }
//
//    private suspend fun getNewToken(): String = suspendCoroutine { continuation ->
//        val functions = FirebaseFunctions.getInstance("asia-northeast3")
//        functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
//            .call()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val token = task.result?.data as String
//                    BusanPartners.preferences.setString(Constants.TOKEN, token)
//                    continuation.resume(token)
//                } else {
//                    val exception = task.exception ?: RuntimeException("Unknown token fetch error")
//                    continuation.resumeWithException(exception)
//                }
//            }
//    }
//
//    private fun getStudentChat() {
//
//        // 일단 원인은 찾아냈다. -> 채널이 이상하게 꼬인 것 같음.
//        val studentUid = arguments?.getString("studentUid", null)
//        val name = arguments?.getString("name", "Chatting Room")
//        if (studentUid != null) {
//            val channelClient = BusanPartners.chatClient.channel(channelType = "messaging", channelId = "")
//            channelClient.create(
//                //6
//                memberIds = listOf(studentUid, BusanPartners.chatClient.getCurrentUser()!!.id),
////                extraData = emptyMap()
//                extraData = mapOf("name" to name.toString())
//            )?.enqueue { result ->
//                if (result.isSuccess) {
//                    val newChannel = result.getOrThrow()
//                    startActivity(ChannelActivity.newIntent(requireContext(), newChannel))
////                    startActivity(ChannelActivity.getIntent(requireContext(), newChannel.id))
//
//                } else {
//                    Log.e(
//                        "Channel Creation Failed",
//                        result.toString() ?: "Error creating channel"
//                    )
//                }
//            }
//        }
//    }
//
//    private fun muteChat(channelId: String) {
//
//
//        BusanPartners.chatClient.muteChannel("messaging", channelId).enqueue { result ->
//            if (result.isSuccess) {
////            Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
//            } else {
//                Log.e("ChatMute", "Failed to mute: ${result}")
//            }
//        }
//
//    }
//
//    private fun unmuteChat(channelId: String) {
//
//        BusanPartners.chatClient.unmuteChannel("messaging", channelId).enqueue { result ->
//            if (result.isSuccess) {
////                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
//            } else {
//                Log.e("ChatMute", "Failed to mute: ${result}")
//            }
//        }
//
//    }
//
//    @SuppressLint("CheckResult")
//    private fun blockUser(channel: Channel) {
//
//
//        Log.e("blockUser", "blockUser가 실행되었습니다.")
//        val currentUserId = ChatClient.instance().getCurrentUser()?.id
//        val otherUser = channel.members.firstOrNull { it.user.id != currentUserId }?.user
//        Log.e("otherUser", otherUser.toString())
//
//        if (otherUser != null) {
//            BusanPartners.chatClient.channel("messaging", channel.id).shadowBanUser(otherUser.id,
//                "유저의 선택으로 밴되었습니다", null)
//                .enqueue { result ->
//                    if (result.isSuccess) {
//                        // 차단 성공
//                        Log.e("BanUser", "User $otherUser.id banned successfully")
//                    } else {
//                        // 차단 실패
//                        Log.e("BanUser", "${result.errorOrNull().toString()}")
//                    }
//                }
//
//
////            chatClient.banUser(otherUser.id, "messaging", channel.id, "유저의 선택으로 밴되었습니다.", null)
////                .enqueue {
////                    Toast.makeText(
////                        requireContext(),
////                        "${otherUser.name}님이 차단되었습니다.",
////                        Toast.LENGTH_SHORT
////                    ).show()
////                }
//        }
//
//    }
//
//    private fun unBlockUser(channel: Channel) {
//        Log.e("unBlockUser", "unBlockUser 실행되었습니다.")
//
//        val currentUserId = ChatClient.instance().getCurrentUser()?.id
//        val otherUser = channel.members.firstOrNull { it.user.id != currentUserId }?.user
//        Log.e("otherUser", otherUser.toString())
//        if (otherUser != null) {
//            BusanPartners.chatClient.unbanUser(otherUser.id, "messaging", channel.id)
//                .enqueue {
//                    Toast.makeText(
//                        requireContext(),
//                        "${otherUser.name}님이 차단 해제되었습니다.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//        }
//
//    }
//
//    private fun deleteChattingRoom(channel: Channel) {
//        val userId = BusanPartners.chatClient.getCurrentUser()?.id
//        AlertDialog.Builder(requireContext())// 여기서 눈물흘리는 벡터 하나 만들어주고
//            .setTitle("채팅방 나가기")
//            .setMessage("정말로 채팅방을 나가시겠습니까? 상대방과 다시는 대화할 수 없습니다.")
//            .setPositiveButton("예") { dialog, which ->
//                if (userId != null) {
//
//                    BusanPartners.chatClient.channel("${channel.type}:${channel.id}").hide(true).enqueue { hideResult ->
//                        if (hideResult.isSuccess) {
//                            // 채널 숨기기에 성공한 후 멤버를 제거합니다.
//                            BusanPartners.chatClient.channel("${channel.type}:${channel.id}").removeMembers(
//                                listOf(userId),
//                                Message(text = "The other person left the chat room.")
//
//                            ).enqueue { result ->
//                                if (result.isSuccess) {
//                                    Log.e(
//                                        "채널 삭제시",
//                                        "${channel.memberCount} ${channel.members.size}"
//                                    )
//                                    if (channel.members.size == 1) {
//                                        BusanPartners.chatClient.channel("${channel.type}:${channel.id}").delete().enqueue() { result ->
//                                            if (result.isSuccess) {
//                                                Log.e("사용자가 채널을 성공적으로 삭제시켰습니다..", "확인")
//                                            } else {
//                                                Log.e(
//                                                    "사용자가 채널을 삭제시키는데 실패했습니다.",
//                                                    result.errorOrNull().toString()
//                                                )
//
//                                            }
//                                        }
//                                    }
//                                    Log.e("사용자가 채널을 성공적으로 나갔습니다.", "확인")
//                                } else {
//                                    Log.e(
//                                        "사용자가 채널을 떠나지 못했습니다.",
//                                        result.errorOrNull()?.message ?: "알 수 없는 오류"
//                                    )
//                                }
//                            }
//                        } else {
//                            Log.e("채널 숨기기 실패", hideResult.errorOrNull()?.message ?: "알 수 없는 오류")
//                        }
//                    }
////                    chatClient.channel("${channel.type}:${channel.id}")
////                        .removeMembers(listOf(userId)).enqueue { result ->
////                        if (result.isSuccess) {
////                            Log.e("사용자가 채널을 성공적으로 나갔습니다.", "확인")
////
////                            chatClient.channel("${channel.type}:${channel.id}")
////                                .hide(true).enqueue { hideResult ->
////                                    if (hideResult.isSuccess) {
////                                        Log.e("채널이 성공적으로 숨겨졌습니다.", "확인")
////                                    } else {
////                                        Log.e("채널 숨기기 실패", hideResult.errorOrNull()?.message ?: "알 수 없는 오류")
////                                    }
////                                }
////
////                        } else {
////                            Log.e("사용자가 채널.", "실패")
////
////                        }
////                    }
//                }
//
//                BusanPartners.chatClient.channel("${channel.type}:${channel.id}").hide(true)
//
//            }
//            .setNegativeButton("아니오") { dialog, which ->
//                // 아무 작업 없이 다이얼로그 닫기
//                Log.e("정상적으로", "아니오.")
//
//            }
//            .show()
//
//    }
//
//}

//private val TAG = "MessageFragment"
@AndroidEntryPoint
class MessageFragment : ChannelListFragment() {

    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
    private val uid = BusanPartners.preferences.getString("uid", "")
    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
    lateinit var user: com.kwonminseok.busanpartners.data.User
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

        if (chatClient.getCurrentUser() == null) {

            Log.e(TAG, chatClient.toString())
            Toast.makeText(requireContext(), "setupUserStream이 실행되었습니다.", Toast.LENGTH_SHORT).show()
            setupUserStream()

        } else {
            getStudentChat()
            setupChannelListViewModel()
        }
    }

    private fun setupChannelListViewModel() {
        viewModel.bindView(binding.channelListView, viewLifecycleOwner)
        // Other viewModel setup code

        binding.channelListView.setIsMoreOptionsVisible { channel ->
            // You can determine visibility based on the channel object.
            ContextCompat.getDrawable(requireContext(), R.drawable.pusan_logo)
            false
        }


        binding.channelListHeaderView.setOnActionButtonClickListener {
            // 클릭 이벤트를 비어 있는 블록으로 처리하여 아무 작업도 수행하지 않음
        }

        binding.channelListView.setChannelItemClickListener { channel ->
            if (channel.members.size <= 1) {
                Toast.makeText(requireContext(), "상대방이 채팅방을 떠났습니다.", Toast.LENGTH_SHORT).show()
            }
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
            if (channel.id == "ExampleChat") {
                true
            } else {
                Log.e("channel", channel.members.toString())
                val isMuted =
                    chatClient.getCurrentUser()?.channelMutes?.any { it.channel.id == channel.id }
                        ?: false
                val options = when {
                    isMuted -> arrayOf("채팅방 알림 켜기", "채팅방 나가기")
                    !isMuted -> arrayOf("채팅방 알림 끄기", "채팅방 나가기")
                    else -> arrayOf("채팅방 알림 끄기", "채팅방 나가기")
                }
                AlertDialog.Builder(requireContext())
                    .setItems(options) { dialog, which ->
                        when (which) {
                            0 -> if (isMuted) unmuteChat(channel.id) else muteChat(channel.id)
                            1 -> deleteChattingRoom(channel)
//                            1 -> if (isBlocked) unBlockUser(channel) else blockUser(channel)

                        }
                    }
                    .show()
                true
            }
        }
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
                    // 보통은 한 번에 여러명 추가하기도 하니까..
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

//    @SuppressLint("CheckResult")
//    private fun blockUser(channel: Channel) {
//        val currentUserId = chatClient.getCurrentUser()?.id
//        val otherUser = channel.members.firstOrNull { it.user.id != currentUserId }?.user
//
//        if (otherUser != null) {
//            chatClient.channel("messaging", channel.id).shadowBanUser(otherUser.id,
//                "유저의 선택으로 밴되었습니다", null).enqueue { result ->
//                if (!result.isSuccess) {
//                    Log.e("BanUser", "${result.errorOrNull().toString()}")
//                }
//            }
//        }
//    }
//
//    private fun unBlockUser(channel: Channel) {
//        val currentUserId = chatClient.getCurrentUser()?.id
//        val otherUser = channel.members.firstOrNull { it.user.id != currentUserId }?.user
//        if (otherUser != null) {
//            chatClient.unbanUser(otherUser.id, "messaging", channel.id).enqueue {
//                Toast.makeText(requireContext(), "${otherUser.name}님이 차단 해제되었습니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun deleteChattingRoom(channel: Channel) {
        val userId = chatClient.getCurrentUser()?.id
        AlertDialog.Builder(requireContext())
            .setTitle("채팅방 나가기")
            .setMessage("정말로 채팅방을 나가시겠습니까? 상대방과 다시는 대화할 수 없습니다.")
            .setPositiveButton("예") { dialog, which ->

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
                                                val count = currentUser?.chatChannelCount?.minus(1) ?: 0

                                                currentUser = currentUser?.copy(chatChannelCount = count)

                                                userViewModel.updateUser(currentUser!!)


                                                userViewModel.setCurrentUser(mapOf("chatChannelCount" to count))

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
                                    val count = currentUser?.chatChannelCount?.minus(1) ?: 0

                                    currentUser = currentUser?.copy(chatChannelCount = count)

                                    userViewModel.updateUser(currentUser!!)


                                    userViewModel.setCurrentUser(mapOf("chatChannelCount" to count))
                                }

                            }


                    }
                }


                // 그러면 채팅을 시작할 때 +1 하고 만약 int가 3이 되면 더이상 추가할 수 없도록 설정
                // 그리고 삭제하는 과정에서는 -1 처리를 통해서 최대 채팅방 개수 조정 그리고 대화를 시작하자마자 blockLIst 추가
                // 대신 연락하기에서 선택할 수 없도록 하기? 분명히 대화하다가 연락하기 다시 누를 수 있다고 생각
            }
            .setNegativeButton("아니오") { dialog, which ->
                Log.e("정상적으로", "아니오.")
            }
            .show()
    }
}
