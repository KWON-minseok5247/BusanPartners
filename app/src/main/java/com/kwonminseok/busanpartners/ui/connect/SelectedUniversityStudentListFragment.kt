package com.kwonminseok.busanpartners.ui.connect

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.kwonminseok.busanpartners.databinding.FragmentUniversityBinding
import com.kwonminseok.busanpartners.ui.login.SplashActivity.Companion.currentUser
import com.kwonminseok.busanpartners.util.LanguageUtils
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG = "SelectedUniversityStudentListFragment"

@AndroidEntryPoint
class SelectedUniversityStudentListFragment : Fragment() {
    private var chipTexts: MutableList<String>? = null
    private var _binding: FragmentUniversityBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private var roomUser: User? = null
    private var userList: MutableList<User>? = null

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

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_selectedUniversityStudentListFragment_to_connectFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_selectedUniversityStudentListFragment_to_connectFragment)
                }
            }
        )

        setFragmentResultListener("blockUserRequest") { _, bundle ->
            val reportedUserId = bundle.getString("reportedUserId")
            val userUniversity = bundle.getString("userUniversity")

//
            userViewModel.getCurrentUser()
            lifecycleScope.launchWhenStarted {
                userViewModel.user.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            roomUser = resource.data
                            reportedUserId?.let { blockUser(it) }

                            Log.e("roomUsedr", roomUser.toString())
                            // ViewModel 함수 호출
                            userViewModel.getUniversityStudentsWantToMeet()

                            lifecycleScope.launchWhenStarted {
                                userViewModel.students.collectLatest {
                                    when (it) {
                                        is Resource.Loading -> {
                                            Log.e("Resource.Loading", "Resource.Loading")
                                        }

                                        is Resource.Success -> {
                                            Log.e("Resource.Success", "Resource.Success")
                                            // blockList에 포함되지 않은 사용자만 userList에 추가

                                            Universities.universityInfoList.forEach { university ->
                                                    if (userUniversity == university.nameKo) {
                                                        val universityName = when (LanguageUtils.getDeviceLanguage(requireContext())) {
                                                            "ko" -> university.nameKo
                                                            "en" -> university.nameEn
                                                            "ja" -> university.nameJa
                                                            "zh-CN" -> university.nameZh
                                                            "zh-TW" -> university.nameZhTw
                                                            "es" -> university.nameEs
                                                            "vi" -> university.nameVi
                                                            "th" -> university.nameTh
                                                            "in" -> university.nameIn
                                                            else -> university.nameEn
                                                        }

                                                        binding.tvUniversity.text = universityName
//                    Glide.with(this).load(university.logoResourceId).into(binding.imageUniversity)
                                                        return@forEach


                                                }
                                            }

                                            userList = it.data?.filter { user ->
                                                user.uid !in (roomUser?.banList ?: emptyList()) &&
                                                        user.college == userUniversity
                                            }?.toMutableList()
                                            Log.e("userList", userList.toString())
                                            if (userList.isNullOrEmpty()) {
                                                binding.nonPeople.visibility = View.VISIBLE
                                                binding.recyclerView.visibility = View.GONE
                                            } else {
                                                binding.nonPeople.visibility = View.GONE
                                                binding.recyclerView.visibility = View.VISIBLE

                                                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                                binding.recyclerView.adapter = adapter
                                                adapter.submitList(userList)


                                                adapter.onClick = { user ->
                                                    val b = Bundle().apply {
                                                        putParcelable("selectedUniversityStudent", user)
                                                    }
                                                    findNavController().navigate(
                                                        R.id.action_selectedUniversityStudentListFragment_to_universityStudentFragment,
                                                        b
                                                    )

                                                }
                                            }

//                                            Universities.universityInfoList.forEach { university ->
//                                                if (userList?.get(0) != null) {
//                                                    if (userList?.get(0)!!.college == university.nameKo) {
//                                                        val universityName = when (LanguageUtils.getDeviceLanguage(requireContext())) {
//                                                            "ko" -> university.nameKo
//                                                            "en" -> university.nameEn
//                                                            "ja" -> university.nameJa
//                                                            "zh-CN" -> university.nameZh
//                                                            "zh-TW" -> university.nameZhTw
//                                                            "es" -> university.nameEs
//                                                            "vi" -> university.nameVi
//                                                            "th" -> university.nameTh
//                                                            "in" -> university.nameIn
//                                                            else -> university.nameEn
//                                                        }
//
//                                                        binding.tvUniversity.text = universityName
////                    Glide.with(this).load(university.logoResourceId).into(binding.imageUniversity)
//                                                        return@forEach
//                                                    }
//
//                                                }
//                                            }
                                        }

                                        is Resource.Error -> {
                                            Log.e("Resource.Error", it.message.toString())


                                        }

                                        else -> Unit

                                    }
                                }
                            }
                        }
                        is Resource.Error -> {}
                        else -> Unit
                    }
                }
            }


        }


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
                        "zh-CN" -> university.nameZh
                        "zh-TW" -> university.nameZhTw
                        "es" -> university.nameEs
                        "vi" -> university.nameVi
                        "th" -> university.nameTh
                        "in" -> university.nameIn
                        else -> university.nameEn
                    }

                    binding.tvUniversity.text = universityName
//                    Glide.with(this).load(university.logoResourceId).into(binding.imageUniversity)
                    return@forEach
                }

            }
        }


        Log.e("usersList", usersList.toString())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        adapter.submitList(usersList)


        adapter.onClick = { user ->
                    val b = Bundle().apply {
                        putParcelable("selectedUniversityStudent", user)
                    }
            findNavController().navigate(
                R.id.action_selectedUniversityStudentListFragment_to_universityStudentFragment,
                b
            )

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        Log.e("onResume", "onResume")

        hideBottomNavigationView()
    }

    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        super.onPause()
        showBottomNavigationView()
        Log.e("onPause", "onPause")

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

    private fun blockUser(reportedUserId: String) {
        //TODo 지금 여기만 손보면 해결할 것 같음..

        val banList = roomUser?.banList?.toMutableList() ?: mutableListOf()
        if (!banList.contains(reportedUserId)) {
            banList.add(reportedUserId)
        }

        currentUser = currentUser?.copy(blockList = banList)
        userViewModel.updateUser(currentUser!!)

        userViewModel.setCurrentUser(
            mapOf(
                "banList" to banList
            )
        )

        Toast.makeText(requireContext(), getString(R.string.block_success), Toast.LENGTH_SHORT).show()

    }
}