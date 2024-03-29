package com.kwonminseok.busanpartners.mainScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.functions.FirebaseFunctions
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ActivityMainBinding
import com.kwonminseok.busanpartners.databinding.FragmentMessageBinding
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.User
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
    private val viewModel by viewModels<ChatListViewModel>()
    private var client: ChatClient? = BusanPartners.chatClient
    lateinit var user: com.kwonminseok.busanpartners.data.User


    // 토큰 절차 1: 일단 token이 있는지 없는지 확인, 있으면 바로 가져온다.
    private var token: String = BusanPartners.preferences.getString("token", "")


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

// Inflate loading view
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

    //TODO 유저 네임이 비어있으면 안된다 나중에 실행하기
    // 아래와 같이 코드를 작성하니 뷰모델을 불러오는 도중 프래그먼트를 이동해도 에러가 발생하지 않는다.
    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
        val myUser = User(
            id = user.uid,
            name = user.name!!,
            image = user.imagePath
        )

        if (token == "") {
            Log.e(TAG, "token이 비어있을 때.")
            lifecycleScope.launch {
                token = getNewToken()
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
    }

    private fun connectClient(myUser: User) {
        client?.let { chatClient ->
            chatClient.connectUser(
                user = myUser,
                token = token
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
                            true
                        }

                        binding.channelListView.setIsDeleteOptionVisible { channel ->
                            // You can determine visibility based on the channel object.
                            // Here is the default implementation:
                            channel.ownCapabilities.contains("delete-channel")
                        }

                    } else {
                        Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    // GetStream API에 사용자 연결
//    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
//
//        // 먼저 파이어베이스로부터 User 데이터를 받아들인다.
//        val myUser = User(
//            id = user.uid,
//            name = "${user.firstName} ${user.lastName}",
//            image = user.imagePath
//        )
//        // 확인해야 할 것은 Client를 재사용하는지? 토큰을 이미 가지고 있는지?
//
//        if (token == "") {
//            Log.e(TAG, "token이 비어있을 때.")
//            lifecycleScope.launch {
//                token= getNewToken()
//                connectClient(myUser)
//
//            }
//
//        } else {
//            connectClient(myUser)
//        }
//
//        // client?.connectUser 호출 전에 client가 null인지 다시 확인하고, null이 아닌 경우에만 connectUser를 호출합니다.
//    }
//
//    private fun connectClient(myUser: User) {
//        client?.let { chatClient ->
//            chatClient.connectUser(
//                user = myUser,
//                token = token
//            ).enqueue {
//                // 성공 또는 실패에 대한 처리
//                if (it.isSuccess) {
//                    // viewModel 초기화 시키기
//                    val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
//
//                    val channelListFactory: ChannelListViewModelFactory =
//                        ChannelListViewModelFactory(
//                            filter = Filters.and(
//                                Filters.eq("type", "messaging"),
//                                Filters.`in`(
//                                    "members",
//                                    listOf(ChatClient.instance().getCurrentUser()!!.id)
//                                ),
//                            ),
//                            sort = QuerySortByField.descByName("last_updated"),
//                            limit = 30,
//
//                            )
//
//
//                    val channelListViewModel: ChannelListViewModel by viewModels { channelListFactory }
//
//
//                    channelListHeaderViewModel.bindView(binding.channelListHeaderView, this)
//                    channelListViewModel.bindView(binding.channelListView, this)
//
//                    binding.channelListView.setChannelItemClickListener { channel ->
//                        startActivity(ChannelActivity.newIntent(requireContext(), channel))
//                    }
//
//                    binding.channelListView.setIsMoreOptionsVisible { channel ->
//                        // You can determine visibility based on the channel object.
//                        true
//                    }
//
//                    binding.channelListView.setIsDeleteOptionVisible { channel ->
//                        // You can determine visibility based on the channel object.
//                        // Here is the default implementation:
//                        channel.ownCapabilities.contains("delete-channel")
//                    }
//
//
//                } else {
//                    Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT).show()
//                    lifecycleScope.launch {
//                        getNewToken()
//                    }
//                }
//            }
//        } ?: run {
//            //TODO 여기서 토큰이 만료가 되면 새로 토큰을 업데이트하고 다시 실행해야 한다.
//            Log.e(TAG, "Client 초기화 실패")
//        }
//    }


//    private suspend fun getNewToken(): String {
//        // 기본적으로 사용자 토큰은 무기한 유효합니다. 토큰을 두 번째 매개변수로 전달하여 토큰 만료를 설정할 수 있습니다.
//        // 만료에는 Unix 시대(1970년 1월 1일 00:00:00 UTC) 이후의 초 수가 포함되어야 합니다.
//
//        // Firebase Functions 인스턴스를 가져옵니다.
//        val functions = FirebaseFunctions.getInstance("asia-northeast3")
//
//// `ext-auth-chat-getStreamUserToken` 함수를 호출하여 토큰을 요청합니다.
//        functions
//            .getHttpsCallable("ext-auth-chat-getStreamUserToken")
//            .call()
//            .addOnCompleteListener { task ->
//
//                if (task.isSuccessful) {
//                    // 함수 호출이 성공했습니다. 반환된 데이터에서 토큰을 추출합니다.
//                    token = task.result?.data as String
//                    BusanPartners.preferences.setString("token", token)
//                } else {
//                    Log.e(TAG, "토큰 호출을 실패했습니다.")
//                }
//            }
//        return token
//    }
private suspend fun getNewToken(): String = suspendCoroutine { continuation ->
    val functions = FirebaseFunctions.getInstance("asia-northeast3")
    functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
        .call()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 함수 호출이 성공했습니다. 반환된 데이터에서 토큰을 추출합니다.
                val token = task.result?.data as String
                BusanPartners.preferences.setString("token", token)
                continuation.resume(token) // 코루틴을 재개하고 결과를 반환합니다.
            } else {
                // 호출 실패. 에러를 처리합니다.
                Log.e(TAG, "토큰 호출을 실패했습니다.")
                continuation.resumeWithException(task.exception ?: RuntimeException("Unknown token fetch error"))
            }
        }
}



        // 여기에서 GetStream 채팅 클라이언트에 토큰을 사용합니다.
    }
