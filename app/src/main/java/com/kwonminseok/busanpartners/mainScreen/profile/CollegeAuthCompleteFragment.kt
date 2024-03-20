package com.kwonminseok.busanpartners.mainScreen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.FragmentColleteAuthCompleteBinding
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint

//import com.univcert.api.UnivCert

private val TAG = "CollegeAuthCompleteFragment"
@AndroidEntryPoint
class CollegeAuthCompleteFragment : Fragment() {
    lateinit var binding: FragmentColleteAuthCompleteBinding
    private val viewModel by viewModels<AuthenticationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뒤로 가기 버튼 커스텀 동작을 설정합니다.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 이 안에서 뒤로 가기 버튼이 눌렸을 때 원하는 동작을 구현합니다.
                // 예를 들어, 아무 것도 하지 않게 하여 뒤로 가기를 제한할 수 있습니다.

                // Toast 메시지로 경고를 표시하는 등 사용자에게 피드백을 줄 수도 있습니다.
                Toast.makeText(
                    context,
                    "인증번호 확인 중에는 뒤로 가실 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentColleteAuthCompleteBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isVerified = arguments?.getBoolean("isVerified") ?: false

        if (isVerified) {
            //TODO 여기서는 유저에서 대학생이라는 것을 true로 설정하고 프로필 화면으로 이동해야 한다.

            binding.authenticationAnswer.text = "인증이 완료되었습니다."
            binding.authCompleteButton.text = "프로필 화면으로 돌아가기"
            // 유저정보 업데이트 함수 필요
            viewModel.saveCollegeStatus()
            binding.authCompleteButton.setOnClickListener {
                findNavController().navigate(R.id.action_collegeAuthCompleteFragment_to_collegeAuthFragment)
            }


        } else {
            binding.authenticationAnswer.text = "인증을 실패하였습니다."
            binding.authCompleteButton.text = "확인"
            binding.authCompleteButton.setOnClickListener {
                findNavController().navigate(R.id.action_collegeAuthCompleteFragment_to_collegeAuthFragment)
            }
            //ToDo 여기선 false이므로 다시 이메일 인증화면으로 돌아간다.
        }





    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }

    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        showBottomNavigationView()
        super.onPause()
    }
}