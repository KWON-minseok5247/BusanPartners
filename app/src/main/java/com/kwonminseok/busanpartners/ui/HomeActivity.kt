package com.kwonminseok.busanpartners.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.databinding.ActivityHomeBinding
import com.kwonminseok.busanpartners.extensions.setStatusBarTransparent
import com.kwonminseok.busanpartners.ui.login.SplashActivity
import com.kwonminseok.busanpartners.ui.login.SplashActivity.Companion.currentUser
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.client.events.MessageReadEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.events.NotificationMarkReadEvent
import io.getstream.chat.android.client.events.NotificationMessageNewEvent
import io.getstream.chat.android.client.events.UserPresenceChangedEvent
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.result.Result
import java.util.Locale

const val EXTRA_CHANNEL_ID = "extra_channel_id"
const val EXTRA_CHANNEL_TYPE = "extra_channel_type"
const val EXTRA_MESSAGE_ID = "extra_message_id"
const val EXTRA_PARENT_MESSAGE_ID = "extra_parent_message_id"

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    //    private val viewModel: UserViewModel by viewModels()
    private var backPressedTime: Long = 0
    private lateinit var toast: Toast

    private lateinit var fragmentManager: FragmentManager
    private val unreadCountLiveData = MutableLiveData<Int>()


    val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedLocale()
        setContentView(binding.root)

//        val navOptions = NavOptions.Builder()
//            .setLaunchSingleTop(true) // 현재 화면이 이미 스택에 있으면 해당 화면을 재사용
//            .build()

//        setStatusBarTransparent()
//        applyWindowInsets(binding.root)

//        // 아래 이것들로 인해 프래그먼트가 움직인다?
        val navController = findNavController(R.id.homeHostFragment)

        val navOptions = navOptions {
            anim {
                enter = R.anim.animate_fade_enter
                exit = R.anim.animate_fade_exit
                popEnter = R.anim.animate_fade_enter
                popExit = R.anim.animate_fade_exit
            }
        }

//        binding.bottomNavigation.setupWithNavController(navController)


        binding.bottomNavigation.apply {
            setupWithNavController(navController)
            setOnItemSelectedListener { item ->
                navController.navigate(item.itemId, null, navOptions)
                true
            }
        }



//        binding.bottomNavigation.apply {
//            setupWithNavController(navController)
//            setOnItemSelectedListener { item ->
//                NavigationUI.onNavDestinationSelected(item, navController)
//                navController.popBackStack(item.itemId, inclusive = false)
//                true
//            }
//        }




//        chatClient.subscribeFor(
//            NotificationMessageNewEvent::class.java,
//            NewMessageEvent::class.java,
//            MessageReadEvent::class.java
//        ) { event: ChatEvent ->
//            when (event) {
//                is NotificationMessageNewEvent -> {
//                    updateBadge(event.totalUnreadCount ?: 0)
//                }
//                is NewMessageEvent -> {
//                    updateBadge(chatClient.getCurrentUser()?.totalUnreadCount ?: 0)
//                }
//                is MessageReadEvent -> {
//                    updateBadge(chatClient.getCurrentUser()?.totalUnreadCount ?: 0)
//                }
//                else -> {
//                    // No action needed
//                }
//            }
//            Log.d("ChatEvent", "새 메시지가 도착했습니다: $event")
//        }
//
//        updateBadge(chatClient.getCurrentUser()?.totalUnreadCount ?: 0)

        // 예시: GetStream SDK에서 메시지 수신 이벤트를 옵저빙
//        getUnreadMessageCountLiveData().observe(this, Observer { unreadCount ->
//            if (unreadCount == 0) {
//                binding.bottomNavigation.removeBadge(R.id.messageFragment)
//            } else {
//                binding.bottomNavigation.getOrCreateBadge(R.id.messageFragment).apply {
//                    number = unreadCount
//                    backgroundColor = resources.getColor(R.color.g_blue)
//                }
//            }
//        })

        unreadCountLiveData.observe(this, Observer { unreadCount ->
            if (unreadCount == 0) {
                binding.bottomNavigation.removeBadge(R.id.messageFragment)
            } else {
                binding.bottomNavigation.getOrCreateBadge(R.id.messageFragment).apply {
                    number = unreadCount
                    backgroundColor = resources.getColor(R.color.green_message_count)
                }
            }
        })
        subscribeToChannelUpdates()
        getUnreadMessageCountLiveData()


    }

    override fun onResume() {
        super.onResume()
        if (currentUser == null) {
            val intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun updateBadge(unreadCount: Int) {
        Handler(Looper.getMainLooper()).post {
            Log.d("MainActivity", "Updating badge with count: $unreadCount")
            if (unreadCount == 0) {
                binding.bottomNavigation.removeBadge(R.id.messageFragment)
            } else {
                binding.bottomNavigation.getOrCreateBadge(R.id.messageFragment).apply {
                    number = unreadCount
                    backgroundColor = resources.getColor(R.color.g_blue, null)
                }
            }
        }


    }

    private fun subscribeToChannelUpdates() {
        chatClient.subscribeFor(
            NewMessageEvent::class.java,
            NotificationMarkReadEvent::class.java,
            UserPresenceChangedEvent::class.java
        ) { event ->
            when (event) {
                is NewMessageEvent -> {
                    // 메시지가 새로 수신되었을 때 처리
                    getUnreadMessageCountLiveData()
                }
                is NotificationMarkReadEvent -> {
                    // 메시지가 읽혔을 때 처리
                    getUnreadMessageCountLiveData()
                }
                is UserPresenceChangedEvent -> {
                    // 사용자의 상태가 변경되었을 때 처리
                    getUnreadMessageCountLiveData()
                }

                else -> {getUnreadMessageCountLiveData()}
            }
        }
    }



    private fun getUnreadMessageCountLiveData() {

        val filter = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(chatClient.getCurrentUser()?.id ?: ""))
        )

        val request = QueryChannelsRequest(
            filter = filter,
            offset = 0,
            limit = 30,
        ).apply {
            watch = true
            state = true
        }

        chatClient.queryChannels(request).enqueue { result: Result<List<Channel>> ->
            if (result.isSuccess) {
                val channels = result.getOrThrow()
                val unreadCount = channels.sumOf { it.unreadCount ?: 0 }
                unreadCountLiveData.postValue(unreadCount)
            } else {
                // Error handling
                unreadCountLiveData.postValue(0)
            }
        }
    }


    companion object {

        fun createLaunchIntent(
            context: Context,
            messageId: String,
            parentMessageId: String?,
            channelType: String,
            channelId: String,
        ) = Intent(context, HomeActivity::class.java).apply {
            putExtra(EXTRA_CHANNEL_ID, channelId)
            putExtra(EXTRA_CHANNEL_TYPE, channelType)
            putExtra(EXTRA_MESSAGE_ID, messageId)
            putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
        }
    }

    private fun requestNotificationAccess(context: Context) {
        if (!isNotificationServiceEnabled(context)) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
        }
    }

    private fun isNotificationServiceEnabled(context: Context): Boolean {
        val pkgName = context.packageName
        val flat =
            Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return !TextUtils.isEmpty(flat) && flat.contains(pkgName)
    }


    private fun applyWindowInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.setPadding(0, statusBarInsets.top, 0, 0)
            WindowInsetsCompat.CONSUMED
        }
    }

    fun getStatusBarHeight(activity: Activity): Int {
        val windowInsets = ViewCompat.getRootWindowInsets(activity.window.decorView)
        return windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
    }


    private fun applySavedLocale() {
        val localeString = BusanPartners.preferences.getString("selected_locale", "")
        val locale = if (localeString.isEmpty()) {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(localeString)
        }

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun recreateActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

}