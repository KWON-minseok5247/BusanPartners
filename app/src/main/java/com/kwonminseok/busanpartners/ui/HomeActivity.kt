package com.kwonminseok.busanpartners.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

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

    val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true) // 현재 화면이 이미 스택에 있으면 해당 화면을 재사용
            .build()


//        // 아래 이것들로 인해 프래그먼트가 움직인다?
        val navController = findNavController(R.id.homeHostFragment)
//        binding.bottomNavigation.setupWithNavController(navController)


        binding.bottomNavigation.apply {
            setupWithNavController(navController)
            setOnItemSelectedListener { item ->
                NavigationUI.onNavDestinationSelected(item, navController)
                navController.popBackStack(item.itemId, inclusive = false)
                true
            }
            val count = chatClient.getCurrentUser()?.totalUnreadCount
            if (count == null || count == 0) {
                removeBadge(R.id.messageFragment)
            } else {
                getOrCreateBadge(R.id.messageFragment).apply {
                    number = count
                    backgroundColor = resources.getColor(R.color.g_blue)
                }
            }


        }
//        requestNotificationAccess(this)


//binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
//    when (item.itemId) {
//        R.id.navigation_home -> {
//            // Home 프래그먼트를 새로 생성하고 표시
//            val homeFragment = HomeFragment.newInstance()
//            navController.navigate(R.id.homeFragment, null, NavOptions.Builder()
//                .setPopUpTo(R.id.homeFragment, true).build())
//            true
//        }
//        R.id.navigation_dashboard -> {
//            // Dashboard 프래그먼트를 새로 생성하고 표시
//            val dashboardFragment = DashboardFragment.newInstance()
//            navController.navigate(R.id.dashboardFragment, null, NavOptions.Builder()
//                .setPopUpTo(R.id.dashboardFragment, true).build())
//            true
//        }
//        R.id.navigation_notifications -> {
//            // Notifications 프래그먼트를 새로 생성하고 표시
//            val notificationsFragment = NotificationsFragment.newInstance()
//            navController.navigate(R.id.notificationsFragment, null, NavOptions.Builder()
//                .setPopUpTo(R.id.notificationsFragment, true).build())
//            true
//        }
//        else -> false
//    }
//}

    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            toast.cancel()
            finishAffinity() // 앱의 모든 활동을 종료
        } else {
            backPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this, "뒤로가기를 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
            toast.show()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!isNotificationServiceEnabled(context)) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent)
            }
        }
    }

    private fun isNotificationServiceEnabled(context: Context): Boolean {
        val pkgName = context.packageName
        val flat =
            Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return !TextUtils.isEmpty(flat) && flat.contains(pkgName)
    }
}