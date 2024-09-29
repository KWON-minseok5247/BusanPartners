package com.kwonminseok.newbusanpartners.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.adapter.FAQAdapter
import com.kwonminseok.newbusanpartners.data.FAQItem
import com.kwonminseok.newbusanpartners.databinding.FragmentFaqListBinding

//class FAQListFragment : Fragment() {
//
//    private var category: String? = null
//    private lateinit var faqAdapter: FAQAdapter
//    private var faqList: List<FAQItem> = listOf()
//    private var _binding: FragmentFaqListBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentFaqListBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        category = arguments?.getString(ARG_CATEGORY)
//        faqList = arguments?.getParcelableArrayList(ARG_FAQ_LIST) ?: listOf()
//
//        val filteredList = if (category == "전체") {
//            faqList
//        } else {
//            faqList.filter { it.category == category }
//        }
//
//        faqAdapter = FAQAdapter(filteredList)
//        binding.recyclerView.layoutManager = LinearLayoutManager(context)
//        binding.recyclerView.adapter = faqAdapter
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    companion object {
//        private const val ARG_CATEGORY = "category"
//        private const val ARG_FAQ_LIST = "faq_list"
//
//        fun newInstance(category: String, faqList: List<FAQItem>): FAQListFragment {
//            val fragment = FAQListFragment()
//            val args = Bundle().apply {
//                putString(ARG_CATEGORY, category)
//                putParcelableArrayList(ARG_FAQ_LIST, ArrayList(faqList))
//            }
//            fragment.arguments = args
//            return fragment
//        }
//    }
//}

class FAQListFragment : Fragment() {

    private var category: String? = null
    private lateinit var faqAdapter: FAQAdapter
    private var faqList: List<FAQItem> = listOf()
    private var _binding: FragmentFaqListBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedLanguage: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFaqListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category = arguments?.getString(ARG_CATEGORY)
        faqList = arguments?.getParcelableArrayList(ARG_FAQ_LIST) ?: listOf()
        selectedLanguage = arguments?.getString(ARG_LANGUAGE) ?: "en"

        val localizedCategory = getLocalizedCategory(requireContext(), category ?: "", selectedLanguage)

        val filteredList = if (localizedCategory == getString(R.string.category_all)) {
            faqList
        } else {
            faqList.filter { getLocalizedCategory(requireContext(), it.category, selectedLanguage) == localizedCategory }
        }

        faqAdapter = FAQAdapter(filteredList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = faqAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORY = "category"
        private const val ARG_FAQ_LIST = "faq_list"
        private const val ARG_LANGUAGE = "language"

        fun newInstance(category: String, faqList: List<FAQItem>, language: String): FAQListFragment {
            val fragment = FAQListFragment()
            val args = Bundle().apply {
                putString(ARG_CATEGORY, category)
                putParcelableArrayList(ARG_FAQ_LIST, ArrayList(faqList))
                putString(ARG_LANGUAGE, language)
            }
            fragment.arguments = args
            return fragment
        }
    }

    // Add this function to map the category name in different languages
    fun getLocalizedCategory(context: Context, category: String, language: String): String {
        return when (category) {
            "전체" -> context.getString(R.string.category_all)
            "대학생" -> context.getString(R.string.university_student)
            "관광객" -> context.getString(R.string.traveler)
            "그 외" -> context.getString(R.string.category_other)
            else -> category
        }
    }

}
