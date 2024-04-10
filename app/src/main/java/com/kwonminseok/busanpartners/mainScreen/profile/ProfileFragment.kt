package com.kwonminseok.busanpartners.mainScreen.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.BusanPartners
import com.kwonminseok.busanpartners.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.login.LoginRegisterActivity
import com.kwonminseok.busanpartners.util.PreferenceUtil
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ChatListViewModel
import com.kwonminseok.busanpartners.viewmodel.ProfileViewModel
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import com.univcert.api.UnivCert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val TAG = "ProfileFragment"
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
//    private val viewModel by viewModels<ProfileViewModel>()
    private val viewModel: UserViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        fetchUserData(it.data!!)
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


//        lifecycleScope.launchWhenStarted {
//            viewModel.updateStatus.collect {
//                when (it.data) {
//                    false -> {
//                        Log.e("viewModel.getCurrentUser()","false")
//
//                    }
//                    true  -> {
//                        viewModel.getCurrentUser()
//                        Log.e("viewModel.getCurrentUser()","true")
//                    }
//
//                    else -> {
//                        Log.e("viewModel.getCurrentUser()","${it.data} ${it.message}")
//
//                    }
//                }
//            }
//        }

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }






        //TODO 클릭하면 information 창으로 넘어가기
        binding.collegeAuthentication.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_collegeAuthFragment)
        }

        //TODO 클릭하면 information 창으로 넘어가기
        binding.travelerAuthentication.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_travelerAuthFragment)
        }

        binding.chat.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    UnivCert.clear(BuildConfig.COLLEGE_KEY)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.linearLogOut.setOnClickListener {
            viewModel.logOutCurrentUser()


            chatClient.disconnect(true).enqueue { result ->
                if (result.isSuccess) {
                    // 성공적으로 사용자 연결 해제 및 로컬 캐시 지움
                    // 여기서 새 사용자로 ChatClient를 설정할 수 있습니다.
                    Log.w(TAG," chatClient가 성공적으로 로그아웃되었습니다.")
                } else {
                    // 연결 해제 실패 처리
                    Log.w(TAG," chatClient가 로그아웃에 실패했습니다.")

                }
            }

            BusanPartners.preferences.setString("token", "")
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

    override fun onResume() {
        super.onResume()
//        viewModel.getCurrentUser()
//        Log.e("viewModel.getCurrentUser()", "실행되었다.")
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
                Log.e("fetchUserData", "complete")
                binding.apply {
                    authenticationLoadOrCompleteCard.visibility = View.VISIBLE
                    travelerAuthentication.visibility = View.INVISIBLE
                    collegeAuthentication.visibility = View.INVISIBLE
                    authenticationLoadOrCompleteText.text = "환영합니다. \n\n자유롭게 관광객들과 대화를 나누고 다양한 경험을 쌓아 보세요."
                }
            }
            else -> {
                Log.e("fetchUserData", "else")
                binding.apply {
                    authenticationLoadOrCompleteCard.visibility = View.GONE
                    travelerAuthentication.visibility = View.VISIBLE
                    collegeAuthentication.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressbarSettings.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressbarSettings.visibility = View.GONE
    }
}