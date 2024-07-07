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
import com.kwonminseok.busanpartners.util.LanguageUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader




class FAQFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null
    private val binding get() = _binding!!
    private lateinit var faqAdapter: FAQAdapter
    private val faqList = mutableListOf<FAQItem>()
    private lateinit var selectedLanguage: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedLanguage = LanguageUtils.getDeviceLanguage(requireContext())

        loadFaqsFromJson()

        val categories = listOf(
            getString(R.string.category_all),
            getString(R.string.university_student),
            getString(R.string.traveler),
            getString(R.string.category_other)
        )






        val fragments = categories.map { category ->
            FAQListFragment.newInstance(category, faqList, selectedLanguage)
        }

        val viewPagerAdapter = TabViewPagerAdapter(this, fragments)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = categories[position]
        }.attach()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadFaqsFromJson() {
        val inputStream = requireContext().assets.open("FAQList.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val faqJson = jsonArray.getJSONObject(i)
            val category = faqJson.getString("category")
            val questions = faqJson.getJSONObject("questions")
            val answers = faqJson.getJSONObject("answers")

            val question = questions.getString(selectedLanguage)
            val answer = answers.getString(selectedLanguage)

            faqList.add(FAQItem(question, answer, category))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
