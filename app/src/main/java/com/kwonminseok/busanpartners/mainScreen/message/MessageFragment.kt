package com.kwonminseok.busanpartners.mainScreen.message

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.functions.FirebaseFunctions
import com.kwonminseok.busanpartners.BuildConfig.GUEST_ID_TOKEN
import com.kwonminseok.busanpartners.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.api.WorldTimeApiService
import com.kwonminseok.busanpartners.api.WorldTimeResponse
import com.kwonminseok.busanpartners.databinding.FragmentMessageBinding
import com.kwonminseok.busanpartners.mainScreen.TAG
import com.kwonminseok.busanpartners.repository.TimeRepository
import com.kwonminseok.busanpartners.util.Constants.TOKEN
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ChatListViewModel
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.token.TokenProvider
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.User
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
//    private val viewModel by viewModels<ChatListViewModel>()
    private val viewModel: UserViewModel by viewModels()

    private var client: ChatClient? = BusanPartners.chatClient
    lateinit var user: com.kwonminseok.busanpartners.data.User

    private val currentServerTime = TimeRepository.currentTime!!.datetime


    // 토큰 절차 1: 일단 token이 있는지 없는지 확인, 있으면 바로 가져온다.
    private var token: String = BusanPartners.preferences.getString(TOKEN, "")


    // Bundle에서 "studentUid" 키로 저장된 데이터 검색


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


//        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")



        // 페이스북 shimmer Ui 구현 로직
        val loadingView =
            LayoutInflater.from(requireContext()).inflate(R.layout.channel_list_loading_view, null)
// Set loading view
        binding.channelListView.setLoadingView(
            loadingView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )


        // 여기서 파이어베이스 정보를 받고 user를 정의한다.
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        user = it.data!!
                        connectUserToStream(user)
                    }

                    is Resource.Error -> {

                    }

                    else -> Unit
                }
            }
        }


    }

    //TODO 유저 네임이 비어있으면 안된다
    // 나중에 실행하기
    // 아래와 같이 코드를 작성하니 뷰모델을 불러오는 도중 프래그먼트를 이동해도 에러가 발생하지 않는다.
    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {

        val currentServerTimeToDateTime = OffsetDateTime.parse(currentServerTime)
        val tokenTimeToDateTime = OffsetDateTime.parse(user.tokenTime)


        // 토큰 기간. 정상적으로 채팅이 가능한 시기
        if (currentServerTimeToDateTime <= tokenTimeToDateTime) {
            val myUser = User(
                id = user.uid,
                name = user.name!!,
                image = user.imagePath
            )

            if (token == "") {
                Log.e(TAG, "token이 비어있을 때.")
                lifecycleScope.launch {
                    getNewToken()
                    // 프래그먼트가 액티비티에 붙어 있는지 확인
                    if (isAdded) {
                        connectClient(myUser)
                    }
                }
            } else {
                // 프래그먼트가 액티비티에 붙어 있는지 확인
                if (isAdded) {
                    connectClient(myUser)
                }
            }

        } else { // 인증이 되지 않았거나 토큰이 만료가 된 경우
            val guestUser = User(
                id = "guestID",
                name = "guestID",
                image = "https://bit.ly/2TIt8NR"
            )


                // 게스트 모드
            client?.let { chatClient ->
                chatClient.connectUser(
                    guestUser,
                    GUEST_ID_TOKEN
                ).enqueue { result ->
                    // 비동기 작업 결과 처리
                    // 프래그먼트의 뷰가 생성된 상태인지 확인


                    if (isAdded && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        if (result.isSuccess) {
                            // ViewModel 바인딩과 UI 업데이트
                            val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()

                            val channelListFactory: ChannelListViewModelFactory =
                                ChannelListViewModelFactory(
                                    filter = Filters.and(
                                        Filters.eq("type", "messaging"),
                                        Filters.`in`(
                                            "members",
                                            listOf(ChatClient.instance().getCurrentUser()!!.id)
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

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    }

    private fun connectClient(myUser: User) {

        val tokenProvider = object : TokenProvider {
            // Make a request to your backend to generate a valid token for the user
            override fun loadToken(): String = BusanPartners.preferences.getString(TOKEN, "")
        }

//
//        client?.let { chatClient ->
//            chatClient.connectUser(
//                myUser,
//                tokenProvider
//            ).enqueue { result ->
//                // 비동기 작업 결과 처리
//                // 프래그먼트의 뷰가 생성된 상태인지 확인
//
//
//                if (isAdded && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
//                    if (result.isSuccess) {
//                        // ViewModel 바인딩과 UI 업데이트
//                        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
//
//                        val channelListFactory: ChannelListViewModelFactory =
//                            ChannelListViewModelFactory(
//                                filter = Filters.and(
//                                    Filters.eq("type", "messaging"),
//                                    Filters.`in`(
//                                        "members",
//                                        listOf(ChatClient.instance().getCurrentUser()!!.id)
//                                    ),
//                                ),
//                                sort = QuerySortByField.descByName("last_updated"),
//                                limit = 30,
//
//                                )
//
//
//                        val channelListViewModel: ChannelListViewModel by viewModels { channelListFactory }
//
//
//                        channelListHeaderViewModel.bindView(binding.channelListHeaderView, this)
//                        channelListViewModel.bindView(binding.channelListView, this)
//
//                        binding.channelListView.setChannelItemClickListener { channel ->
//                            startActivity(ChannelActivity.newIntent(requireContext(), channel))
//                        }
//
//                        binding.channelListView.setIsMoreOptionsVisible { channel ->
//                            // You can determine visibility based on the channel object.
//                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_setting)
//
//                            true
//                        }
//
//                        binding.channelListView.setIsDeleteOptionVisible { channel ->
//                            // You can determine visibility based on the channel object.
//                            // Here is the default implementation:
//                            channel.ownCapabilities.contains("delete-channel")
//                        }
//
//                    } else {
//                        Toast.makeText(
//                            requireContext(),
//                            "Something went wrong!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }



        client?.let { chatClient ->
            chatClient.connectUser(
                user = myUser,
                tokenProvider
            ).enqueue { result ->
                // 비동기 작업 결과 처리
                // 프래그먼트의 뷰가 생성된 상태인지 확인

                // 여기는 대학생 목록에서 원하는 대학생과 메세지를 보내는 과정
                val studentUid = arguments?.getString("studentUid", null)
                if (studentUid != null) {

                    val calendar = Calendar.getInstance().apply {
                        add(Calendar.SECOND, 10)  // 현재 시간에서 5분을 추가
                    }

                    val freezeTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }.format(calendar.time)

                    val extraData = mapOf("freeze_time" to freezeTime)

                    val channelClient = chatClient.channel(channelType = "messaging", channelId = "")
                    channelClient.create(memberIds = listOf(studentUid, myUser.id), extraData = extraData)
                        .enqueue { result ->
                            if (result.isSuccess) {
                                val newChannel = result.getOrThrow()
                                startActivity(ChannelActivity.newIntent(requireContext(), newChannel))
                            } else {
                                Log.e("Channel Creation Failed", result.toString() ?: "Error creating channel")
                            }
                        }
                }


                if (isAdded && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    if (result.isSuccess) {
                        // ViewModel 바인딩과 UI 업데이트
                        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()

                        val channelListFactory: ChannelListViewModelFactory =
                            ChannelListViewModelFactory(
                                filter = Filters.and(
                                    Filters.eq("type", "messaging"),
                                    Filters.`in`(
                                        "members",
                                        listOf(ChatClient.instance().getCurrentUser()!!.id)
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

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    }


    //TODO 관광객들은 시간이 지나면 토큰을 해제해야 한다.
    // 파이어베이스랑 연동을 시키니까 굳이 user 데이터를 들고 와서 할 필요는 없다.
    private suspend fun getNewToken(): String = suspendCoroutine { continuation ->
        val functions = FirebaseFunctions.getInstance("asia-northeast3")
        functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
            .call()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 함수 호출이 성공했습니다. 반환된 데이터에서 토큰을 추출합니다.
                    val token = task.result?.data as String
                    BusanPartners.preferences.setString(TOKEN, token)
                    continuation.resume(token) // 코루틴을 재개하고 결과를 반환합니다.
                } else {
                    // 호출 실패. 에러를 처리합니다.
                    Log.e(TAG, "토큰 호출을 실패했습니다.")
                    continuation.resumeWithException(
                        task.exception ?: RuntimeException("Unknown token fetch error")
                    )
                }
            }
    }


    // 여기에서 GetStream 채팅 클라이언트에 토큰을 사용합니다.
}
