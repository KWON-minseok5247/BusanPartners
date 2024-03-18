package com.kwonminseok.busanpartners.mainScreen.profile

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthBinding
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthNumberBinding
import com.kwonminseok.busanpartners.databinding.FragmentColleteAuthCompleteBinding
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.viewmodel.AuthCompleteViewModel
import com.kwonminseok.busanpartners.viewmodel.ChatListViewModel
import com.univcert.api.UnivCert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//import com.univcert.api.UnivCert

private val TAG = "CollegeAuthCompleteFragment"
@AndroidEntryPoint
class CollegeAuthCompleteFragment : Fragment() {
    lateinit var binding: FragmentColleteAuthCompleteBinding
    private val viewModel by viewModels<AuthCompleteViewModel>()

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
                findNavController().navigate(R.id.action_collegeAuthCompleteFragment_to_profileFragment)
            }


        } else {
            binding.authenticationAnswer.text = "인증을 실패하였습니다."
            binding.authCompleteButton.text = "다시 인증하기"
            binding.authCompleteButton.setOnClickListener {
                findNavController().navigate(R.id.action_collegeAuthCompleteFragment_to_collegeAuthFragment)
            }
            //ToDo 여기선 false이므로 다시 이메일 인증화면으로 돌아간다.
        }





    }
}