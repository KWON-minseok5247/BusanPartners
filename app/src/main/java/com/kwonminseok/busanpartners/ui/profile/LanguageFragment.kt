package com.kwonminseok.busanpartners.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alertview.lib.AlertView
import com.alertview.lib.OnItemClickListener
import com.barnea.dialoger.Dialoger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.LanguageAdapter
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentTranslateBinding
import com.kwonminseok.busanpartners.databinding.FragmentUnregisterBinding
import com.kwonminseok.busanpartners.extensions.setStatusBarTransparent
import com.kwonminseok.busanpartners.extensions.setStatusBarVisible
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.extensions.toUser
import com.kwonminseok.busanpartners.ui.login.LoginRegisterActivity
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Filters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class LanguageFragment : Fragment() {

    private var _binding: FragmentTranslateBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
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

        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        languages = listOf(
            Pair("시스템 기본 언어", Locale.getDefault()),
            Pair("한국어", Locale.KOREAN),
            Pair("English", Locale.ENGLISH),
            Pair("繁體中文", Locale.TRADITIONAL_CHINESE),
            Pair("简体中文", Locale.SIMPLIFIED_CHINESE),
            Pair("日本語", Locale.JAPANESE),
            Pair("ไทย", Locale("th")),
            Pair("Tiếng Việt", Locale("vi")),
            Pair("Bahasa Indonesia", Locale("in")),
            Pair("Filipino", Locale("fil")),
            Pair("Español", Locale("es"))
        )

        // 기본적으로 선택된 언어의 위치를 찾기
        val currentLocale = getCurrentLocale()
        selectedPosition = if (currentLocale == Locale.getDefault()) 0 else languages.indexOfFirst { it.second == currentLocale }

        // 언어 선택 뷰 추가
        val languageContainer = binding.languageContainer
        for ((index, language) in languages.withIndex()) {
            val languageView = layoutInflater.inflate(R.layout.language_item, languageContainer, false)
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

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.r9rymzh6imkh.setOnClickListener {
            activity?.recreate()
        }
    }

    private fun updateSelectedPosition(position: Int) {
        if (selectedPosition != position) {
            selectedPosition = position
            for (i in 0 until binding.languageContainer.childCount) {
                val child = binding.languageContainer.getChildAt(i)
                val radioButton = child.findViewById<RadioButton>(R.id.radioButton)
                radioButton.isChecked = i == selectedPosition
            }
            val selectedLocale = languages[selectedPosition].second
            updateLocale(selectedLocale)
        }
    }

    private fun updateLocale(locale: Locale) {
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        saveLocale(locale)
    }

    private fun saveLocale(locale: Locale) {
        with(sharedPreferences.edit()) {
            putString("selected_locale", locale.toLanguageTag())
            apply()
        }
    }

    private fun getCurrentLocale(): Locale {
        val localeString = sharedPreferences.getString("selected_locale", Locale.getDefault().toLanguageTag())
        return if (localeString.isNullOrEmpty()) Locale.getDefault() else Locale.forLanguageTag(localeString)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}