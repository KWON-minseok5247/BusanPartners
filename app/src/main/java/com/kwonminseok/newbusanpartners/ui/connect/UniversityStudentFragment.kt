package com.kwonminseok.newbusanpartners.ui.connect


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.newbusanpartners.data.TranslatedList
import com.kwonminseok.newbusanpartners.data.TranslatedText
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.databinding.FragmentUniversityStudentBinding
import com.kwonminseok.newbusanpartners.ui.login.SplashActivity.Companion.currentUser
import com.kwonminseok.newbusanpartners.util.LanguageUtils
import com.kwonminseok.newbusanpartners.util.hideBottomNavigationView
import com.kwonminseok.newbusanpartners.util.showBottomNavigationView
import com.kwonminseok.newbusanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters


private val TAG = "SelectedUniversityStudentListFragment"

@AndroidEntryPoint
class UniversityStudentFragment : Fragment() {
    private var chipTexts: MutableList<String>? = null
    private var _binding: FragmentUniversityStudentBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    //    private val viewModel by viewModels<ConnectViewModel>()
//    private val adapter by lazy { StudentCardAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUniversityStudentBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user: User? = arguments?.getParcelable("selectedUniversityStudent")
        user?.let {

            binding.backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            binding.reportButton.setOnClickListener {a ->
//                val bottomSheet = ReportBottomSheetFragment.newInstance(it)
//                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                val b = Bundle().apply {
                    putParcelable("selectedUser", user)
                }
                findNavController().navigate(
                    R.id.action_universityStudentFragment_to_reportBottomSheetFragment,
                    b
                )
            }

//            binding.reportButton.setOnClickListener {
////                findNavController().navigate(R.id.action_universityStudentFragment_to_mySheetFragment)
//                val bottomSheet = ReportBottomSheetFragment()
//                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//            }

            binding.apply {
                Glide.with(this@UniversityStudentFragment).load(user.imagePath)
                    .into(binding.imageUser)
                tvName.text = getTranslatedText(user.name)
                tvMajor.text = "${getTranslatedText(user.major)}"

                // ChipGroup 초기화
                chipGroup.removeAllViews()

                // 사용자의 취미 목록을 받아와 Chip으로 변환 후 ChipGroup에 추가
                getTranslatedList(user.chipGroup)?.forEach { hobby ->
                    val chip = Chip(requireContext()).apply {
                        text = hobby
                        isClickable = false
                        isCheckable = false
                        setChipBackgroundColorResource(R.color.chipgroup_color)
                    }
                    chipGroup.addView(chip)
                }

                tvIntroduction.text = getTranslatedText(user.introduction)
            }
        }

        // TODO 프로모션 전에는 삭제하기
        sharedPreferences.edit().putBoolean("isAgreed", false).apply()


        binding.connectButton.setOnClickListener {


            if (chatClient.getCurrentUser()!!.id == "guestID") {
                Toast.makeText(requireContext(),getString(R.string.data_changed_message), Toast.LENGTH_SHORT).show()
                requireActivity().finishAffinity()
                System.exit(0)
            }

            if (currentUser?.authentication?.collegeStudent == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.college_student_cannot_contact),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (currentUser!!.blockList?.contains(user!!.uid) == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.cannot_recontact),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            getChannelCountForUser(currentUser!!.uid) { channelCount ->
                Log.e("channelCount", channelCount.toString())
                if (channelCount >= 3) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.max_chat_rooms),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {


                    if (!sharedPreferences.getBoolean("isAgreed", false)) { // 체크를 하지 않으면 채팅을 이용할 수 없도록 미리 안내사항을 제시하는 부분
                        val b = Bundle().apply {
                            putString("studentUid", user!!.uid)
                            putString(
                                "name",
                                "${user.name?.ko}(${getTranslatedText(user.name)})"
                            )
                        }
                        findNavController().navigate(R.id.action_universityStudentFragment_to_communityGuidelinesFragment, b)


                    } else {
                        val b = Bundle().apply {
                            putString("studentUid", user!!.uid)
                            putString(
                                "name",
                                "${user.name?.ko}(${getTranslatedText(user.name)})"
                            )
                        }
                        findNavController().navigate(
                            R.id.action_universityStudentFragment_to_messageFragment,
                            b
                        )
                    }

                }
            }



        }

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
            "en" -> translatedText?.en
            "ja" -> translatedText?.ja ?: translatedText?.en
            "zh" -> translatedText?.zh ?: translatedText?.en
            "zh-TW" -> translatedText?.zh ?: translatedText?.en
            "zh-CN" -> translatedText?.zh ?: translatedText?.en
            "es" -> translatedText?.es ?: translatedText?.en

            else -> translatedText?.en
        }
    }

    private fun getTranslatedList(translatedList: TranslatedList?): List<String>? {
        val language = LanguageUtils.getDeviceLanguage(requireContext())
        Log.e("language", language)
        return when (language) {
            "ko" -> translatedList?.ko
            "en" -> translatedList?.en
            "ja" -> translatedList?.ja
            "zh" -> translatedList?.zh
            "zh-TW" -> translatedList?.zh
            "zh-CN" -> translatedList?.zh
            "es" -> translatedList?.es
            else -> translatedList?.en
        }
    }

    fun getChannelCountForUser(userId: String, callback: (Int) -> Unit) {
        val filter = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(userId))
        )

        val request = QueryChannelsRequest(
            filter = filter,
            offset = 0,
            limit = 30
        )

        chatClient.queryChannels(request).enqueue { result ->
            if (result.isSuccess) {
                val channels: List<Channel> = result.getOrNull() ?: emptyList()
                val channelCount = channels.size
                callback(channelCount)
            } else {
                try {
                    result.getOrThrow()
                } catch (e: Exception) {
                    println("Error querying channels: ${e.message}")
                }
                callback(0)
            }
        }
    }

//    private fun blockUser(reportedUserId: String) {
//        val banList = currentUser?.banList?.toMutableList() ?: mutableListOf()
//        if (!banList.contains(reportedUserId)) {
//            banList.add(reportedUserId)
//            currentUser = currentUser?.copy(banList = banList)
//            userViewModel.updateUser(currentUser!!)
//            userViewModel.setCurrentUser(mapOf("banList" to banList))
//
//            Toast.makeText(requireContext(), "차단이 완료되었습니다.", Toast.LENGTH_SHORT).show()
//            findNavController().popBackStack()
//
//            // Update UI or navigate as needed
//        }
//    }


}