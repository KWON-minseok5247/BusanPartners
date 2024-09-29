package com.kwonminseok.newbusanpartners.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.api.TourismAllInOneApiService
import com.kwonminseok.newbusanpartners.application.BusanPartners.Companion.preferences
import com.kwonminseok.newbusanpartners.databinding.FragmentTranslateBinding
import com.kwonminseok.newbusanpartners.ui.HomeActivity
import com.kwonminseok.newbusanpartners.util.LanguageUtils
import com.kwonminseok.newbusanpartners.util.PreferenceUtil
import java.util.Locale

class LanguageFragment : Fragment() {

    private var _binding: FragmentTranslateBinding? = null
    private val binding get() = _binding!!
    private lateinit var languages: List<Pair<String, Locale>>
    private var selectedPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTranslateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        languages = listOf(
            Pair(getString(R.string.system_default_language), Locale.getDefault()), //zh_TW_#Hant
            Pair("한국어", Locale.KOREAN),
            Pair("English", Locale.ENGLISH),
            Pair("繁體中文", Locale.TRADITIONAL_CHINESE),
            Pair("简体中文", Locale.SIMPLIFIED_CHINESE), //zh_CN
            Pair("日本語", Locale.JAPANESE),
            Pair("Español", Locale("es")),
            Pair("Tiếng Việt", Locale("vi")),
            Pair("ไทย", Locale("th")),
            Pair("Bahasa Indonesia", Locale("in")),
        )

        val currentLocale = getCurrentLocale(preferences)
        Log.e("언어 프래그먼트 currentLocale", currentLocale.toLanguageTag().toString()) //zh-Hant-TW
        Log.e("시스템 언어?", LanguageUtils.getDeviceLanguage(requireContext())) //zh-CN

        selectedPosition = getSelectedPosition(currentLocale)

        populateLanguageOptions()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.r9rymzh6imkh.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun populateLanguageOptions() {
        val languageContainer = binding.languageContainer
        languageContainer.removeAllViews()
        for ((index, language) in languages.withIndex()) {
            val languageView =
                layoutInflater.inflate(R.layout.language_item, languageContainer, false)
            val textView = languageView.findViewById<TextView>(R.id.itemTextView)
            val radioButton = languageView.findViewById<RadioButton>(R.id.radioButton)

            textView.text = language.first
            radioButton.isChecked = index == selectedPosition

            languageView.setOnClickListener {
                updateSelectedPosition(index)
            }

            radioButton.setOnClickListener {
                updateSelectedPosition(index)
            }

            languageContainer.addView(languageView)
        }
    }

    private fun updateSelectedPosition(position: Int) {
        if (selectedPosition != position) {
            selectedPosition = position
            Log.e("selectedPosition", selectedPosition.toString())
            updateRadioButtonStates()
            val selectedLocale = languages[selectedPosition].second
            Log.e("selectedLocale", selectedLocale.toString())
            updateLocale(selectedLocale)
        }
    }

    private fun updateRadioButtonStates() {
        for (i in 0 until binding.languageContainer.childCount) {
            val child = binding.languageContainer.getChildAt(i)
            val radioButton = child.findViewById<RadioButton>(R.id.radioButton)
            radioButton.isChecked = i == selectedPosition
        }
    }

    private fun updateLocale(locale: Locale) {
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        saveLocale(locale)
        TourismAllInOneApiService.init(requireContext(), forceRefresh = true)
        populateLanguageOptions()

        (activity as? HomeActivity)?.recreateActivity() // Activity 재생성

    }

    private fun saveLocale(locale: Locale) {
        val localeString = if (locale == Locale.getDefault()) "" else locale.toLanguageTag()
        preferences.setString("selected_locale", localeString)
    }

    private fun getCurrentLocale(preferences: PreferenceUtil): Locale {
        val localeString = preferences.getString("selected_locale", "")
        Log.e("localeString", localeString.toString())
        return if (localeString.isEmpty()) Locale.getDefault() else Locale.forLanguageTag(
            localeString
        )
    }

    private fun getSelectedPosition(currentLocale: Locale): Int {
        val localeString = preferences.getString("selected_locale", "")
        return if (localeString.isEmpty()) {
            0 // 시스템 기본 언어
        } else {
            // 사용자가 선택한 언어와 매칭
            languages.indexOfFirst { it.second.toLanguageTag() == currentLocale.toLanguageTag() }
                .takeIf { it >= 0 } ?: 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}