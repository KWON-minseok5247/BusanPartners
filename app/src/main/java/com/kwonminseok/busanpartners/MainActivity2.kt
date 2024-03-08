package com.kwonminseok.busanpartners

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.ChannelActivity
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ActivityMainBinding
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ChatListViewModel
import com.kwonminseok.busanpartners.viewmodel.LoginsViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User
import io.getstream.chat.android.models.querysort.QuerySortByField
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.chat.android.ui.feature.channels.ChannelListActivity
import io.getstream.chat.android.ui.feature.channels.ChannelListFragment
import io.getstream.chat.android.ui.helper.StyleTransformer
import io.getstream.chat.android.ui.helper.TransformStyle
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import java.time.Duration

class MainActivity2 : AppCompatActivity() {
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

        // Step 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = this)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = this,
        )
        // 여기서 파이어베이스 정보를 받고 user를 정의한다.
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        user = it.data!!

                    }

                    is Resource.Error -> {

                    }

                    else -> Unit
                }
            }
        }

        // Step 2 - Set up the client for API calls with the plugin for offline storage
        val client = ChatClient.Builder(BuildConfig.API_KEY, applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()

//        val user = User(
//            id = "tutorial-droid",
//            name = "Tutorial Droid",
//            image = "https://bit.ly/2TIt8NR"
//        )


        val user = User(
            id = "Kim",
            name = "Kim",
            image = ""
        )



        client.connectUser(
            user = user,
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiS2ltIn0.h2o_zyqgtVTaQEpJVjV7ecQ2DMpqOzhXOF6FUqAeXOE"
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

//                    binding.channelListView.setMoreOptionsIconProvider { channel ->
//                        // You can generate the icon Drawable based on the channel object
//                        ContextCompat.getDrawable(this, io.getstream.chat.android.ui.R.drawable.stream_ui_ic_more)
//                    }
//                    binding.channelListView.setDeleteOptionIconProvider { channel ->
//                        // You can generate the icon Drawable based on the channel object
//                        ContextCompat.getDrawable(this, R.drawable.baseline_delete_24)
//                    }
//
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
    private fun connectUserToStream() {

    }
}





