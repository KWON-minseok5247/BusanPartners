package com.kwonminseok.busanpartners.mainScreen.profile

import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.univcert.api.UnivCert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CollegeAuthFragment : Fragment() {
    lateinit var binding: FragmentCollegeAuthBinding
    lateinit var selectedUniversity: String
    private var emailDomain: String = "@pukyong.ac.kr" // 기본값으로 초기화
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
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (view != null) {
                        // 여기에서 선택된 아이템에 따라 collegeEmail TextView를 업데이트합니다.
                        selectedUniversity = parent.getItemAtPosition(position).toString()
                        emailDomain = when (selectedUniversity) {
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
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedUniversity = "국립부경대학교"
                    binding.collegeEmail.text = "@pukyong.ac.kr"
                    emailDomain = "@pukyong.ac.kr"
                }
            }

        //TODO 이메일 작성은 필수 안하면 에러뜨도록
        binding.buttonSendVerificationCode.setOnClickListener {
            if (binding.editTextEmail.text.toString() == "") {
                binding.editTextEmail.error = "이메일을 입력해주세요."
            } else {
                val myEmail = binding.editTextEmail.text.toString()
                if (!Patterns.EMAIL_ADDRESS.matcher(myEmail).matches() || !binding.editTextEmail.text.endsWith(emailDomain)) {
                    // 이메일 형식이 유효하지 않으면, 에러 메시지를 설정합니다.
                    binding.editTextEmail.error = "올바른 이메일 주소(${emailDomain})를 입력하세요."
                } else {
                    // 이메일 형식이 유효하면, 특정 작업을 수행합니다. 예를 들어, 에러 메시지를 지웁니다.
                    binding.editTextEmail.error = null
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            //todo 여기서 잠깐의 로딩이 있는게 더 자연스럽겠다.
//                            UnivCert.certify(BuildConfig.COLLEGE_KEY, myEmail, selectedUniversity, false)
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


    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }

    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        showBottomNavigationView()
        super.onPause()
    }

}