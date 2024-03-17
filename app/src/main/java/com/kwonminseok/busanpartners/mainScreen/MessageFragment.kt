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
import androidx.lifecycle.lifecycleScope
import com.google.firebase.functions.FirebaseFunctions
import com.kwonminseok.busanpartners.BuildConfig
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
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
    private val viewModel by viewModels<ChatListViewModel>()
    private lateinit var client: ChatClient
    lateinit var user: com.kwonminseok.busanpartners.data.User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate","onCreate")


    }

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
        Log.e("onViewCreated","onViewCreated")

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
// Step 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = requireContext())
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = requireContext(),
        )

        client = ChatClient.Builder(BuildConfig.API_KEY, requireContext())
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()


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

    override fun onStart() {
        super.onStart()
        Log.e("onStart","onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume","onResume")

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    // GetStream API에 사용자 연결
    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {




        val myUser = User(
            id = user.uid,
            name = "${user.firstName} ${user.lastName}",
            image = user.imagePath
        )

//        getChatListView(client, myUser, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiazNmN0RpVVVDT1NtenYzRUJ3QWduVnZGQjNrMiIsImlhdCI6MTcxMDQ5Mjc0OX0.TTOr-aRtWZrccCLmahbCNEPStutGyI1L8Ov6MLF-BPk")

        // Firebase Functions 인스턴스를 가져옵니다.
        val functions = FirebaseFunctions.getInstance("asia-northeast3")

// `ext-auth-chat-getStreamUserToken` 함수를 호출하여 토큰을 요청합니다.
        functions
            .getHttpsCallable("ext-auth-chat-getStreamUserToken")
            .call()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    // 함수 호출이 성공했습니다. 반환된 데이터에서 토큰을 추출합니다.
                    val token = task.result?.data as String
                    Log.e(TAG,"$token")
                    getChatListView(client, myUser, token)

                    // 여기에서 GetStream 채팅 클라이언트에 토큰을 사용합니다.
                } else {
                    // 함수 호출이 실패했습니다. 오류를 처리합니다.
                    val exception = task.exception
                    Log.e("exception", task.exception.toString())

                    // 오류 처리 로직을 구현합니다.
                }
            }
//        val token = client.devToken(user.uid)
        // Firebase Functions을 호출하여 Stream Chat 토큰을 가져옴
        // 여기서 토큰을 사용하여 Stream Chat에 로그인하거나 다른 작업을 수행할 수 있음

    }
    private fun getChatListView(
        client: ChatClient,
        myUser: User,
        token: String
    ) {
        client.connectUser(
            user = myUser,
            token = token
        ) // Replace with a real token BuildConfig.MY_TOKEN
            .enqueue {
                if (it.isSuccess) {
                    // viewModel 초기화 시키기
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

                    //                    binding.channelListHeaderView.setOnActionButtonClickListener {
                    //                        Toast.makeText(this,"Action clicked", Toast.LENGTH_SHORT).show()
                    //                    }
                    //                    binding.channelListHeaderView.setOnUserAvatarClickListener {
                    //                        Toast.makeText(this,"User Avatar clicked", Toast.LENGTH_SHORT).show()
                    //                    }

                    ////// Create custom view holder factory
                    //                    val customFactory = CustomChannelListItemViewHolderFactory()
                    //
                    //// Set custom view holder factory
                    //                    binding.channelListView.setViewHolderFactory(customFactory)

                    // Step 3 - Apply Style Transformation
                    //                    applyStyleTransformation()


                    //                    TransformStyle.channelListStyleTransformer = StyleTransformer { defaultStyle ->
                    //                        defaultStyle.copy(
                    //                            optionsEnabled = false,
                    //                            foregroundLayoutColor = Color.LTGRAY,
                    //                            channelTitleText = defaultStyle.channelTitleText.copy(
                    //                                color = Color.BLUE,
                    //                            ),
                    //                            unreadMessageCounterBackgroundColor = Color.BLUE,
                    //                        )
                    //                    }
                    binding.channelListView.setChannelItemClickListener { channel ->
                        startActivity(ChannelActivity.newIntent(requireContext(), channel))
                    }

                    //                    binding.channelListView.setChannelLongClickListener { channel ->
                    //                        Toast.makeText(this, "click long", Toast.LENGTH_SHORT).show()
                    //                        true
                    //                    }
                    //
                    //                    binding.channelListView.setMoreOptionsIconProvider { channel ->
                    //                        // You can generate the icon Drawable based on the channel object
                    //                        ContextCompat.getDrawable(this, io.getstream.chat.android.ui.R.drawable.stream_ui_ic_more)
                    //                    }
                    //                    binding.channelListView.setDeleteOptionIconProvider { channel ->
                    //                        // You can generate the icon Drawable based on the channel object
                    //                        ContextCompat.getDrawable(this, R.drawable.baseline_delete_24)
                    //                    }

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
                    Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT).show()
                }

            }
    }
}