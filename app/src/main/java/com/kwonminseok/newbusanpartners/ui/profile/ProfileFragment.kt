package com.kwonminseok.newbusanpartners.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alertview.lib.AlertView
import com.alertview.lib.OnItemClickListener
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.application.BusanPartners
import com.kwonminseok.newbusanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.newbusanpartners.extensions.toEntity
import com.kwonminseok.newbusanpartners.extensions.toUser
import com.kwonminseok.newbusanpartners.ui.login.LoginRegisterActivity
import com.kwonminseok.newbusanpartners.util.Constants
import com.kwonminseok.newbusanpartners.util.LanguageUtils.getDeviceLanguage
import com.kwonminseok.newbusanpartners.util.Resource
import com.kwonminseok.newbusanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Filters
import kotlinx.coroutines.flow.collectLatest
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale


private val TAG = "ProfileFragment"

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!


    private var isLogOut = false
    //    private val viewModel by viewModels<ProfileViewModel>()
    private val viewModel: UserViewModel by viewModels()
    lateinit var user: User
    private val uid = BusanPartners.preferences.getString("uid", "")
    private lateinit var sharedPreferences: SharedPreferences

    private var hasShownFancyShowCase = false  // 추가된 플래그 변수
    private var statusBarHeight: Int = 0

    // 여기서 해야 할 거는 일단 room을 통해서 데이터를 가져오기 ->
    // 만약 room에 데이터가 없다면 네트워크로부터 데이터를 가져오기 -> 가져온 데이터를 insert하기
    // 만약 room의 데이터가 있으면 일단 그 데이터로 표시한다 -> 근데 네트워크로부터 가져온 데이터랑
    // 다르다면 네트워크 데이터로 최신화한다. -> 그리고 데이터를 insert한다.
    // 만약 room 데이터와 네트워크로부터 가져온 데이터가 동일하다면 굳이 업데이트는 필요 없음.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        requireActivity().setStatusBarTransparent()
//        applyWindowInsets(binding.root)


        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                }
            }
        )


        viewModel.getUserStateFlowData(uid).observe(viewLifecycleOwner) { userEntity ->
            // userEntity가 null이 아닐 때 UI 업데이트
            if (isLogOut) { // 로그아웃을 클릭할 때
               return@observe
            }
            if (userEntity == null ) { // 처음 로그인을 했을 때.
                viewModel.getCurrentUser()

                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
//                                showProgressBar()
                            }

                            is Resource.Success -> {
//                                hideProgressBar()
                                fetchUserData(it.data!!)
                                user = it.data
                                viewModel.insertUser(user.toEntity())

                            }

                            is Resource.Error -> {
//                                hideProgressBar()
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> Unit
                        }
                    }
                }
            } else { // 여기는 Room으로부터 먼저 가져오되 서버에서도 가져와서 비교를 하고 업데이트 및 수정을 한다.

                user = userEntity.toUser()
                Log.e("user는 뭐지?", user.toString() )
                fetchUserData(user)
                viewModel.getCurrentUser()


                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest {
                        when (it) {
                            is Resource.Success -> {
                                if (user == it.data) {
                                    return@collectLatest
                                } else {
                                    if (it.data?.reset == true) {
                                        Log.e("reset", "true일 때")
//                                        fetchUserData(it.data!!)
//                                        user = it.data
//                                        viewModel.updateUser(user.toEntity())

//                                        Dialoger(requireContext(), Dialoger.TYPE_MESSAGE)
//                                            .setTitle("서버로부터 데이터가 변경되었습니다.")
//                                            .setDescription("앱을 다시 실행시켜주세요.")
////                                            .setProgressBarColor(R.color.black)
//                                            .show()
//                                            .setButtonText("확인")
//                                            .setButtonOnClickListener {
//                                                requireActivity().finishAffinity()
//                                                System.exit(0)
//                                            }
//                                        isDialogShown = true
                                        Toast.makeText(requireContext(),getString(R.string.data_changed_message), Toast.LENGTH_SHORT).show()
                                        requireActivity().finishAffinity()
                                        System.exit(0)

                                    }
                                    Log.e("it.data 뭐지?", it.data.toString() )

                                    fetchUserData(it.data!!)
                                    user = it.data
                                    viewModel.updateUser(user.toEntity())

                                }

                            }

                            is Resource.Error -> {

                            }

                            else -> Unit
                        }
                    }
                }
            }

        }


// Handle back press




    binding.linearProfileUser.setOnClickListener {
            if (user.authentication.collegeStudent) {
                findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
            } else {
                findNavController().navigate(R.id.action_profileFragment_to_userAccountForBeginnerFragment)
            }
        }
        sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        binding.eventSwitchNotification.isChecked = sharedPreferences.getBoolean("all_notifications_enabled", true)

        val allNotificationsEnabled = sharedPreferences.getBoolean("all_notifications_enabled", true)

        val chatNotificationsEnabled = sharedPreferences.getBoolean("chat_notifications_enabled", true)
        binding.chatSwitchNotification.isChecked = chatNotificationsEnabled




        // 채팅 알림 스위치 온 오프
        binding.chatSwitchNotification.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("chat_notifications_enabled", isChecked).apply()

            // 약간 알림 1개까지 추가로 제공하고 막히고 제공하는 느낌???????
            if (isChecked) {
                // 사용자가 스위치를 켜면 채널 알림을 활성화합니다.
                unmuteAllChannels(requireContext(), chatClient)
            } else {
                // 사용자가 스위치를 끄면 채널 알림을 비활성화합니다.
                muteAllChannels(requireContext(), chatClient)


            }
        }

        // connect에서 넘어왔을 경우 실행되는 코드
        val showAuthenticationPrompt = arguments?.getBoolean("showAuthenticationPrompt") ?: false
        if (showAuthenticationPrompt && !hasShownFancyShowCase) {  // 플래그 체크 추가
            Handler(Looper.getMainLooper()).postDelayed({
                FancyShowCaseView.Builder(requireActivity())
                    .focusOn(binding.linearAuthentication)
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .focusAnimationStep(0)
                    .focusAnimationMaxValue(10)
//                    .title("인증을 먼저 진행해주세요.")
//                    .titleStyle(R.style.CustomShowcaseTitle, Gravity.CENTER)
//                    .titleGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL) // 타이틀을 상단 중앙으로 배치
                    .build()
                    .show()
            }, 300)



//            val density = resources.displayMetrics.density
//            val offsetInDp = 25 * density
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                // Focus on view excluding the status bar height
////                val location = IntArray(2)
////                binding.linearAuthentication.getLocationOnScreen(location)
////                val focusX = location[0] + binding.linearAuthentication.width / 2
////                // 상태바 높이를 빼서 focusY 조정 후, 추가로 오프셋을 줘서 더 낮게 설정
////                val focusY = location[1] + binding.linearAuthentication.height / 2 - statusBarHeight + 65 // 추가로 100dp 낮게 설정
//
//                val location = IntArray(2)
//                binding.linearAuthentication.getLocationOnScreen(location)
//                val focusX = location[0] + binding.linearAuthentication.width / 2
//                // 상태바 높이를 빼서 focusY 조정 후, 추가로 오프셋을 줘서 더 낮게 설정
//                val focusY = location[1] + binding.linearAuthentication.height / 2 - statusBarHeight + offsetInDp.toInt()
//
//                FancyShowCaseView.Builder(requireActivity())
//                    .focusOn(binding.linearAuthentication)
////                    .focusRectAtPosition(focusX, focusY, binding.linearAuthentication.width, binding.linearAuthentication.height
////                    )
//                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
//                    .focusAnimationStep(0)
//                    .focusAnimationMaxValue(10)
//                    .title("인증을 먼저 진행해주세요.")
//                    .titleStyle(R.style.CustomShowcaseTitle, Gravity.CENTER)
//                    .build().apply {
//                        // set position if needed
//                    }
//                    .show()
//            }, 300)

            hasShownFancyShowCase = true  // 플래그 설정
        }



        // 모든 알림 온오프
        binding.eventSwitchNotification.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("all_notifications_enabled", isChecked).apply()

            // 약간 알림 1개까지 추가로 제공하고 막히고 제공하는 느낌???????
            if (isChecked) {
                // 사용자가 스위치를 켜면 채널 알림을 활성화합니다.
                Toast.makeText(requireContext(), getString(R.string.notification_activated), Toast.LENGTH_SHORT).show()

            } else {
                // 사용자가 스위치를 끄면 채널 알림을 비활성화합니다.
                Toast.makeText(requireContext(), getString(R.string.notification_deactivated), Toast.LENGTH_SHORT).show()


            }
        }

        binding.linearAuthentication.setOnClickListener {
            if (user.authentication.authenticationStatus == "loading") {
                Toast.makeText(requireContext(), getString(R.string.auth_in_progress), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (user.authentication.authenticationStatus == "complete") {
                Toast.makeText(requireContext(), getString(R.string.auth_already_completed), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_profileFragment_to_authenticationSelectFragment)
        }

        binding.linearCustomerService.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_FAQFragment)
        }
        binding.linearPersonalInformation.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_privacyPolicyFragment)

        }

        binding.linearUseTerms.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_termsOfServiceFragment)

        }


        // 로그아웃 버튼
        binding.linearLogOut.setOnClickListener {
            AlertView.Builder()
                .setContext(requireActivity())
                .setStyle(AlertView.Style.ActionSheet)
                .setTitle(getString(R.string.log_out_confirmation))
                .setDestructive(getString(R.string.confirmation))
                .setOthers(arrayOf(getString(R.string.cancel)))
                .setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(o: Any?, position: Int) {
                        if (position == 0) { // 확인 버튼 위치 확인
                            isLogOut = true
                            // room 데이터 삭제
//            viewModel.getUserStateFlowData(uid).removeObservers(this)
                            BusanPartners.preferences.setString("uid", "")
                            BusanPartners.preferences.setString(Constants.TOKEN, "")
                            viewModel.deleteUser(user.toEntity())

                            viewModel.logOutCurrentUser()


                            chatClient.disconnect(true).enqueue { result ->
                                if (result.isSuccess) {
                                    // 성공적으로 사용자 연결 해제 및 로컬 캐시 지움
                                    // 여기서 새 사용자로 ChatClient를 설정할 수 있습니다.
                                    Log.w(TAG, " chatClient가 성공적으로 로그아웃되었습니다.")
                                } else {
                                    // 연결 해제 실패 처리
                                    Log.w(TAG, " chatClient가 로그아웃에 실패했습니다.")

                                }
                            }


                            // 접근권한으로부터 해제
                            GoogleSignIn.getClient(
                                requireActivity(),
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                            ).revokeAccess().addOnCompleteListener {
                                // 로그아웃 성공 후 LoginRegisterActivity로 이동
                                val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        } else {
                            (o as AlertView).dismiss() // 다른 버튼 클릭 시 AlertView 닫기
                        }
                    }

                })
                .build()
                .setCancelable(true)
                .show()


        }

        binding.linearDeleteAccount.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_unregisterFragment)
        }

        binding.linearTranslate.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_languageFragment)
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun fetchUserData(user: User) {
        binding.apply {
            Glide.with(requireView()).load(user.imagePath).override(200, 200)
                .into(binding.imageUser)
            tvUserName.text = user.name?.ko
            if (user.college == null) {
                tvUniversityName.visibility = View.GONE
            } else {
                tvUniversityName.visibility = View.VISIBLE
                tvUniversityName.text = "${user.college} ${user.major?.ko}"
            }
        }
        when (user.authentication.authenticationStatus) {
            "loading" -> {
                binding.apply {
                    authenticationStatus.text = getString(R.string.auth_in_progress)
                }
            }

            "complete" -> {
                if (user.authentication.collegeStudent) {
                    binding.apply {
                        authenticationStatus.text = getString(R.string.authentication_completed_student)
                    }
                } else if (user.authentication.traveler) {
                    val tokenTime = formatDateTime(user.tokenTime.toString(), Locale.forLanguageTag(getDeviceLanguage(requireContext())))
                    binding.apply {
                        authenticationStatus.text = getString(R.string.welcome_traveler, tokenTime)
                    }
                }

            }

            else -> {
            }
        }
    }

//    private fun showProgressBar() {
//        binding.progressbarSettings.visibility = View.VISIBLE
//    }
//
//    private fun hideProgressBar() {
//        binding.progressbarSettings.visibility = View.GONE
//    }

    fun formatDateTime(dateTimeString: String, locale: Locale): String {
        val offsetDateTime = OffsetDateTime.parse(dateTimeString)
        Log.e("locale", locale.language)

        // 로케일에 따라 날짜 형식 설정
        val pattern = when (locale.language) {
            "ja" -> "yyyy年MM月dd日" // 일본어
            "en" -> "MMMM dd, yyyy" // 영어
            "zh" -> if (locale.country == "CN") "yyyy年MM月dd日" else "yyyy年MM月dd日" // 중국어 (간체, 번체)
            "es" -> "dd 'de' MMMM 'de' yyyy" // 에스파냐어
            "th" -> "dd MMM yyyy" // 태국어
            "vi" -> "dd MMM yyyy" // 베트남어
            "in" -> "dd MMM yyyy" // 인도네시아어
            "ko" -> "yyyy년 MM월 dd일"
            else ->  "MMMM dd, yyyy"//
        }

        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return offsetDateTime.format(formatter)
    }

    override fun onResume() {
        super.onResume()
//        requireActivity().setStatusBarTransparent()
//        applyWindowInsets(binding.root)
////        binding.linearProfileUser.setPadding(
////            0,
////            requireContext().statusBarHeight(),
////            0,
////            0
////        )
//        Log.e("d", requireContext().statusBarHeight().toString())

    }

    override fun onPause() {
        super.onPause()
//        requireActivity().setStatusBarVisible()

    }

    fun muteAllChannels(context: Context, chatClient: ChatClient) {
        val filter = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(chatClient.getCurrentUser()?.id ?: ""))
        )
        val request = QueryChannelsRequest(filter, 0, 100)

        chatClient.queryChannels(request).enqueue { result ->
            if (result.isSuccess) {
                val channels = result.getOrNull()
                if (channels == null) {
                    return@enqueue
                }

                for (channel in channels) {
                    chatClient.muteChannel(channel.type, channel.id).enqueue { muteResult ->
                        if (!muteResult.isSuccess) {
                            Log.e("ChatMute", "Failed to mute channel: ${channel.id}")
                        }
                    }
                }
                Toast.makeText(context, getString(R.string.notification_deactivated), Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ChatMute", "Failed to query channels: ${result.errorOrNull()?.message}")
            }
        }
    }

    fun unmuteAllChannels(context: Context, chatClient: ChatClient) {
        val filter = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(chatClient.getCurrentUser()?.id ?: ""))
        )
        val request = QueryChannelsRequest(filter, 0, 100)

        chatClient.queryChannels(request).enqueue { result ->
            if (result.isSuccess) {
                val channels = result.getOrNull()
                if (channels == null) {
                    return@enqueue
                }
                for (channel in channels) {
                    chatClient.unmuteChannel(channel.type, channel.id).enqueue { unmuteResult ->
                        if (!unmuteResult.isSuccess) {
                            Log.e("ChatMute", "Failed to unmute channel: ${channel.id}")
                        }
                    }
                }
                Toast.makeText(context, getString(R.string.notification_activated), Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ChatMute", "Failed to query channels: ${result.errorOrNull()?.message}")
            }
        }
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


}