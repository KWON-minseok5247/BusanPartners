package com.kwonminseok.newbusanpartners.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.databinding.FragmentAuthenticationSelectBinding
import com.kwonminseok.newbusanpartners.extensions.toEntity
import com.kwonminseok.newbusanpartners.extensions.toUser
import com.kwonminseok.newbusanpartners.ui.login.SplashActivity.Companion.currentUser
import com.kwonminseok.newbusanpartners.util.Resource
import com.kwonminseok.newbusanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AuthenticationSelectFragment : Fragment() {

    private var _binding: FragmentAuthenticationSelectBinding? = null
    private val binding get() = _binding!!
    lateinit var user: User

    private val viewModel: UserViewModel by viewModels()

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
        _binding = FragmentAuthenticationSelectBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = currentUser!!.toUser()
        Log.e("user", user.toString())
        viewModel.getUserStateFlowData(currentUser!!.uid).observe(viewLifecycleOwner) { userEntity ->
               // 여기는 Room으로부터 먼저 가져오되 서버에서도 가져와서 비교를 하고 업데이트 및 수정을 한다.

            if (userEntity != null) {
                user = userEntity.toUser()
            }
                Log.e("user는 뭐지?", user.toString() )
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

                                        Toast.makeText(requireContext(), getString(R.string.data_changed_message), Toast.LENGTH_SHORT).show()
                                        requireActivity().finishAffinity()
                                        System.exit(0)

                                    }
                                    Log.e("it.data 뭐지?", it.data.toString() )

                                    user = it.data!!
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

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // 여기서 만약 이메일인증까지 진행했다면 바로 학생증 인증으로 넘어가기
        // 클릭하면 information 창으로 넘어가기
        binding.linearStudent.setOnClickListener {
            if (user.authentication.studentEmailAuthenticationComplete) {
                findNavController().navigate(R.id.action_authenticationSelectFragment_to_collegeAuthImageFragment)
            } else {
                findNavController().navigate(R.id.action_authenticationSelectFragment_to_onboardingStudentInformationFragment)

//                val intent =
//                    Intent(requireContext(), OnboardingStudentInformationActivity::class.java)
//                startActivity(intent)

            }
        }

        binding.linearTourist.setOnClickListener {
            findNavController().navigate(R.id.action_authenticationSelectFragment_to_onboardingTravelerInformationFragment)
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }








}