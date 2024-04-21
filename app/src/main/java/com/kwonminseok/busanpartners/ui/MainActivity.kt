package com.kwonminseok.busanpartners.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ActivityMainBinding
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
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

val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
//    // 1
//    val factory = MessageListViewModelFactory(cid = "messaging:123")
//    // 2
//    val viewModel: MessageListViewModel by viewModels { factory }
//// 3
//    viewModel.bindView(messageListView, viewLifecycleOwner)

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<ChatListViewModel>()

    lateinit var user: com.kwonminseok.busanpartners.data.User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


// Inflate loading view
        val loadingView =
            LayoutInflater.from(this).inflate(R.layout.channel_list_loading_view, null)
// Set loading view
        binding.channelListView.setLoadingView(
            loadingView,
            FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
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

        // Step 2 - Set up the client for API calls with the plugin for offline storage

    }

//    private fun applyStyleTransformation() {
//        TransformStyle.channelListStyleTransformer = StyleTransformer { defaultStyle ->
//            defaultStyle.copy(
//                optionsEnabled = false,
//                foregroundLayoutColor = Color.LTGRAY,
//                channelTitleText = defaultStyle.channelTitleText.copy(
//                    color = Color.BLUE,
//                    size = resources.getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_content_inset_material)
//                ),
//                unreadMessageCounterBackgroundColor = Color.BLUE
//            )
//        }
//        Log.e("asd","applyStyleTransformation Complete!!")
//    }

    private fun Context.toast(
        message: String,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(this, message, duration).show()
    }


    // GetStream API에 사용자 연결
    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {

        // Step 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = this,
        )

        val client = ChatClient.Builder(BuildConfig.API_KEY, applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()



//        val user = User(
//            id = "Kim",
//            name = "Kim",
//            image = ""
//        )


        val myUser = User(
            id = user.uid,
            name = "${user.firstName} ${user.lastName}",
            image = user.imagePath
        )
//        // Firebase Functions를 가져옴
//        val functions = Firebase.functions
//
//        // Firebase Functions에서 ext-auth-chat-getStreamUserToken 함수를 가져옴
//        val getStreamUserToken = functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
//
//        val data = hashMapOf("uid" to user.uid)
//        getStreamUserToken(user.uid,
//            onSuccess = { token ->
//                // 토큰을 성공적으로 받았을 때 처리
//                Log.e("token","GetStream 토큰: $token")
//                // 이곳에서 GetStream 클라이언트를 초기화하고 작업을 수행할 수 있습니다.
//            },
//            onFailure = { exception ->
//                // 토큰을 받지 못했을 때 처리
//                Log.e("token","토큰을 가져오는 데 실패했습니다: ${exception.message}")
//            }
//        )

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
                        startActivity(ChannelActivity.newIntent(this, channel))
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
                    Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show()
                }

            }
    }

    fun getStreamUserToken(uid: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val functions = Firebase.functions
        val getStreamUserToken = functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")

        val data = hashMapOf("uid" to uid)

        getStreamUserToken.call(data)
            .addOnSuccessListener { result ->
                val token = result.data as String
                Log.e("in the function", token)
                onSuccess.invoke(token)
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

}





