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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.barnea.dialoger.Dialoger
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentAuthenticationSelectBinding
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.extensions.toUser
import com.kwonminseok.busanpartners.ui.login.LoginRegisterActivity
import com.kwonminseok.busanpartners.ui.login.SplashActivity.Companion.currentUser
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Filters
import kotlinx.coroutines.flow.collectLatest
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@AndroidEntryPoint
class AuthenticationSelectFragment : Fragment() {

    private var _binding: FragmentAuthenticationSelectBinding? = null
    private val binding get() = _binding!!
    lateinit var user: User


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