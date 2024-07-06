package com.kwonminseok.busanpartners.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.barnea.dialoger.Dialoger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
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
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//private val TAG = "SplashActivity"
//
//@SuppressLint("CustomSplashScreen")
//@AndroidEntryPoint
//class SplashActivity : AppCompatActivity() {
//
//    private val PERMISSION_REQUEST_CODE = 1000
//    private var client: ChatClient? = BusanPartners.chatClient
//    lateinit var user: com.kwonminseok.busanpartners.data.User
//    private val viewModel: UserViewModel by viewModels()
//    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
//    private val uid = BusanPartners.preferences.getString("uid", "")
//    private var retryCount = 0
//    private val maxRetries = 3
//    private val timeoutDuration = 15000L // 15 seconds
//
//    init {
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        supportActionBar?.hide()
//        setContentView(R.layout.activity_splash)
//        Log.e("Splash 화면이", "시작되었습니다.")
//
//        if (!isNetworkAvailable(this)) {
//            showNetworkErrorAndExit()
//            return
//        }
//
//        lifecycleScope.launch {
//            TimeRepository.fetchCurrentTime()
//        }
//
//
//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        if (firebaseUser == null) {
//            Log.e("firebaseUser가", "null이다.")
//            navigateToLoginRegisterActivity()
//            return
//        }
//
//        fetchCurrentUserEntity()
//        firebaseUser.getIdToken(true).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                setupUserStream()
//            } else {
//                Log.e("자동 로그인을 할 때", "task가 실패")
//                handleRetryOrNavigate("토큰을 가져오는 중 오류가 발생했습니다.")
//            }
//        }.addOnFailureListener {
//            Log.e("자동 로그인을 할 때", "토큰 얻는 것 자체를 실패함.")
//            handleRetryOrNavigate("토큰을 가져오는 중 오류가 발생했습니다.")
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
//                .withName(user.name?.ko ?: "")
//                .withImage(user.imagePath)
//                .build()
//            Log.e("myUser", myUser.toString())
//
//            if (token.isEmpty()) {
//                Log.e(TAG, "token이 비어있을 때.")
//                lifecycleScope.launch {
//                    try {
//                        getNewToken()
//                        connectClient(myUser)
//                    } catch (e: Exception) {
//                        Log.e(TAG, "토큰을 가져오는 중 오류가 발생했습니다.", e)
//                        handleRetryOrNavigate("토큰을 가져오는 중 오류가 발생했습니다.")
//                    }
//                }
//            } else {
//                connectClient(myUser)
//            }
//        } else { // 토큰 타임이 지났거나 처음 방문하는 경우
//            val guestUser = User(
//                id = "guestID",
//                name = "guestID",
//                image = "https://bit.ly/2TIt8NR"
//            )
//
//            //여기서 만약에 entity에 traveler가 true로 되어 있다면? -> false로 만들고 이미지 없애고, complete 지우고
//            if (user.authentication.traveler) {
//                val newData = mapOf(
//                    "authentication.traveler" to false,
//                    "authentication.authenticationStatus" to "expire",
//                    "authentication.travelerAuthenticationImage" to FieldValue.delete()
//                )
//                viewModel.setCurrentUser(newData)
//
//                val updatedAuthentication = user.authentication.copy(
//                    traveler = false,
//                    authenticationStatus = "expire",
//                    travelerAuthenticationImage = null
//                )
//
//                val updatedUser = user.copy(
//                    authentication = updatedAuthentication
//                )
//                viewModel.updateUser(updatedUser.toEntity())
//
//
//            }
//
//
//
//            client?.let { chatClient ->
//                chatClient.connectUser(
//                    guestUser,
//                    BuildConfig.GUEST_ID_TOKEN
//                ).enqueue { result ->
//                    if (result.isSuccess) {
//                        navigateToHomeActivity()
//                    } else {
//                        Log.e(TAG, "게스트 유저 연결 실패: ${result.errorOrNull()}")
//                        handleRetryOrNavigate("게스트 유저 연결 실패.")
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
//                    handleRetryOrNavigate("유저 연결 실패.")
//                }
//            }
//        }
//    }
//
//    private fun handleRetryOrNavigate(errorMessage: String) {
//        if (retryCount < maxRetries) {
//            retryCount++
//            Log.e("Retry", "Retrying to connect... attempt $retryCount")
//            Toast.makeText(this, errorMessage + " 재시도 중...", Toast.LENGTH_SHORT).show()
//            setupUserStream()
//        } else {
//            Log.e("Retry", "Max retries reached, navigating to login")
//            Toast.makeText(this, "최대 재시도 횟수에 도달했습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
//            navigateToLoginRegisterActivity()
//        }
//    }
//
//    private fun setupUserStream() {
//        lifecycleScope.launch {
//            try {
//                TimeRepository.fetchCurrentTime()
//                currentServerTime = TimeRepository.currentTime?.datetime
//                Log.e("currentServerTime", currentServerTime ?: "2021-04-09T12:38:11.818609+09:00")
//                BusanPartners.preferences.setString("uid", user.uid)
//                connectUserToStream(user)
//            } catch (e: Exception) {
//                Log.e("setupUserStream", "Exception: ${e.message}")
//                handleRetryOrNavigate("사용자 스트림 설정 중 오류가 발생했습니다.")
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
//                Log.e("currentUser?", currentUser.toString())
//
//                user = currentUser!!.toUser()
//                viewModel.getCurrentUser()
//                lifecycleScope.launchWhenStarted {
//                    viewModel.user.collectLatest { resource ->
//                        when (resource) {
//                            is Resource.Success -> {
//                                if (user == resource.data) {
//                                    Log.e("UserEntity가 존재한다면?", "서버와 동일한 데이터")
//                                    Log.e("UserEntity가 user?", user.toString())
//                                    return@collectLatest
//                                } else {
//                                    Log.e("UserEntity가 존재한다면?", "서버와 다른 데이터")
//                                    user = resource.data!!
//                                    currentUser = user.toEntity()
//
//                                    viewModel.updateUser(currentUser!!)
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
//        var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
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
//
//    private fun showNetworkErrorAndExit() {
//        Dialoger(this, Dialoger.TYPE_MESSAGE)
//            .setTitle("네트워크 오류")
//            .setDescription("인터넷 연결이 없습니다. 앱을 종료합니다.")
//            .setProgressBarColor(R.color.black)
//            .show()
//            .setButtonText("확인")
//            .setButtonOnClickListener {
//                finish()
//            }
//
////        AlertDialog.Builder(this)
////            .setTitle("네트워크 오류")
////            .setMessage("인터넷 연결이 없습니다. 앱을 종료합니다.")
////            .setPositiveButton("확인") { dialog, _ ->
////                dialog.dismiss()
////                finish()
////            }
////            .setCancelable(false)
////            .show()
//    }
//
//    fun isNetworkAvailable(context: Context): Boolean {
//        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val network = connectivityManager.activeNetwork ?: return false
//            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
//            return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//        } else {
//            val networkInfo = connectivityManager.activeNetworkInfo
//            return networkInfo != null && networkInfo.isConnected
//        }
//    }
//
//}

private val TAG = "SplashActivity"

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1000
    private var client: ChatClient? = BusanPartners.chatClient
    lateinit var user: com.kwonminseok.busanpartners.data.User
    private val viewModel: UserViewModel by viewModels()
    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
    private val uid = BusanPartners.preferences.getString("uid", "")
    private var retryCount = 0
    private val maxRetries = 3
    private val timeoutDuration = 15000L // 15 seconds
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var deviceToken: String

    init {

    }
    // TODO 매번 인증하는 것은 문제가 있으니까 그냥 false true로 만들어서 빠르게 넘길 수 있도록 하기.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        Log.e("Splash 화면이", "시작되었습니다.")
        sharedPreferences = this.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        // 언어 설정 적용


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MyToken", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            deviceToken = task.result
            // Log and toast
        })


        if (!isNetworkAvailable(this)) { // 네트워크가 끊겨있을 경우
            showNetworkErrorAndExit()
            return
        }

        lifecycleScope.launch {
            TimeRepository.fetchCurrentTime()
        }


        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            Log.e("firebaseUser가", "null이다.")
            navigateToLoginRegisterActivity()
            return
        }

        fetchCurrentUserEntity()
        firebaseUser.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setupUserStream()
            } else {
                Log.e("자동 로그인을 할 때", "task가 실패")
                handleRetryOrNavigate(getString(R.string.token_fetch_error))
            }
        }.addOnFailureListener {
            Log.e("자동 로그인을 할 때", "토큰 얻는 것 자체를 실패함.")
            handleRetryOrNavigate(getString(R.string.token_fetch_error))
        }

        // 타임아웃 설정
        lifecycleScope.launch {
            delay(timeoutDuration)
            if (!isFinishing) {
                Toast.makeText(this@SplashActivity, getString(R.string.relogin_prompt), Toast.LENGTH_SHORT).show()
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
        if (user.authentication.collegeStudent) {
            lifecycleScope.launch {
                val myUser = User.Builder()
                    .withId(user.uid)
                    .withName(user.name?.ko ?: "")
                    .withImage(user.imagePath)
                    .build()
                Log.e("myUser", myUser.toString())

                if (token.isEmpty()) {
                    Log.e(TAG, "token이 비어있을 때.")
                    try {
                        token = getNewToken()
                        connectClient(myUser)
                    } catch (e: Exception) {
                        Log.e(TAG, "토큰을 가져오는 중 오류가 발생했습니다.", e)
                        handleRetryOrNavigate(getString(R.string.token_fetch_error))
                    }
                } else {
                    val isFirstVisit = sharedPreferences.getBoolean("is_first_visit", true)

                    if (isFirstVisit) {
                        // 다이얼로그를 보여줍니다.
                        if (user.authentication.traveler) { // 관광객일 때
                            sharedPreferences.edit().putBoolean("is_first_visitor", true).apply()
                        }
                        if (user.authentication.collegeStudent) { // 대학생일때
                            sharedPreferences.edit().putBoolean("is_first_student", true).apply()
                        }
                        // 지금부터는
                        sharedPreferences.edit().putBoolean("is_first_visit", false).apply()
                    }

                    // 만약 처음이다. 그러면 dialog 하나 넣기
                    connectClient(myUser)
                }
            }
            return // 대학생일 경우 바로 종료
        }

        if (currentServerTime == null) { // 만약 서버로부터 시간을 받지 못했을 경우.
            val temporaryTime = getCachedServerTime() ?: DEFAULT_SERVER_TIME

            val clientTimeToDateTime: OffsetDateTime = OffsetDateTime.now() // 클라이언트 현재 시간

            val cachedServerTimeToDateTime: OffsetDateTime? = try {
                OffsetDateTime.parse(temporaryTime)
            } catch (e: DateTimeParseException) {
                null
            }

            val effectiveTimeToDateTime: OffsetDateTime = when { // 어쩔 수 없지만 멋대로 시간 조작하면 그 잘못은 이사람에게.. 이게 맞는 것 같다.
                cachedServerTimeToDateTime == null -> clientTimeToDateTime
                clientTimeToDateTime.isAfter(cachedServerTimeToDateTime) -> clientTimeToDateTime
                else -> cachedServerTimeToDateTime
            }
            currentServerTime = effectiveTimeToDateTime.toString()

        }


        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)

        lifecycleScope.launch { // 실제 계정으로 접속
            if (currentServerTimeToDateTime != null) {
                if (currentServerTimeToDateTime <= tokenTimeToDateTime) {
                    val myUser = User.Builder()
                        .withId(user.uid)
                        .withName(user.name?.ko ?: "")
                        .withImage(user.imagePath)
                        .build()
                    Log.e("myUser", myUser.toString())

                    if (token.isEmpty()) {
                        Log.e(TAG, "token이 비어있을 때.")
                        try {
                            token = getNewToken()
                            connectClient(myUser)
                        } catch (e: Exception) {
                            Log.e(TAG, "토큰을 가져오는 중 오류가 발생했습니다.", e)
                            handleRetryOrNavigate(getString(R.string.token_fetch_error))
                        }
                    } else {

                        val isFirstVisit = sharedPreferences.getBoolean("is_first_visit", true)

                        if (isFirstVisit) {
                            // 다이얼로그를 보여줍니다.
                            if (user.authentication.traveler) { // 관광객일 때
                                sharedPreferences.edit().putBoolean("is_first_visitor", true).apply()
                            }
                            if (user.authentication.collegeStudent) { // 대학생일때
                                sharedPreferences.edit().putBoolean("is_first_student", true).apply()
                            }
                            // 지금부터는
                            sharedPreferences.edit().putBoolean("is_first_visit", false).apply()
                        }

                        // 만약 처음이다. 그러면 dialog 하나 넣기
                        connectClient(myUser)
                    }
                } else { // 토큰 타임이 지났거나 처음 방문하는 경우
                    val guestUser = User(
                        id = "guestID",
                        name = "guestID",
                        image = "https://bit.ly/2TIt8NR"
                    )

                    //여기서 만약에 entity에 traveler가 true로 되어 있다면? -> false로 만들고 이미지 없애고, complete 지우고
                    if (user.authentication.traveler) {
                        val newData = mapOf(
                            "authentication.traveler" to false,
                            "authentication.authenticationStatus" to "expire",
                            "authentication.travelerAuthenticationImage" to FieldValue.delete(),
                            "blockList" to mutableListOf<String>(),
                            "chatChannelCount" to 0,
                            "tokenTime" to "2000-01-01T12:38:11.818609+09:00"
                        )
                        viewModel.setCurrentUser(newData)

                        val updatedAuthentication = user.authentication.copy(
                            traveler = false,
                            authenticationStatus = "expire",
                            travelerAuthenticationImage = null
                        )

                        val updatedUser = user.copy(
                            blockList = mutableListOf(),
                            authentication = updatedAuthentication,
                            chatChannelCount = 0
                        )
                        viewModel.updateUser(updatedUser.toEntity())
                        sharedPreferences.edit().putBoolean("traveler_finish", true).apply()

                    }

                    client?.let { chatClient ->
                        chatClient.connectUser(
                            guestUser,
                            BuildConfig.GUEST_ID_TOKEN
                        ).enqueue { result ->
                            if (result.isSuccess) {
                                navigateToHomeActivity()
                            } else {
                                Log.e(TAG, "게스트 유저 연결 실패: ${result.errorOrNull()}")
                                handleRetryOrNavigate(getString(R.string.guest_user_connect_failed, result.errorOrNull().toString()))
                            }
                        }
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
                    handleRetryOrNavigate(getString(R.string.user_connect_failed, result.errorOrNull().toString()))
                }
            }
        }
    }

    private fun handleRetryOrNavigate(errorMessage: String) {
        if (retryCount < maxRetries) {
            retryCount++
            Log.e("Retry", "Retrying to connect... attempt $retryCount")
//            Toast.makeText(this, errorMessage + " 재시도 중...", Toast.LENGTH_SHORT).show()
            setupUserStream()
        } else {
            Log.e("Retry", "Max retries reached, navigating to login")
            Toast.makeText(this, getString(R.string.max_retries_reached), Toast.LENGTH_SHORT).show()
            navigateToLoginRegisterActivity()
        }
    }

    private fun setupUserStream() {
        lifecycleScope.launch {
            try {
                TimeRepository.fetchCurrentTime()
                currentServerTime = TimeRepository.currentTime?.datetime
                if (currentServerTime != null) {
                    saveServerTime(currentServerTime!!)
                }
//                else {
//                    currentServerTime = getCachedServerTime() ?: DEFAULT_SERVER_TIME
//                }


                BusanPartners.preferences.setString("uid", user.uid)
                connectUserToStream(user)
            } catch (e: Exception) {
                Log.e("setupUserStream", "Exception: ${e.message}")
                currentServerTime = getCachedServerTime() ?: DEFAULT_SERVER_TIME
                handleRetryOrNavigate(getString(R.string.user_stream_setup_error))
            }
        }
    }

    private fun fetchCurrentUserEntity() {
        viewModel.getUserStateFlowData(uid).observe(this) { userEntity ->
            if (userEntity == null) { //
                val newData = mapOf(
                    "deviceToken" to deviceToken,
                )
                viewModel.setCurrentUser(newData)

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
                Log.e("currentUser?", currentUser.toString())
                user = currentUser!!.toUser()
                if (user.reset) { // 시작하자마자 reset을 false로 만들기
                    val newData = mapOf(
                        "reset" to false
                    )
                    viewModel.setCurrentUser(newData)

                }
                if (user.deviceToken != deviceToken) {
                    val newData = mapOf(
                        "deviceToken" to deviceToken
                    )
                    viewModel.setCurrentUser(newData)
                }

                viewModel.getCurrentUser()
                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                if (user == resource.data) {
                                    Log.e("UserEntity가 존재한다면?", "서버와 동일한 데이터")
                                    Log.e("UserEntity가 user?", user.toString())
                                    return@collectLatest
                                } else {
                                    Log.e("UserEntity가 존재한다면?", "서버와 다른 데이터")
                                    user = resource.data!!
                                    currentUser = user.toEntity()

                                    viewModel.updateUser(currentUser!!)
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
    //"2021-04-09T12:38:11.818609+09:00"
    companion object {
        var currentUser: UserEntity? = null
        var currentServerTime: String? = null
        const val DEFAULT_SERVER_TIME = "2021-04-09T12:38:11.818609+09:00"

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

    private fun showNetworkErrorAndExit() {
        Dialoger(this, Dialoger.TYPE_MESSAGE)
            .setTitle(getString(R.string.network_error))
            .setDescription(getString(R.string.no_internet_connection))

            .setProgressBarColor(R.color.black)
            .show()
            .setButtonText(getString(R.string.confirmation))
            .setButtonOnClickListener {
                finish()
            }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun saveServerTime(serverTime: String) {
        sharedPreferences.edit().putString("last_server_time", serverTime).apply()
    }


    private fun getCachedServerTime(): String? {
        return sharedPreferences.getString("last_server_time", null)
    }




}

