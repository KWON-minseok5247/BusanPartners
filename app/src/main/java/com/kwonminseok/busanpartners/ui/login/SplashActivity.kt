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
import com.kwonminseok.busanpartners.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.repository.TimeRepository
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.token.TokenProvider
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

    private var client: ChatClient? = BusanPartners.chatClient
    lateinit var user: com.kwonminseok.busanpartners.data.User
    private val viewModel: UserViewModel by viewModels()
    private lateinit var currentServerTime: String



    // getStream 채팅 토큰
    // 토큰 절차 1: 일단 token이 있는지 없는지 확인, 있으면 바로 가져온다.
    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        // Create a Handler

        lifecycleScope.launch {
            TimeRepository.fetchCurrentTime()
            currentServerTime = TimeRepository.currentTime!!.datetime
        }


        // 자동 로그인 과정 -> 실제로는 splash 스크린에서 사용해야 할 것!
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                Log.d(TAG, "아이디 토큰 = $idToken")
                viewModel.getCurrentUser()


            // 여기서 파이어베이스 정보를 받고 user를 정의한다.
                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest {
                        when (it) {
                            is Resource.Loading -> {

                            }

                            is Resource.Success -> {
                                user = it.data!!
                                BusanPartners.preferences.setString("uid", it.data.uid)
                                connectUserToStream(user)
                            }

                            is Resource.Error -> {

                            }

                            else -> Unit
                        }
                    }
                }
            }
        }?.addOnFailureListener {
            val intent =
                Intent(this, LoginRegisterActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            startActivity(intent)
        }
    }

    private suspend fun getNewToken(): String = suspendCoroutine {  continuation ->
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
                    Log.e(com.kwonminseok.busanpartners.ui.TAG, "토큰 호출을 실패했습니다.")
                    continuation.resumeWithException(
                        task.exception ?: RuntimeException("Unknown token fetch error")
                    )
                }
            }
    }


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
                Log.e(com.kwonminseok.busanpartners.ui.TAG, "token이 비어있을 때.")
                lifecycleScope.launch {
                    getNewToken()
                    connectClient(myUser)
                }
            } else {
                connectClient(myUser)
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
