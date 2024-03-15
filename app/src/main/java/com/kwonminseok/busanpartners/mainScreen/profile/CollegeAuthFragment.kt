package com.kwonminseok.busanpartners.mainScreen.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthBinding
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.univcert.api.UnivCert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CollegeAuthFragment : Fragment() {
    lateinit var binding: FragmentCollegeAuthBinding
    lateinit var selectedUniversity: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCollegeAuthBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.university_list,
            android.R.layout.simple_spinner_item
        )

// 드롭다운 레이아웃 설정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// 어댑터를 스피너에 설정
        binding.spinnerUniversityEmail.adapter = adapter
// Spinner의 아이템이 선택되었을 때의 리스너를 설정합니다.
        binding.spinnerUniversityEmail.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    // 여기에서 선택된 아이템에 따라 collegeEmail TextView를 업데이트합니다.
                    selectedUniversity = parent.getItemAtPosition(position).toString()
                    val emailDomain = when (selectedUniversity) {
                        "국립부경대학교" -> "@pukyong.ac.kr"
                        "부산교육대학교" -> "@bnue.ac.kr"
                        "부산대학교" -> "@pusan.ac.kr"
                        "한국방송통신대학교" -> "@knou.ac.kr"
                        "국립한국해양대학교" -> "@kmou.ac.kr"
                        "경성대학교" -> "@ks.ac.kr"
                        "고신대학교" -> "@kosin.ac.kr"
                        "동명대학교" -> "@tu.ac.kr"
                        "동서대학교" -> "@dongseo.ac.kr"
                        "동아대학교" -> "@donga.ac.kr"
                        "동의대학교" -> "@deu.ac.kr"
                        "부산가톨릭대학교" -> "@cup.ac.kr"
                        "부산외국어대학교" -> "@bufs.ac.kr"
                        "신라대학교" -> "silla.ac.kr"
                        "영산대학교" -> "@ysu.ac.kr"
                        "인제대학교" -> "@inje.ac.kr"
                        else -> ""

                    }
                    binding.collegeEmail.text = emailDomain
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedUniversity = "국립부경대학교"
                    binding.collegeEmail.text = "@pukyong.ac.kr"
                }
            }

        //TODO 이메일 작성은 필수 안하면 에러뜨도록
        binding.buttonSendVerificationCode.setOnClickListener {
            val myEmail = binding.editTextEmail.text.toString()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    UnivCert.certify(BuildConfig.COLLEGE_KEY, myEmail, selectedUniversity, false)
                    val b = Bundle().apply {
                        putParcelable("collegeData", CollegeData(myEmail,selectedUniversity))
                    }
                    findNavController().navigate(R.id.action_collegeAuthFragment_to_collegeAuthNumberFragment, b)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }
}