package com.kwonminseok.newbusanpartners.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.databinding.FragmentColleteAuthCompleteBinding
import com.kwonminseok.newbusanpartners.util.hideBottomNavigationView
import com.kwonminseok.newbusanpartners.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

//import com.univcert.api.UnivCert

private val TAG = "CollegeAuthCompleteFragment"
@AndroidEntryPoint
class CollegeAuthCompleteFragment : Fragment() {

    private var _binding: FragmentColleteAuthCompleteBinding? = null
    private val binding get() = _binding!!

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
                    getString(R.string.cannot_go_back_during_verification),
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
        _binding = FragmentColleteAuthCompleteBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isVerified = arguments?.getBoolean("isVerified") ?: false

        if (isVerified) {
            //TODO 여기서는 유저에서 대학생이라는 것을 true로 설정하고 프로필 화면으로 이동해야 한다.

            binding.authenticationAnswer.text = getString(R.string.email_verification_success)
            binding.authenticationDetailAnswer.text = getString(R.string.prepare_student_id)
            // 유저정보 업데이트 함수 필요
//            binding.authCompleteButton.setOnClickListener {
//                findNavController().navigate(R.id.action_collegeAuthCompleteFragment_to_collegeAuthImageFragment)
//            }
            binding.authCompleteButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_collegeAuthCompleteFragment_to_collegeAuthImageFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
                )
            }


        } else {            // 여기선 false이므로 다시 이메일 인증화면으로 돌아간다.

            binding.authenticationAnswer.text = getString(R.string.email_verification_failed)
            binding.authenticationDetailAnswer.text = getString(R.string.retry_verification)
            binding.authCompleteButton.setOnClickListener {
                findNavController().navigate(R.id.action_collegeAuthCompleteFragment_to_collegeAuthFragment)
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}