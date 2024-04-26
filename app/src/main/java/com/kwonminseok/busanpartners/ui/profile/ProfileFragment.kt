package com.kwonminseok.busanpartners.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentHomeBinding
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.extensions.toUser
import com.kwonminseok.busanpartners.ui.login.LoginRegisterActivity
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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


    //Todo 여기서 해야 할 거는 일단 room을 통해서 데이터를 가져오기 ->
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
            if (userEntity == null ) { //
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
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }


        //TODO 여기서 만약 이메일인증까지 진행했다면 바로 학생증 인증으로 넘어가기
        //TODO 클릭하면 information 창으로 넘어가기
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

        //TODO 클릭하면 information 창으로 넘어가기
        binding.travelerAuthentication.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_onboardingTravelerInformationFragment)
        }

        binding.chat.setOnClickListener {
//            GlobalScope.launch(Dispatchers.IO) {
//                try {
//                    UnivCert.clear(BuildConfig.COLLEGE_KEY)
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//


        }

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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun fetchUserData(user: User) {
        binding.apply {
            Glide.with(requireView()).load(user.imagePath).override(200, 200)
                .into(binding.imageUser)
            tvUserName.text = user.name
            tvEditPersonalDetails.text = "${user.college} ${user.major}"
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



}