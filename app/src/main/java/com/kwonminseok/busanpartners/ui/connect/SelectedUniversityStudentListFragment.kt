package com.kwonminseok.busanpartners.ui.connect

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kelineyt.adapter.makeIt.StudentCardAdapter
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.StudentAdapter
import com.kwonminseok.busanpartners.data.TranslatedText
import com.kwonminseok.busanpartners.data.Universities
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentConnectBinding
import com.kwonminseok.busanpartners.databinding.FragmentSelectedUniversityStudentListBinding
import com.kwonminseok.busanpartners.databinding.FragmentUniversityBinding
import com.kwonminseok.busanpartners.ui.login.SplashActivity.Companion.currentUser
import com.kwonminseok.busanpartners.util.LanguageUtils
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


private val TAG = "SelectedUniversityStudentListFragment"

@AndroidEntryPoint
class SelectedUniversityStudentListFragment : Fragment() {
    private var chipTexts: MutableList<String>? = null
    private var _binding: FragmentUniversityBinding? = null
    private val binding get() = _binding!!

    //    private val viewModel by viewModels<ConnectViewModel>()
//    private val adapter by lazy { StudentCardAdapter() }
    private val adapter by lazy { StudentAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUniversityBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: Array<out Parcelable>? =
            arguments?.getParcelableArray("selectedUniversityStudents")
        val usersList = args?.toList()?.mapNotNull { it as? User }

        val user = usersList?.get(0)
        Universities.universityInfoList.forEach { university ->
            if (user != null) {
                if (user.college == university.nameKo) {
                    val universityName = when (LanguageUtils.getDeviceLanguage(requireContext())) {
                        "ko" -> university.nameKo
                        "en" -> university.nameEn
                        "ja" -> university.nameJa
                        "zh" -> university.nameZh
                        "zh-TW" -> university.nameZhTw
                        else -> university.nameEn
                    }
                    binding.tvUniversity.text = universityName
                    Glide.with(this).load(university.logoResourceId).into(binding.imageUniversity)
                    return@forEach
                }

            }
        }


        Log.e("usersList", usersList.toString())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        adapter.submitList(usersList)

//        //TODO // 자기 자신 클릭할 수 없게. 대학생은 대학생끼리 연락할 수 없게. 관광객이 아니면 연락할 수 없게.
//        // 무분별하게 연락할 수 없게.
//        studentCardRv()
//
//        binding.floatingMessageButton.setOnClickListener {
//            if (usersList != null) {
//
//                val currentPosition = binding.viewPagerImages.currentItem
//
//                if (currentUser?.authentication?.collegeStudent == true) {
//                    Toast.makeText(
//                        requireContext(),
//                        "대학생은 다른 대학생에게 연락을 할 수 없습니다.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                if (currentUser!!.blockList?.contains(usersList[currentPosition].uid) == true) {
//                    Toast.makeText(
//                        requireContext(),
//                        "현재 채팅 중이거나 이미 채팅을 끝낸 상대방과 다시 연락할 수 없습니다. ",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                if (currentUser!!.chatChannelCount >= 3) {
//                    Toast.makeText(
//                        requireContext(),
//                        "최대 활성화할 수 있는 채팅방은 3개입니다.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                else {
//                    val b = Bundle().apply {
//                        putString("studentUid", usersList[currentPosition].uid)
//                        putString(
//                            "name",
////                            "${usersList[currentPosition].name?.ko}\n (${getTranslatedText(usersList[currentPosition].name)})"
//                            "${usersList[currentPosition].name?.ko}(${getTranslatedText(usersList[currentPosition].name)})"
//
//                        )
//                    }
//                    findNavController().navigate(
//                        R.id.action_selectedUniversityStudentListFragment_to_messageFragment,
//                        b
//                    )
//                }
//
//            }
//
//
//        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun studentCardRv() {
//        binding.viewPagerImages.adapter = adapter
//    }


    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }

    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        showBottomNavigationView()
        super.onPause()
    }

//    fun getDeviceLanguage(context: Context): String {
//        return context.resources.configuration.locales.get(0).language
//    }

    private fun getTranslatedText(translatedText: TranslatedText?): String? {
        val language = LanguageUtils.getDeviceLanguage(requireContext())
        return when (language) {
            "ko" -> translatedText?.ko
            "en" -> translatedText?.en ?: translatedText?.ko
            "ja" -> translatedText?.ja ?: translatedText?.ko
            "zh" -> translatedText?.zh ?: translatedText?.ko
            else -> translatedText?.en
        }
    }

}