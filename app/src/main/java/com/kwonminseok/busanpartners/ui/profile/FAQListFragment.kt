package com.kwonminseok.busanpartners.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.FAQAdapter
import com.kwonminseok.busanpartners.data.FAQItem
import com.kwonminseok.busanpartners.databinding.FragmentFaqListBinding

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

        val filteredList = if (category == getString(R.string.category_all)) {
            faqList
        } else {
            faqList.filter { it.category == category }
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
}
