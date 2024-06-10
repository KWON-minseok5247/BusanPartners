package com.kwonminseok.busanpartners.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.FAQAdapter
import com.kwonminseok.busanpartners.adapter.TabViewPagerAdapter
import com.kwonminseok.busanpartners.data.FAQItem
import com.kwonminseok.busanpartners.databinding.FragmentFaqBinding
import java.io.InputStreamReader


//@AndroidEntryPoint
//class FAQFragment : Fragment() {
//    private var _binding: FragmentFaqBinding? = null
//    private val binding get() = _binding!!
//
//
//    // 여기서 해야 할 거는 일단 room을 통해서 데이터를 가져오기 ->
//    // 만약 room에 데이터가 없다면 네트워크로부터 데이터를 가져오기 -> 가져온 데이터를 insert하기
//    // 만약 room의 데이터가 있으면 일단 그 데이터로 표시한다 -> 근데 네트워크로부터 가져온 데이터랑
//    // 다르다면 네트워크 데이터로 최신화한다. -> 그리고 데이터를 insert한다.
//    // 만약 room 데이터와 네트워크로부터 가져온 데이터가 동일하다면 굳이 업데이트는 필요 없음.
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentFaqBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val faqList = listOf(
//            Faq("트립마켓에서 코치로 활동하는 방법이 궁금해요!", "마이페이지 > 설정 > 회원 탈퇴 > 사유 선택 후 탈퇴"),
//            Faq("트립마켓에서 코치로 활동하는 방법이 궁금해요!", "마이페이지 > 설정 > 회원 탈퇴 > 사유 선택 후 탈퇴"),
//
//            Faq("트립마켓에서 코치로 활동하는 방법이 궁금해요!", "마이페이지 > 설정 > 회원 탈퇴 > 사유 선택 후 탈퇴"),
//            Faq("트립마켓에서 코치로 활동하는 방법이 궁금해요!", "마이페이지 > 설정 > 회원 탈퇴 > 사유 선택 후 탈퇴"),
//            Faq("트립마켓에서 코치로 활동하는 방법이 궁금해요!", "마이페이지 > 설정 > 회원 탈퇴 > 사유 선택 후 탈퇴"),
//            Faq("트립마켓에서 코치로 활동하는 방법이 궁금해요!", "마이페이지 > 설정 > 회원 탈퇴 > 사유 선택 후 탈퇴"),
//            Faq("트립마켓에서 코치로 활동하는 방법이 궁금해요!", "마이페이지 > 설정 > 회원 탈퇴 > 사유 선택 후 탈퇴"),
//
//            // 추가 질문과 답변
//        )
//
//        val adapter = FaqAdapter(faqList)
//        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerView.adapter = adapter
//
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//
//
//
//
//}
class FAQFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null
    private val binding get() = _binding!!
    private lateinit var faqAdapter: FAQAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val faqList = listOf(
            FAQItem("트림마켓에서 코치로 활동하는 방법이 궁금해요!", "코치로 활동하려면...", "대학생"),
            FAQItem("일정표는 코치만 만들 수 있나요?", "네, 일정표는 코치만...", "관광객"),
            FAQItem("일정표 수정이 안돼요!", "일정표 수정은...", "그 외"),
            FAQItem("탈퇴는 어떻게 하나요?", "마이페이지 > 설정 > 회원 탈퇴...", "그 외"),
            // 더 많은 FAQ 아이템 추가
        )

        val categories = listOf("전체", "대학생", "관광객", "그 외")
        val fragments = categories.map { category ->
            FAQListFragment.newInstance(category, faqList)
        }

        val viewPagerAdapter = TabViewPagerAdapter(this, fragments)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = categories[position]
        }.attach()

        binding.backImageView.setOnClickListener {
            // 뒤로 가기 버튼 클릭 시 동작
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun loadFAQItems(context: Context): List<FAQItem> {
        val inputStream = context.assets.open("json/faq_data.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<FAQItem>>() {}.type
        return Gson().fromJson(reader, type)
    }
}
