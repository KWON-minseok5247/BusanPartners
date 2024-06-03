package com.kwonminseok.busanpartners.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.db.entity.UserEntity
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.extensions.toUser
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.repository.TimeRepository
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_ID
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_TYPE
import com.kwonminseok.busanpartners.ui.EXTRA_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.EXTRA_PARENT_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.ChatEventListener
import io.getstream.chat.android.client.events.NotificationChannelMutesUpdatedEvent
import io.getstream.chat.android.client.token.TokenProvider
import io.getstream.chat.android.models.ChannelMute
import io.getstream.chat.android.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//private val TAG = "SplashActivity"
//@SuppressLint("CustomSplashScreen")
//@AndroidEntryPoint
//class SplashActivity : AppCompatActivity() {
//
//    private val PERMISSION_REQUEST_CODE = 1000
//
//    private var client: ChatClient? = BusanPartners.chatClient
//    lateinit var user: com.kwonminseok.busanpartners.data.User
//    private val viewModel: UserViewModel by viewModels()
//    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
//
//    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
//
//    private val uid = BusanPartners.preferences.getString("uid", "")
//
//    init {
//        lifecycleScope.launch {
//            TimeRepository.fetchCurrentTime()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        supportActionBar?.hide()
//        setContentView(R.layout.activity_splash)
//        Log.e("Splash 화면이", "시작되었습니다.")
//
//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        if (firebaseUser == null) {
//            Log.e("firebaseUser가", "null이다.")
//            navigateToLoginRegisterActivity()
//            return
//        }
//
//        fetchCurrentUserEntity()
//
//        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                setupUserStream()
//            } else {
//                Log.e("자동 로그인을 할 때", "task가 실패")
//                navigateToLoginRegisterActivity()
//            }
//        }?.addOnFailureListener {
//            Log.e("자동 로그인을 할 때", "토큰 얻는 것 자체를 실패함.")
//            navigateToLoginRegisterActivity()
//        }
//    }
//
//    private fun navigateToLoginRegisterActivity() {
//        val intent = Intent(this, LoginRegisterActivity::class.java).addFlags(
//            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        )
//        startActivity(intent)
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
//                    Log.e(TAG, "토큰 호출을 실패했습니다.", exception)
//                    continuation.resumeWithException(exception)
//                }
//            }
//    }
//
//    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
//        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
//        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)
//
//        if (currentServerTimeToDateTime != null && currentServerTimeToDateTime <= tokenTimeToDateTime) {
//            val myUser = User.Builder()
//                .withId(user.uid)
//                .withName(user.name?.ko ?: "ㅁㄴㅇ")
//                .withImage(user.imagePath)
//                .build()
//            Log.e("myUser", myUser.toString())
//
//            if (token.isEmpty()) {
//                Log.e(TAG, "token이 비어있을 때.")
//                lifecycleScope.launch {
//                    getNewToken()
//                    connectClient(myUser)
//                }
//            } else {
//                connectClient(myUser)
//            }
//        } else {
//            val guestUser = User(
//                id = "guestID",
//                name = "guestID",
//                image = "https://bit.ly/2TIt8NR"
//            )
//
//            client?.let { chatClient ->
//                chatClient.connectUser(
//                    guestUser,
//                    BuildConfig.GUEST_ID_TOKEN
//                ).enqueue { result ->
//                    if (result.isSuccess) {
//                        navigateToHomeActivity()
//                    }
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
//        client?.let { chatClient ->
//            chatClient.connectUser(myUser, tokenProvider).enqueue { result ->
//                if (result.isSuccess) {
//                    navigateToHomeActivity()
//                } else {
//                    Log.e("connectUser", "Failed to connect user: ${result.errorOrNull()}")
//                    navigateToLoginRegisterActivity()
//                }
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
//    private fun fetchCurrentUserEntity() {
//        viewModel.getUserStateFlowData(uid).observe(this) { userEntity ->
//            if (userEntity == null) {
//                viewModel.getCurrentUser()
//                lifecycleScope.launchWhenStarted {
//                    viewModel.user.collectLatest { resource ->
//                        when (resource) {
//                            is Resource.Loading -> {}
//                            is Resource.Success -> {
//                                user = resource.data!!
//                                viewModel.insertUser(user.toEntity())
//                                currentUser = user.toEntity()
//                            }
//                            is Resource.Error -> {
//                                Toast.makeText(
//                                    this@SplashActivity,
//                                    resource.message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            else -> Unit
//                        }
//                    }
//                }
//            } else {
//                currentUser = userEntity
//                user = userEntity.toUser()
//                viewModel.getCurrentUser()
//
//                lifecycleScope.launchWhenStarted {
//                    viewModel.user.collectLatest { resource ->
//                        when (resource) {
//                            is Resource.Success -> {
//                                if (user == resource.data) {
//                                    Log.e("UserEntity가 존재한다면?", "서버와 동일한 데이터")
//                                } else {
//                                    Log.e("UserEntity가 존재한다면?", "서버와 다른 데이터")
//                                    user = resource.data!!
//                                    viewModel.updateUser(user.toEntity())
//                                }
//                            }
//                            is Resource.Error -> {}
//                            else -> Unit
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun navigateToHomeActivity() {
//        val intent = Intent(this, HomeActivity::class.java).addFlags(
//            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        )
//        startActivity(intent)
//    }
//
//    companion object {
//        var currentUser: UserEntity? = null
//
//        fun createLaunchIntent(
//            context: Context,
//            messageId: String,
//            parentMessageId: String?,
//            channelType: String,
//            channelId: String,
//        ) = Intent(context, SplashActivity::class.java).apply {
//            putExtra(EXTRA_CHANNEL_ID, channelId)
//            putExtra(EXTRA_CHANNEL_TYPE, channelType)
//            putExtra(EXTRA_MESSAGE_ID, messageId)
//            putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
//        }
//    }
//}

//private val TAG = "SplashActivity"
//@SuppressLint("CustomSplashScreen")
//@AndroidEntryPoint
//class SplashActivity : AppCompatActivity() {
//
//    private val PERMISSION_REQUEST_CODE = 1000
//    private var client: ChatClient? = BusanPartners.chatClient
//    lateinit var user: com.kwonminseok.busanpartners.data.User
//    private val viewModel: UserViewModel by viewModels()
//    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
//    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
//    private val uid = BusanPartners.preferences.getString("uid", "")
//    private var retryCount = 0
//    private val maxRetries = 3
//    private val timeoutDuration = 15000L // 15 seconds
//
//    init {
//        lifecycleScope.launch {
//            TimeRepository.fetchCurrentTime()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        supportActionBar?.hide()
//        setContentView(R.layout.activity_splash)
//        Log.e("Splash 화면이", "시작되었습니다.")
//
//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        if (firebaseUser == null) {
//            Log.e("firebaseUser가", "null이다.")
//            navigateToLoginRegisterActivity()
//            return
//        }
//
//        fetchCurrentUserEntity()
//        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                setupUserStream()
//            } else {
//                Log.e("자동 로그인을 할 때", "task가 실패")
//                navigateToLoginRegisterActivity()
//            }
//        }?.addOnFailureListener {
//            Log.e("자동 로그인을 할 때", "토큰 얻는 것 자체를 실패함.")
//            navigateToLoginRegisterActivity()
//        }
//
//        // 타임아웃 설정
//        lifecycleScope.launch {
//            delay(timeoutDuration)
//            if (!isFinishing) {
//                Toast.makeText(this@SplashActivity, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
//                navigateToLoginRegisterActivity()
//            }
//        }
//    }
//
//    private fun navigateToLoginRegisterActivity() {
//        val intent = Intent(this, LoginRegisterActivity::class.java).addFlags(
//            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        )
//        startActivity(intent)
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
//                    Log.e(TAG, "토큰 호출을 실패했습니다.", exception)
//                    continuation.resumeWithException(exception)
//                }
//            }
//    }
//
//    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
//        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
//        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)
//
//        if (currentServerTimeToDateTime != null && currentServerTimeToDateTime <= tokenTimeToDateTime) {
//            val myUser = User.Builder()
//                .withId(user.uid)
//                .withName(user.name?.ko ?: "ㅁㄴㅇ")
//                .withImage(user.imagePath)
//                .build()
//            Log.e("myUser", myUser.toString())
//
//            if (token.isEmpty()) {
//                Log.e(TAG, "token이 비어있을 때.")
//                lifecycleScope.launch {
//                    getNewToken()
//                    connectClient(myUser)
//                }
//            } else {
//                connectClient(myUser)
//            }
//        } else {
//            val guestUser = User(
//                id = "guestID",
//                name = "guestID",
//                image = "https://bit.ly/2TIt8NR"
//            )
//
//            client?.let { chatClient ->
//                chatClient.connectUser(
//                    guestUser,
//                    BuildConfig.GUEST_ID_TOKEN
//                ).enqueue { result ->
//                    if (result.isSuccess) {
//                        navigateToHomeActivity()
//                    }
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
//        client?.let { chatClient ->
//            chatClient.connectUser(myUser, tokenProvider).enqueue { result ->
//                if (result.isSuccess) {
//                    navigateToHomeActivity()
//                } else {
//                    Log.e("connectUser", "Failed to connect user: ${result.errorOrNull()}")
//                    handleRetryOrNavigate()
//                }
//            }
//        }
//    }
//
//    private fun handleRetryOrNavigate() {
//        if (retryCount < maxRetries) {
//            retryCount++
//            Log.e("Retry", "Retrying to connect... attempt $retryCount")
//            setupUserStream()
//        } else {
//            Log.e("Retry", "Max retries reached, navigating to login")
//            navigateToLoginRegisterActivity()
//        }
//    }
//
//    private fun setupUserStream() {
//        lifecycleScope.launch {
//            try {
//                TimeRepository.fetchCurrentTime()
//                currentServerTime = TimeRepository.currentTime?.datetime
//                BusanPartners.preferences.setString("uid", user.uid)
//                connectUserToStream(user)
//            } catch (e: Exception) {
//                Log.e("setupUserStream", "Exception: ${e.message}")
//                handleRetryOrNavigate()
//            }
//        }
//    }
//
//    private fun fetchCurrentUserEntity() {
//        viewModel.getUserStateFlowData(uid).observe(this) { userEntity ->
//            if (userEntity == null) {
//                viewModel.getCurrentUser()
//                lifecycleScope.launchWhenStarted {
//                    viewModel.user.collectLatest { resource ->
//                        when (resource) {
//                            is Resource.Loading -> {}
//                            is Resource.Success -> {
//                                user = resource.data!!
//                                viewModel.insertUser(user.toEntity())
//                                currentUser = user.toEntity()
//                            }
//                            is Resource.Error -> {
//                                Toast.makeText(
//                                    this@SplashActivity,
//                                    resource.message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            else -> Unit
//                        }
//                    }
//                }
//            } else {
//                currentUser = userEntity
//                user = userEntity.toUser()
//                viewModel.getCurrentUser()
//                lifecycleScope.launchWhenStarted {
//                    viewModel.user.collectLatest { resource ->
//                        when (resource) {
//                            is Resource.Success -> {
//                                if (user == resource.data) {
//                                    Log.e("UserEntity가 존재한다면?", "서버와 동일한 데이터")
//                                } else {
//                                    Log.e("UserEntity가 존재한다면?", "서버와 다른 데이터")
//                                    user = resource.data!!
//                                    viewModel.updateUser(user.toEntity())
//                                }
//                            }
//                            is Resource.Error -> {}
//                            else -> Unit
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun navigateToHomeActivity() {
//        val intent = Intent(this, HomeActivity::class.java).addFlags(
//            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        )
//        startActivity(intent)
//    }
//
//    companion object {
//        var currentUser: UserEntity? = null
//
//        fun createLaunchIntent(
//            context: Context,
//            messageId: String,
//            parentMessageId: String?,
//            channelType: String,
//            channelId: String,
//        ) = Intent(context, SplashActivity::class.java).apply {
//            putExtra(EXTRA_CHANNEL_ID, channelId)
//            putExtra(EXTRA_CHANNEL_TYPE, channelType)
//            putExtra(EXTRA_MESSAGE_ID, messageId)
//            putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
//        }
//    }
//}
private val TAG = "SplashActivity"

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1000
    private var client: ChatClient? = BusanPartners.chatClient
    lateinit var user: com.kwonminseok.busanpartners.data.User
    private val viewModel: UserViewModel by viewModels()
    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
    private val uid = BusanPartners.preferences.getString("uid", "")
    private var retryCount = 0
    private val maxRetries = 3
    private val timeoutDuration = 15000L // 15 seconds

    init {
        lifecycleScope.launch {
            TimeRepository.fetchCurrentTime()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        Log.e("Splash 화면이", "시작되었습니다.")

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            Log.e("firebaseUser가", "null이다.")
            navigateToLoginRegisterActivity()
            return
        }

        fetchCurrentUserEntity()
        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setupUserStream()
            } else {
                Log.e("자동 로그인을 할 때", "task가 실패")
                handleRetryOrNavigate("토큰을 가져오는 중 오류가 발생했습니다.")
            }
        }?.addOnFailureListener {
            Log.e("자동 로그인을 할 때", "토큰 얻는 것 자체를 실패함.")
            handleRetryOrNavigate("토큰을 가져오는 중 오류가 발생했습니다.")
        }

        // 타임아웃 설정
        lifecycleScope.launch {
            delay(timeoutDuration)
            if (!isFinishing) {
                Toast.makeText(this@SplashActivity, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                navigateToLoginRegisterActivity()
            }
        }
    }

    private fun navigateToLoginRegisterActivity() {
        val intent = Intent(this, LoginRegisterActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        startActivity(intent)
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
                    Log.e(TAG, "토큰 호출을 실패했습니다.", exception)
                    continuation.resumeWithException(exception)
                }
            }
    }

    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)

        if (currentServerTimeToDateTime != null && currentServerTimeToDateTime <= tokenTimeToDateTime) {
            val myUser = User.Builder()
                .withId(user.uid)
                .withName(user.name?.ko ?: "ㅁㄴㅇ")
                .withImage(user.imagePath)
                .build()
            Log.e("myUser", myUser.toString())

            if (token.isEmpty()) {
                Log.e(TAG, "token이 비어있을 때.")
                lifecycleScope.launch {
                    try {
                        getNewToken()
                        connectClient(myUser)
                    } catch (e: Exception) {
                        Log.e(TAG, "토큰을 가져오는 중 오류가 발생했습니다.", e)
                        handleRetryOrNavigate("토큰을 가져오는 중 오류가 발생했습니다.")
                    }
                }
            } else {
                connectClient(myUser)
            }
        } else {
            val guestUser = User(
                id = "guestID",
                name = "guestID",
                image = "https://bit.ly/2TIt8NR"
            )

            client?.let { chatClient ->
                chatClient.connectUser(
                    guestUser,
                    BuildConfig.GUEST_ID_TOKEN
                ).enqueue { result ->
                    if (result.isSuccess) {
                        navigateToHomeActivity()
                    } else {
                        Log.e(TAG, "게스트 유저 연결 실패: ${result.errorOrNull()}")
                        handleRetryOrNavigate("게스트 유저 연결 실패.")
                    }
                }
            }
        }
    }

    private fun connectClient(myUser: User) {
        val tokenProvider = object : TokenProvider {
            override fun loadToken(): String = BusanPartners.preferences.getString(Constants.TOKEN, "")
        }

        client?.let { chatClient ->
            chatClient.connectUser(myUser, tokenProvider).enqueue { result ->
                if (result.isSuccess) {
                    navigateToHomeActivity()
                } else {
                    Log.e("connectUser", "Failed to connect user: ${result.errorOrNull()}")
                    handleRetryOrNavigate("유저 연결 실패.")
                }
            }
        }
    }

    private fun handleRetryOrNavigate(errorMessage: String) {
        if (retryCount < maxRetries) {
            retryCount++
            Log.e("Retry", "Retrying to connect... attempt $retryCount")
            Toast.makeText(this, errorMessage + " 재시도 중...", Toast.LENGTH_SHORT).show()
            setupUserStream()
        } else {
            Log.e("Retry", "Max retries reached, navigating to login")
            Toast.makeText(this, "최대 재시도 횟수에 도달했습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            navigateToLoginRegisterActivity()
        }
    }

    private fun setupUserStream() {
        lifecycleScope.launch {
            try {
                TimeRepository.fetchCurrentTime()
                currentServerTime = TimeRepository.currentTime?.datetime
                BusanPartners.preferences.setString("uid", user.uid)
                connectUserToStream(user)
            } catch (e: Exception) {
                Log.e("setupUserStream", "Exception: ${e.message}")
                handleRetryOrNavigate("사용자 스트림 설정 중 오류가 발생했습니다.")
            }
        }
    }

    private fun fetchCurrentUserEntity() {
        viewModel.getUserStateFlowData(uid).observe(this) { userEntity ->
            if (userEntity == null) {
                viewModel.getCurrentUser()
                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest { resource ->
                        when (resource) {
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                user = resource.data!!
                                viewModel.insertUser(user.toEntity())
                                currentUser = user.toEntity()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    this@SplashActivity,
                                    resource.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> Unit
                        }
                    }
                }
            } else {
                currentUser = userEntity
                user = userEntity.toUser()
                viewModel.getCurrentUser()
                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                if (user == resource.data) {
                                    Log.e("UserEntity가 존재한다면?", "서버와 동일한 데이터")
                                } else {
                                    Log.e("UserEntity가 존재한다면?", "서버와 다른 데이터")
                                    user = resource.data!!
                                    viewModel.updateUser(user.toEntity())
                                }
                            }
                            is Resource.Error -> {}
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        startActivity(intent)
    }

    companion object {
        var currentUser: UserEntity? = null

        fun createLaunchIntent(
            context: Context,
            messageId: String,
            parentMessageId: String?,
            channelType: String,
            channelId: String,
        ) = Intent(context, SplashActivity::class.java).apply {
            putExtra(EXTRA_CHANNEL_ID, channelId)
            putExtra(EXTRA_CHANNEL_TYPE, channelType)
            putExtra(EXTRA_MESSAGE_ID, messageId)
            putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
        }
    }
}
