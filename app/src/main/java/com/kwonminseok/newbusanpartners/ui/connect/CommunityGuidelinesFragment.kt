package com.kwonminseok.newbusanpartners.ui.connect

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.data.TranslatedText
import com.kwonminseok.newbusanpartners.databinding.FragmentMessagePrivacyBeforeChatBinding
import com.kwonminseok.newbusanpartners.util.LanguageUtils
import com.kwonminseok.newbusanpartners.viewmodel.UserViewModel

class CommunityGuidelinesFragment : Fragment(R.layout.fragment_message_privacy_before_chat) {

    private var _binding: FragmentMessagePrivacyBeforeChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMessagePrivacyBeforeChatBinding.bind(view)

        // SharedPreferences 초기화
        sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        val asd = sharedPreferences.getBoolean("isAgreed", false)
        Log.e("asd는", asd.toString())

        // 기본적으로 버튼 비활성화 및 회색으로 설정
        binding.connectButton.isEnabled = false
        binding.connectButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.cr24bff009965)

        // 체크박스 상태에 따라 버튼 활성화/비활성화 및 색상 변경
        binding.cbAgree.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.connectButton.isEnabled = true
                binding.connectButton.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cr24bff009963)
            } else {
                binding.connectButton.isEnabled = false
                binding.connectButton.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cr24bff009965)
//                binding.connectButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey)) // 회색으로 변경
            }
        }

        // 채팅 시작 버튼 클릭 시 동작
        binding.connectButton.setOnClickListener {
            // SharedPreferences에 체크 상태 저장
            sharedPreferences.edit().putBoolean("isAgreed", true).apply()
            val studentUid = arguments?.getString("studentUid", null)
            val name = arguments?.getString("name", "Chatting Room")

            val b = Bundle().apply {
                putString("studentUid", studentUid)
                putString(
                    "name",
                    name
                )
            }
            findNavController().navigate(R.id.action_communityGuidelinesFragment_to_messageFragment, b)

            // 프래그먼트 종료
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getTranslatedText(translatedText: TranslatedText?): String? {
        val language = LanguageUtils.getDeviceLanguage(requireContext())
        return when (language) {
            "ko" -> translatedText?.ko
            "en" -> translatedText?.en
            "ja" -> translatedText?.ja ?: translatedText?.en
            "zh" -> translatedText?.zh ?: translatedText?.en
            "zh-TW" -> translatedText?.zh ?: translatedText?.en
            "zh-CN" -> translatedText?.zh ?: translatedText?.en
            "es" -> translatedText?.es ?: translatedText?.en

            else -> translatedText?.en
        }
    }

}

