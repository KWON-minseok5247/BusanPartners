package com.kwonminseok.busanpartners.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.repository.TimeRepository
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


private val TAG = "SplashActivity"

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    //TODO 여기서 위치 정보를 무조건 받아야 한다.
    // 만약 못받으면 그 위치를 켜야 한다는 화면으로 넘어가는 게 좋음..

    private var client: ChatClient? = BusanPartners.chatClient
    lateinit var user: com.kwonminseok.busanpartners.data.User
    private val viewModel: UserViewModel by viewModels()
    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"

    // getStream 채팅 토큰
    // 토큰 절차 1: 일단 token이 있는지 없는지 확인, 있으면 바로 가져온다.
    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")

    init {
        //TODo 언젠가 이것도 수정을 할 필요가 있다.
        lifecycleScope.launch {
            TimeRepository.fetchCurrentTime()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        // Create a Handler


        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            navigateToLoginRegisterActivity()
        }

        // 구글 자동 로그인 과정
        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                Log.d(TAG, "아이디 토큰 = $idToken")

                setupUserStream()

//                viewModel.getCurrentUser()
//
//
//                // 여기서 파이어베이스 정보를 받고 user를 정의한다.
//                lifecycleScope.launchWhenStarted {
//                    viewModel.user.collectLatest {
//                        when (it) {
//                            is Resource.Success -> {
//                                user = it.data!!
//                                BusanPartners.preferences.setString("uid", it.data.uid)
//                                connectUserToStream(user)
//                            }
//
//                            else -> Unit
//                        }
//                    }
//                }
            } else {
                navigateToLoginRegisterActivity()
            }
        }?.addOnFailureListener {
            Log.e(TAG, it.message.toString())
            navigateToLoginRegisterActivity()
        }
    }

    private fun navigateToLoginRegisterActivity() {
        val intent =
            Intent(this, LoginRegisterActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        startActivity(intent)
    }


    private suspend fun getNewToken(): String = suspendCoroutine { continuation ->
        val functions = FirebaseFunctions.getInstance("asia-northeast3")
        functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
            .call()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 함수 호출이 성공했습니다. 반환된 데이터에서 토큰을 추출합니다.
                    val token = task.result?.data as String
                    BusanPartners.preferences.setString(Constants.TOKEN, token)
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


    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
//        currentServerTime = TimeRepository.currentTime?.datetime
        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
        Log.e("currentServer", currentServerTimeToDateTime.toString())
        Log.e("user.tokenTime", user.tokenTime.toString())
        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)
        Log.e("tokenTimeToDateTime", tokenTimeToDateTime.toString())


        // 토큰 기간. 정상적으로 채팅이 가능한 시기
        if (currentServerTimeToDateTime != null) {
            if (currentServerTimeToDateTime <= tokenTimeToDateTime) {
                // 채팅이 사라지는 이유로 의심할 수 있겠다.  unreadCount 등 추가를 하지 않았다면 0으로 인식을 할 거니까.

                val myUser = User.Builder()
                    .withId(user.uid)
                    .build()
                Log.e("myUser", myUser.toString())

//                val myUser = User(
//                    id = user.uid,
//                    name = user.name!!,
//                    image = user.imagePath,
//                )

                if (token == "") {
                    Log.e(TAG, "token이 비어있을 때.")
                    lifecycleScope.launch {
                        getNewToken()
                        if (myUser != null) {
                            connectClient(myUser)
                        }
                    }
                } else {
                    if (myUser != null) {
                        connectClient(myUser)
                    }
                }

            } else { // 인증이 되지 않았거나 토큰이 만료가 된 경우 게스트 모드로 로그인 해두기

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
                        // 비동기 작업 결과 처리
                        // 프래그먼트의 뷰가 생성된 상태인지 확인

                        val intent =
                            Intent(this, HomeActivity::class.java).addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                        startActivity(intent)


                    }
                }
            }
        }
    }


    private fun connectClient(myUser: User) {

        val tokenProvider = object : TokenProvider {
            // Make a request to your backend to generate a valid token for the user
            override fun loadToken(): String =
                BusanPartners.preferences.getString(Constants.TOKEN, "")
        }

        client?.let { chatClient ->
            chatClient.connectUser(
                user = myUser,
                tokenProvider
            ).enqueue { result ->
                // 비동기 작업 결과 처리
                if (result.isSuccess) {
                    val user = result.getOrNull()?.user
                    Log.e("user?.unreadChannels", user?.unreadChannels.toString())
                    Log.e("user?.totalUnreadCount", user?.totalUnreadCount.toString())
                    // Result contains the list of channel mutes
                    val mutes: List<ChannelMute>? = user?.channelMutes
                }

                val intent =
                    Intent(this, HomeActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                startActivity(intent)


            }
        }




    }

    private fun setupUserStream() {
        lifecycleScope.launch {
            // 서버 시간 먼저 가져오기
            TimeRepository.fetchCurrentTime()
            currentServerTime = TimeRepository.currentTime?.datetime
            viewModel.getCurrentUser()

            // 유저 정보 스트림 수집 시작
            viewModel.user.collectLatest { userResource ->
                when (userResource) {
                    is Resource.Success -> {
                        user = userResource.data!!
                        BusanPartners.preferences.setString("uid", user.uid)
                        connectUserToStream(user)
                    }

                    is Resource.Error -> {
                        Log.e(TAG, "User data fetch error: ${userResource.message}")
                        val intent =
                            Intent(this@SplashActivity, LoginRegisterActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        startActivity(intent)
                    }

                    else -> Log.d(TAG, "Loading user data")
                }
            }
        }
    }

//    private fun fetchServerTime() {
//        lifecycleScope.launch {
//            TimeRepository.fetchCurrentTime()
//            currentServerTime = TimeRepository.currentTime?.datetime
//            val currentServerTimeToDateTime: OffsetDateTime? =
//                OffsetDateTime.parse(currentServerTime)
//            Log.e("currentServer", currentServerTimeToDateTime.toString())
//            Log.e("user.tokenTime", user.tokenTime.toString())
//            val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)
//            Log.e("tokenTimeToDateTime", tokenTimeToDateTime.toString())
//
//            serverTime.value = TimeRepository.fetchCurrentTime()?.let {
//                OffsetDateTime.parse(it.datetime)
//            }
//        }
//    }


}
