package com.kwonminseok.busanpartners.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.extensions.toUser
import com.kwonminseok.busanpartners.ui.login.LoginRegisterActivity
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Filters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter


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
                fetchUserData(user)
                viewModel.getCurrentUser()


                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest {
                        when (it) {
                            is Resource.Success -> {
                                if (user == it.data) {
                                    return@collectLatest
                                } else {
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


        binding.constraintProfile.setOnClickListener {
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
        if (showAuthenticationPrompt) {
            Handler(Looper.getMainLooper()).postDelayed({
                FancyShowCaseView.Builder(requireActivity())
                    .focusOn(binding.linearAuthentication)
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .focusAnimationStep(0)  // 기본값은 1
                    .focusAnimationMaxValue(10)
                    .title("인증을 먼저 진행해주세요.")
                    .titleStyle(R.style.CustomShowcaseTitle, Gravity.CENTER)
                    .build()
                    .show()
            }, 300) // 0.3초 지연


        }


        // 모든 알림 온오프
        binding.eventSwitchNotification.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("all_notifications_enabled", isChecked).apply()

            // 약간 알림 1개까지 추가로 제공하고 막히고 제공하는 느낌???????
            if (isChecked) {
                // 사용자가 스위치를 켜면 채널 알림을 활성화합니다.
                Toast.makeText(requireContext(), "알림이 활성화되었습니다", Toast.LENGTH_SHORT).show()

            } else {
                // 사용자가 스위치를 끄면 채널 알림을 비활성화합니다.
                Toast.makeText(requireContext(), "알림이 비활성화되었습니다", Toast.LENGTH_SHORT).show()


            }
        }



        // 여기서 만약 이메일인증까지 진행했다면 바로 학생증 인증으로 넘어가기
        // 클릭하면 information 창으로 넘어가기
        binding.collegeAuthentication.setOnClickListener {
            if (user.authentication.studentEmailAuthenticationComplete) {
                findNavController().navigate(R.id.action_profileFragment_to_collegeAuthImageFragment)
            } else {
                findNavController().navigate(R.id.action_profileFragment_to_onboardingStudentInformationFragment)

//                val intent =
//                    Intent(requireContext(), OnboardingStudentInformationActivity::class.java)
//                startActivity(intent)

            }
        }

        // 클릭하면 information 창으로 넘어가기
        binding.travelerAuthentication.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_onboardingTravelerInformationFragment)
        }

        binding.customerService.setOnClickListener {

        }

        // 로그아웃 버튼
        binding.linearLogOut.setOnClickListener {
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


        }

        binding.deleteAccount.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("계정이 탈퇴됩니다.")
                .setMessage("정말로 계정을 삭제하시겠습니까?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    isLogOut = true
                    BusanPartners.preferences.setString("uid", "")
                    BusanPartners.preferences.setString(Constants.TOKEN, "")

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // Delete user from Room database
                            viewModel.deleteUser(user.toEntity())

                            // Delete user from Firebase
                            val result = viewModel.deleteCurrentUser()
                            withContext(Dispatchers.Main) {
                                if (result is Resource.Success) {
                                    // Revoke Google Sign-In access
                                    GoogleSignIn.getClient(
                                        requireActivity(),
                                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                                    ).revokeAccess().addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(requireContext(), "계정이 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        } else {
                                            Log.e("Google Sign-In access revoke", "Failed")
                                            Toast.makeText(requireContext(), "Google Sign-In access revoke failed.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Log.e("계정 삭제에 실패했습니다", "Failure: ${result.message}")
                                    Toast.makeText(requireContext(), "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Delete Account", "Exception: ${e.message}")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

//        binding.deleteAccount.setOnClickListener {
//            AlertDialog.Builder(requireContext())
//                .setTitle("계정이 탈퇴됩니다.")
//                .setMessage("정말로 계정을 삭제하시겠습니까?")
//                .setPositiveButton("Yes") { dialog, _ ->
//                    dialog.dismiss()
//
////                    isLogOut = true
////                    // room 데이터 삭제
////                    BusanPartners.preferences.setString("uid", "")
////                    BusanPartners.preferences.setString(Constants.TOKEN, "")
////                    viewModel.deleteUser(user.toEntity())
////
////                    CoroutineScope(Dispatchers.Main).launch {
////                        viewModel.deleteCurrentUser()
////                    }
////
////                    GoogleSignIn.getClient(
////                        requireActivity(),
////                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
////                    ).revokeAccess().addOnCompleteListener {
////                        // 로그아웃 성공 후 LoginRegisterActivity로 이동
////                        val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
////                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////                        startActivity(intent)
////                    }
//                    isLogOut = true
//                    // Clear local preferences
//                    BusanPartners.preferences.setString("uid", "")
//                    BusanPartners.preferences.setString(Constants.TOKEN, "")
//
//                    CoroutineScope(Dispatchers.IO).launch {
//                        // Delete user from Room database
//                        viewModel.deleteUser(user.toEntity())
//
//                        // Delete user from Firebase
//                        val result = viewModel.deleteCurrentUser()
//                        withContext(Dispatchers.Main) {
//                            if (result is Resource.Success) {
//                                // Revoke Google Sign-In access
//                                GoogleSignIn.getClient(
//                                    requireActivity(),
//                                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//                                ).revokeAccess().addOnCompleteListener {
//                                    if (it.isSuccessful) {
//                                        // Redirect to LoginRegisterActivity
//                                        Log.e("Google Sign-In access revoke", "")
//
//                                        val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
//                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                        startActivity(intent)
//                                    } else {
//                                        // Handle failure
//                                        Log.e("Google Sign-In access revoke", "")
//
//                                        Toast.makeText(requireContext(), "Google Sign-In access revoke failed.", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            } else {
//                                // Handle failure
//                                Log.e("계정 삭제에 실패했습니다", "")
//                                Toast.makeText(requireContext(), "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//
//
//                }
//                .setNegativeButton("No") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
//
//        }



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
            tvEditPersonalDetails.text = if (user.college == null) {
                ""
            } else {
                "${user.college} ${user.major?.ko}"
            }
        }
        when (user.authentication.authenticationStatus) {
            "loading" -> {
                Log.e("fetchUserData", "load")
                binding.apply {
                    authenticationLoadOrCompleteCard.visibility = View.VISIBLE
                    travelerAuthentication.visibility = View.INVISIBLE
                    collegeAuthentication.visibility = View.INVISIBLE
                    authenticationLoadOrCompleteText.text = "인증 중입니다. 잠시만 기다려주세요."
                }
            }

            "complete" -> {
                if (user.authentication.collegeStudent) {
                    binding.apply {
                        authenticationLoadOrCompleteCard.visibility = View.VISIBLE
                        travelerAuthentication.visibility = View.INVISIBLE
                        collegeAuthentication.visibility = View.INVISIBLE
                        authenticationLoadOrCompleteText.text =
                            "환영합니다. \n\n자유롭게 관광객들과 대화를 나누고 다양한 경험을 쌓아 보세요."
                    }
                } else if (user.authentication.traveler) {
                    val tokenTime = formatDateTime(user.tokenTime.toString())
                    binding.apply {
                        authenticationLoadOrCompleteCard.visibility = View.VISIBLE
                        travelerAuthentication.visibility = View.INVISIBLE
                        collegeAuthentication.visibility = View.INVISIBLE
                        authenticationLoadOrCompleteText.text =
                            "부산에 오신 것을 환영합니다. \n ${tokenTime}까지 자유롭게 대화를 나눠보세요."
                    }
                }

            }

            else -> {
                binding.apply {
                    authenticationLoadOrCompleteCard.visibility = View.GONE
                    travelerAuthentication.visibility = View.VISIBLE
                    collegeAuthentication.visibility = View.VISIBLE
                }
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

    fun formatDateTime(dateTimeString: String): String {
        // ISO 8601 형식의 문자열을 OffsetDateTime 객체로 파싱
        val offsetDateTime = OffsetDateTime.parse(dateTimeString)

        // 원하는 날짜 형식 설정
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        // 날짜 형식화
        return offsetDateTime.format(formatter)
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
                Toast.makeText(context, "모든 채널의 알림이 비활성화되었습니다", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "모든 채널의 알림이 활성화되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ChatMute", "Failed to query channels: ${result.errorOrNull()?.message}")
            }
        }
    }





}