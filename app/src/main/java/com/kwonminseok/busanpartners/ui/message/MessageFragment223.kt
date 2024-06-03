//package com.kwonminseok.busanpartners.ui.message
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import com.google.firebase.functions.FirebaseFunctions
//import com.kwonminseok.busanpartners.BuildConfig
//import com.kwonminseok.busanpartners.R
//import com.kwonminseok.busanpartners.application.BusanPartners
//import com.kwonminseok.busanpartners.repository.TimeRepository
//import com.kwonminseok.busanpartners.util.Constants
//import dagger.hilt.android.AndroidEntryPoint
//import io.getstream.chat.android.client.ChatClient
//import io.getstream.chat.android.client.token.TokenProvider
//import io.getstream.chat.android.models.Channel
//import io.getstream.chat.android.models.Message
//import io.getstream.chat.android.models.User
//import io.getstream.chat.android.ui.feature.channels.ChannelListFragment
//import kotlinx.coroutines.launch
//import org.threeten.bp.OffsetDateTime
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//import kotlin.coroutines.suspendCoroutine
//
//@AndroidEntryPoint
//class MessageFragment223 : ChannelListFragment() {
//    // TODO 20240531 일단 제대로 작동이 된 줄 알았는데 connectUser가 적용되지 않았던 문제 발생..
//    // 일단 지켜보자
//
//
//    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
//    lateinit var user: com.kwonminseok.busanpartners.data.User
//    // getStream 채팅 토큰
//    // 토큰 절차 1: 일단 token이 있는지 없는지 확인, 있으면 바로 가져온다.
//    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
//    // 초반에 userEntity를 고정을 시켜서 언제든지 불러올 수있도록 여기서 정한다.
//
//    private val uid = BusanPartners.preferences.getString("uid", "")
//
////    private val viewModel: ChatInfoViewModel by viewModels()
////    private var _binding: StreamUiFragmentChannelListBinding? = null
////    private val binding get() = _messageBinding!!
//
////    private var _binding: StreamUiFragmentChannelListBinding? = null
////    protected override val binding: StreamUiFragmentChannelListBinding get() = _binding!!
//
////    override fun onCreateView(
////        inflater: LayoutInflater,
////        container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////        _messageBinding = StreamUiFragmentChannelListBinding.inflate(layoutInflater)
////        return messageBinding.root
////    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        if (BusanPartners.chatClient.getCurrentUser() == null) {
//            Log.e(TAG, "setupUserStream이 실행되었습니다.")
//            Toast.makeText(requireContext(), "setupUserStream이 실행되었습니다.", Toast.LENGTH_SHORT).show()
//            setupUserStream()
//        }
//
//        // 여기는 대학생 목록에서 원하는 대학생과 메세지를 보내는 과정
//        getStudentChat()
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
//
//        // ViewModel 바인딩과 UI 업데이트
////        getChatList()
//
//
//    }
//
//    //    override fun onDestroyView() {
////        super.onDestroyView()
////        _messageBinding = null
////    }
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
//    private fun checkIfUserIsBlocked(channel: Channel, userId: String): Boolean {
//        val member = channel.members.find { it.user.id == userId }
//        return member?.user?.banned ?: false
//    }
//
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
////    private fun subscribeForChannelMutesUpdatedEvents() {
////        chatClient.subscribeFor<NotificationChannelMutesUpdatedEvent>(viewLifecycleOwner) {
////            viewModel.onAction(ChatInfoViewModel.Action.ChannelMutesUpdated(it.me.channelMutes))
////        }
//////        ChatClient.instance().subscribeFor<NotificationChannelMutesUpdatedEvent>(viewLifecycleOwner) {
//////            viewModel.onAction(ChatInfoViewModel.Action.ChannelMutesUpdated(it.me.channelMutes))
//////        }
////    }
//
//
//
//    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
////        currentServerTime = TimeRepository.currentTime?.datetime
//        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
//        Log.e("currentServer", currentServerTimeToDateTime.toString())
//        Log.e("user.tokenTime", user.tokenTime.toString())
//        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)
//        Log.e("tokenTimeToDateTime", tokenTimeToDateTime.toString())
//
//        // 토큰 기간. 정상적으로 채팅이 가능한 시기
//        if (currentServerTimeToDateTime != null) {
//            if (currentServerTimeToDateTime <= tokenTimeToDateTime) {
//                // 채팅이 사라지는 이유로 의심할 수 있겠다.  unreadCount 등 추가를 하지 않았다면 0으로 인식을 할 거니까.
//
//                val myUser = User.Builder()
//                    .withId(user.uid)
//                    .withName(user.name?.ko ?: "Guest")
//                    .withImage(user.imagePath)
//                    .build()
//                Log.e("myUser", myUser.toString())
//
////                val myUser = User(
////                    id = user.uid,
////                    name = user.name!!,
////                    image = user.imagePath,
////                )
//
//                if (token == "") {
//                    Log.e(TAG, "token이 비어있을 때.")
//                    lifecycleScope.launch {
//                        getNewToken()
//                        connectClient(myUser)
//                    }
//                } else {
//                    connectClient(myUser)
//
//                }
//
//            } else { // 인증이 되지 않았거나 토큰이 만료가 된 경우 게스트 모드로 로그인 해두기
//
//                val guestUser = User(
//                    id = "guestID",
//                    name = "guestID",
//                    image = "https://bit.ly/2TIt8NR"
//                )
//
//                BusanPartners.chatClient.let { chatClient ->
//                    chatClient.connectUser(
//                        guestUser,
//                        BuildConfig.GUEST_ID_TOKEN
//                    ).enqueue { result ->
//                        Log.e("guestUser", "접속 완료")
//
//
//                    }
//                }
//            }
//        }
//    }
//
//    private fun connectClient(myUser: User) {
////        parseNotificationData()
//
//        val tokenProvider = object : TokenProvider {
//            // Make a request to your backend to generate a valid token for the user
//            override fun loadToken(): String =
//                BusanPartners.preferences.getString(Constants.TOKEN, "")
//        }
//        BusanPartners.chatClient.let { chatClient ->
//            chatClient.connectUser(
//                user = myUser,
//                tokenProvider
//            ).enqueue { result ->
//
//
//                // 비동기 작업 결과 처리
//                if (result.isSuccess) {
//                    val user = result.getOrNull()?.user
//                    Log.e("user?.totalUnreadCount", user?.totalUnreadCount.toString())
//
//                }
//            }
//        }
//
//
//    }
//
//    private fun setupUserStream() {
//        lifecycleScope.launch {
//            // 서버 시간 먼저 가져오기
//            TimeRepository.fetchCurrentTime()
//            currentServerTime = TimeRepository.currentTime?.datetime
//
//            BusanPartners.preferences.setString("uid", user.uid)
//            connectUserToStream(user)
//        }
//    }
//
//
//    private suspend fun getNewToken(): String = suspendCoroutine { continuation ->
//        val functions = FirebaseFunctions.getInstance("asia-northeast3")
//        functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
//            .call()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // 함수 호출이 성공했습니다. 반환된 데이터에서 토큰을 추출합니다.
//                    val token = task.result?.data as String
//                    BusanPartners.preferences.setString(Constants.TOKEN, token)
//                    continuation.resume(token) // 코루틴을 재개하고 결과를 반환합니다.
//                } else {
//                    // 호출 실패. 에러를 처리합니다.
////                    Log.e(TAG, "토큰 호출을 실패했습니다.")
////                    continuation.resumeWithException(
////                        task.exception ?: RuntimeException("Unknown token fetch error")
////                    )
//                    val exception = task.exception ?: RuntimeException("Unknown token fetch error")
//                    Log.e(TAG, "토큰 호출을 실패했습니다.", exception)
//                    continuation.resumeWithException(exception)
//
//                }
//            }
//    }
//
//}