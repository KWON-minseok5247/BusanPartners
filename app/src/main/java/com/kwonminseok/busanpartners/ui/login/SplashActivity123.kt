//package com.kwonminseok.busanpartners.ui.login
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.functions.FirebaseFunctions
//import com.kwonminseok.busanpartners.BuildConfig
//import com.kwonminseok.busanpartners.R
//import com.kwonminseok.busanpartners.application.BusanPartners
//import com.kwonminseok.busanpartners.db.entity.UserEntity
//import com.kwonminseok.busanpartners.extensions.toEntity
//import com.kwonminseok.busanpartners.extensions.toUser
//import com.kwonminseok.busanpartners.repository.TimeRepository
//import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_ID
//import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_TYPE
//import com.kwonminseok.busanpartners.ui.EXTRA_MESSAGE_ID
//import com.kwonminseok.busanpartners.ui.EXTRA_PARENT_MESSAGE_ID
//import com.kwonminseok.busanpartners.ui.HomeActivity
//import com.kwonminseok.busanpartners.ui.message.ChannelActivity
//import com.kwonminseok.busanpartners.util.Constants
//import com.kwonminseok.busanpartners.util.Resource
//import com.kwonminseok.busanpartners.viewmodel.UserViewModel
//import dagger.hilt.android.AndroidEntryPoint
//import io.getstream.chat.android.client.ChatClient
//import io.getstream.chat.android.client.token.TokenProvider
//import io.getstream.chat.android.models.User
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import org.threeten.bp.OffsetDateTime
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//import kotlin.coroutines.suspendCoroutine
//
//@SuppressLint("CustomSplashScreen")
//@AndroidEntryPoint
//class SplashActivity123 : AppCompatActivity() {
//
//    private val PERMISSION_REQUEST_CODE = 1000
//    //TODO 여기서 위치 정보를 무조건 받아야 한다.
//    // 만약 못받으면 그 위치를 켜야 한다는 화면으로 넘어가는 게 좋음..
//
//    private var client: ChatClient? = BusanPartners.chatClient
//    lateinit var user: com.kwonminseok.busanpartners.data.User
//    private val viewModel: UserViewModel by viewModels()
//    private var currentServerTime: String? = "2021-04-09T12:38:11.818609+09:00"
//
//    // getStream 채팅 토큰
//    // 토큰 절차 1: 일단 token이 있는지 없는지 확인, 있으면 바로 가져온다.
//    private var token: String = BusanPartners.preferences.getString(Constants.TOKEN, "")
//    // 초반에 userEntity를 고정을 시켜서 언제든지 불러올 수있도록 여기서 정한다.
//
//    private val uid = BusanPartners.preferences.getString("uid", "")
//
//    init {
//        //TODo 언젠가 이것도 수정을 할 필요가 있다.
//        lifecycleScope.launch {
//            TimeRepository.fetchCurrentTime()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        supportActionBar?.hide()
//        setContentView(R.layout.activity_splash)
//        // Create a Handler
//        Log.e("Splash 화면이", "시작되었습니다.")
//
//
//        // 일단 먼저
//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        if (firebaseUser == null) {
//            Log.e("firebaseUser가", "null이다.")
//            navigateToLoginRegisterActivity()
//            return
//        }
//
//        // UserEntity를 가져오는 부분
//        fetchCurrentUserEntity()
//
//
//        // 구글 자동 로그인 과정
//        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val idToken = task.result.token
//
////                requestNotificationPermission()
//                setupUserStream()
//            } else {
//                // TODO 여기서 로그아웃처럼 각종 데이터 초기화하는 과정이 필요할 수 있겠다.
//                Log.e("자동 로그인을 할 때", "task가 실패")
//
//                navigateToLoginRegisterActivity()
//            }
//        }?.addOnFailureListener {
//            Log.e("자동 로그인을 할 때", "토큰 얻는 것 자체를 실패함.")
//
//            navigateToLoginRegisterActivity()
//        }
//    }
//
//    private fun navigateToLoginRegisterActivity() {
//        val intent =
//            Intent(this, LoginRegisterActivity::class.java).addFlags(
//                Intent.FLAG_ACTIVITY_NEW_TASK or
//                        Intent.FLAG_ACTIVITY_CLEAR_TASK
//            )
//        startActivity(intent)
//    }
//
//
//    private suspend fun getNewToken(): String = suspendCoroutine { continuation ->
//        val functions = FirebaseFunctions.getInstance("asia-northeast3")
//        functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
//            .call()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // 함수 호출이 성공했습니다. 반환된 데이터에서 토큰을 추출합니다.
//                    val token = task.result?.data as String
//                    BusanPartners.preferences.setString(Constants.TOKEN, token)
//                    continuation.resume(token) // 코루틴을 재개하고 결과를 반환합니다.
//                } else {
//                    // 호출 실패. 에러를 처리합니다.
////                    Log.e(TAG, "토큰 호출을 실패했습니다.")
////                    continuation.resumeWithException(
////                        task.exception ?: RuntimeException("Unknown token fetch error")
////                    )
//                    val exception = task.exception ?: RuntimeException("Unknown token fetch error")
//                    Log.e(TAG, "토큰 호출을 실패했습니다.", exception)
//                    continuation.resumeWithException(exception)
//
//                }
//            }
//    }
//
//
//    private fun connectUserToStream(user: com.kwonminseok.busanpartners.data.User) {
////        currentServerTime = TimeRepository.currentTime?.datetime
//        val currentServerTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(currentServerTime)
//        Log.e("currentServer", currentServerTimeToDateTime.toString())
//        Log.e("user.tokenTime", user.tokenTime.toString())
//        val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)
//        Log.e("tokenTimeToDateTime", tokenTimeToDateTime.toString())
//
//        // 토큰 기간. 정상적으로 채팅이 가능한 시기
//        if (currentServerTimeToDateTime != null) {
//            if (currentServerTimeToDateTime <= tokenTimeToDateTime) {
//                // 채팅이 사라지는 이유로 의심할 수 있겠다.  unreadCount 등 추가를 하지 않았다면 0으로 인식을 할 거니까.
//
//                val myUser = User.Builder()
//                    .withId(user.uid)
//                    .withName(user.name?.ko ?: "ㅁㄴㅇ")
//                    .withImage(user.imagePath)
//                    .build()
//                Log.e("myUser", myUser.toString())
//
////                val myUser = User(
////                    id = user.uid,
////                    name = user.name!!,
////                    image = user.imagePath,
////                )
//
//                if (token == "") {
//                    Log.e(TAG, "token이 비어있을 때.")
//                    lifecycleScope.launch {
//                        getNewToken()
//                        connectClient(myUser)
//                    }
//                } else {
//                    connectClient(myUser)
//
//                }
//
//            } else { // 인증이 되지 않았거나 토큰이 만료가 된 경우 게스트 모드로 로그인 해두기
//
//                val guestUser = User(
//                    id = "guestID",
//                    name = "guestID",
//                    image = "https://bit.ly/2TIt8NR"
//                )
//
//                client?.let { chatClient ->
//                    chatClient.connectUser(
//                        guestUser,
//                        BuildConfig.GUEST_ID_TOKEN
//                    ).enqueue { result ->
//                        // 비동기 작업 결과 처리
//                        // 프래그먼트의 뷰가 생성된 상태인지 확인
//                        //여기 알림 권한 먼저 요청 그 후
//                        val intent =
//                            Intent(this, HomeActivity::class.java).addFlags(
//                                Intent.FLAG_ACTIVITY_NEW_TASK or
//                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            )
//                        startActivity(intent)
//
//
//                    }
//                }
//            }
//        }
//    }
//
//    private fun connectClient(myUser: User) {
////        parseNotificationData()
//
//        val tokenProvider = object : TokenProvider {
//            // Make a request to your backend to generate a valid token for the user
//            override fun loadToken(): String =
//                BusanPartners.preferences.getString(Constants.TOKEN, "")
//        }
//        client?.let { chatClient ->
//            chatClient.connectUser(
//                user = myUser,
//                tokenProvider
//            ).enqueue { result ->
//
//
//                // 비동기 작업 결과 처리
//                if (result.isSuccess) {
//                    val user = result.getOrNull()?.user
//                    Log.e("user?.totalUnreadCount", user?.totalUnreadCount.toString())
//
//                }
//                val intent =
//                    Intent(this, HomeActivity::class.java).addFlags(
//                        Intent.FLAG_ACTIVITY_NEW_TASK or
//                                Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    )
//                startActivity(intent)
//
//
//            }
//        }
//
//
//    }
//
//    private fun setupUserStream() {
//        lifecycleScope.launch {
//            // 서버 시간 먼저 가져오기
//            TimeRepository.fetchCurrentTime()
//            currentServerTime = TimeRepository.currentTime?.datetime
//
//            BusanPartners.preferences.setString("uid", user.uid)
//            connectUserToStream(user)
//        }
//    }
//
//
//    //    private fun fetchServerTime() {
////        lifecycleScope.launch {
////            TimeRepository.fetchCurrentTime()
////            currentServerTime = TimeRepository.currentTime?.datetime
////            val currentServerTimeToDateTime: OffsetDateTime? =
////                OffsetDateTime.parse(currentServerTime)
////            Log.e("currentServer", currentServerTimeToDateTime.toString())
////            Log.e("user.tokenTime", user.tokenTime.toString())
////            val tokenTimeToDateTime: OffsetDateTime? = OffsetDateTime.parse(user.tokenTime)
////            Log.e("tokenTimeToDateTime", tokenTimeToDateTime.toString())
////
////            serverTime.value = TimeRepository.fetchCurrentTime()?.let {
////                OffsetDateTime.parse(it.datetime)
////            }
////        }
////    }
//    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
//
//    // 알림 권한 요청 다이얼로그 띄우기
//    private fun requestNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                // 알림 권한이 이미 허용된 경우
//                Log.e("알림이 ", "허용된 상태")
//                setupUserStream()
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // 알림 권한이 거부되었지만 다시 요청 가능한 경우
//                Log.e("알림이 ", "다시 요청 상태")
//
//                // 사용자에게 알림이 필요한 이유를 설명하고 권한 요청 다이얼로그 띄우기
//                showNotificationPermissionExplanationDialog()
//            } else {
//                // 알림 권한을 요청하는 다이얼로그 띄우기
//                Log.e("알림이 ", "처음 요청 상태")
//
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
//
//    private fun parseNotificationData() {
//        intent?.let {
//            if (it.hasExtra(EXTRA_CHANNEL_ID) && it.hasExtra(EXTRA_MESSAGE_ID) && it.hasExtra(
//                    EXTRA_CHANNEL_TYPE
//                )
//            ) {
//                val channelType = it.getStringExtra(EXTRA_CHANNEL_TYPE)
//                val channelId = it.getStringExtra(EXTRA_CHANNEL_ID)
//                val cid = "$channelType:$channelId"
//                val messageId = it.getStringExtra(EXTRA_MESSAGE_ID)
//                val parentMessageId = it.getStringExtra(EXTRA_PARENT_MESSAGE_ID)
//
//                intent = null
//
//                // 새로운 인텐트 생성
//                val intent = Intent(this, ChannelActivity::class.java).apply {
//                    putExtra("key:cid", cid)
//                    putExtra(EXTRA_MESSAGE_ID, messageId)
//                    putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
//                }
//                Log.e("startSplash", cid)
//                startActivity(intent)
//            } else return
//        }
//    }
//
//    // 권한 요청 결과 처리
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                // 알림 권한이 허용된 경우
//                setupUserStream()
//            } else {
//                // 알림 권한이 거부된 경우
//                // 사용자에게 다시 알림 권한이 필요한 이유를 설명하거나 추가 조치를 취해야 함
//                showNotificationPermissionDeniedDialog()
//            }
//        }
//
//    // 알림 권한이 허용된 경우 MainFragment로 이동
//    private fun proceedToMainFragment() {
//        val intent = Intent(this, HomeActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    // 사용자에게 알림 권한이 필요한 이유를 설명하고 권한 요청 다이얼로그 띄우기
//    private fun showNotificationPermissionExplanationDialog() {
//        // 다이얼로그를 통해 알림 권한이 필요한 이유를 설명하고, 사용자에게 권한 요청
//        // 사용자가 권한 요청을 수락하면 requestPermissionLauncher를 통해 결과를 처리함
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_NOTIFICATION_POLICY
//            ),
//            NOTIFICATION_PERMISSION_REQUEST_CODE
//        )
//    }
//
//    // 사용자에게 알림 권한이 거부된 경우 다시 설명하거나 추가 조치를 취할 수 있는 다이얼로그 표시
//    private fun showNotificationPermissionDeniedDialog() {
//        // 알림 권한이 거부된 경우, 사용자에게 추가적인 안내를 제공하거나 앱 설정으로 이동할 수 있는 옵션 제공
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_NOTIFICATION_POLICY
//            ),
//            NOTIFICATION_PERMISSION_REQUEST_CODE
//        )
//
//    }
//
//    private fun fetchCurrentUserEntity() {
//        viewModel.getUserStateFlowData(uid).observe(this) { userEntity ->
////            Log.e("userEntity 1차", userEntity.toString())
//            if (userEntity == null) {
////                Log.e("userEntity 2차", "null")
//                viewModel.getCurrentUser()
//
//                lifecycleScope.launchWhenStarted {
//                    viewModel.user.collectLatest { resource ->
//                        when (resource) {
//                            is Resource.Loading -> {
//                                // showProgressBar()
//                            }
//                            is Resource.Success -> {
//                                //TODO 처음 로그인할 때 무한 로그가 찍힌다. 이유는 모르겠음.
////                                Log.e("fetchCurrentUserEntity", "값을 성공적으로 불러왔습니다.")
//                                user = resource.data!!
//                                viewModel.insertUser(user.toEntity())
//                                currentUser = user.toEntity()
//                                // 2초 대기
////                                Log.e("fetchCurrentUserEntity", currentUser.toString())
//                                return@collectLatest
//
//                            }
//                            is Resource.Error -> {
//                                Toast.makeText(
//                                    this@SplashActivity123,
//                                    resource.message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            else -> Unit
//                        }
//                    }
//                }
//            } else {
//                Log.e("userEntity", userEntity.toString())
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
//                                    return@collectLatest
//                                } else {
//                                    Log.e("UserEntity가 존재한다면?", "서버와 다른 데이터")
//                                    user = resource.data!!
//                                    viewModel.updateUser(user.toEntity())
//                                }
//                            }
//                            is Resource.Error -> {
//                                // 에러 처리
//                            }
//                            else -> Unit
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
////    private fun fetchCurrentUserEntity() {
////        // ViewModel에서 유저 데이터를 가져오는 부분을 한 번만 호출
////        viewModel.getUserStateFlowData(uid).observe(this) { userEntity ->
////            // userEntity가 null이 아닐 때 UI 업데이트
////            if (userEntity == null) { // 처음 로그인을 했을 때.
////                Log.e("userEntity", "null입니다.")
////                viewModel.getCurrentUser()
////
////                // 한 번만 데이터 수집
////                lifecycleScope.launchWhenStarted {
////                    viewModel.user.collectLatest {
////                        when (it) {
////                            is Resource.Loading -> {
////                                // showProgressBar()
////                            }
////                            is Resource.Success -> {
////                                // hideProgressBar()
////                                Log.e("fetchCurrentUserEntity", "값을 성공적으로 불러왔습니다.")
////                                user = it.data!!
////                                viewModel.insertUser(user.toEntity())
////                                currentUser = user.toEntity()
////                            }
////                            is Resource.Error -> {
////                                // hideProgressBar()
////                                Toast.makeText(
////                                    this@SplashActivity,
////                                    it.message.toString(),
////                                    Toast.LENGTH_SHORT
////                                ).show()
////                            }
////                            else -> Unit
////                        }
////                    }
////                }
////            } else { // 여기는 Room으로부터 먼저 가져오되 서버에서도 가져와서 비교를 하고 업데이트 및 수정을 한다.
////                Log.e("userEntity", "${userEntity.toString()}")
////                currentUser = userEntity
////                user = userEntity.toUser()
////
////                // 실제랑 비교해보기
////                viewModel.getCurrentUser()
////
////                // 한 번만 데이터 수집
////                lifecycleScope.launchWhenStarted {
////                    viewModel.user.collectLatest {
////                        when (it) {
////                            is Resource.Success -> {
////                                if (user == it.data) {
////                                    Log.e("UserEntity가 존재한다면?", "서버와 동일한 데이터")
////                                    return@collectLatest
////                                } else {
////                                    Log.e("UserEntity가 존재한다면?", "서버와 다른 데이터")
////                                    user = it.data!!
////                                    viewModel.updateUser(user.toEntity())
////                                }
////                            }
////                            is Resource.Error -> {
////                                // 에러 처리
////                            }
////                            else -> Unit
////                        }
////                    }
////                }
////            }
////        }
////    }
//
//
//
//
//
//    companion object {
//
//        fun createLaunchIntent(
//            context: Context,
//            messageId: String,
//            parentMessageId: String?,
//            channelType: String,
//            channelId: String,
//        ) = Intent(context, SplashActivity123::class.java).apply {
//            putExtra(EXTRA_CHANNEL_ID, channelId)
//            putExtra(EXTRA_CHANNEL_TYPE, channelType)
//            putExtra(EXTRA_MESSAGE_ID, messageId)
//            putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
//        }
//
//        var currentUser: UserEntity? = null
//    }
//
//}