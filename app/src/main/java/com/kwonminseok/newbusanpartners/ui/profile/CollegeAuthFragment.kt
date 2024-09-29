package com.kwonminseok.newbusanpartners.ui.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.barnea.dialoger.Dialoger
import com.kwonminseok.newbusanpartners.BuildConfig
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.data.CollegeData
import com.kwonminseok.newbusanpartners.data.Universities
import com.kwonminseok.newbusanpartners.databinding.FragmentCollegeAuthBinding
import com.kwonminseok.newbusanpartners.util.hideBottomNavigationView
import com.kwonminseok.newbusanpartners.util.showBottomNavigationView
import com.univcert.api.UnivCert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CollegeAuthFragment : Fragment() {

    private var _binding: FragmentCollegeAuthBinding? = null
    private val binding get() = _binding!!

    lateinit var selectedUniversity: String
    private var emailDomain: String = "@pukyong.ac.kr" // 기본값으로 초기화

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCollegeAuthBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 얘는 일단 user 데이터를 불러오고 만약 학생증 사진이 이미 올라와져있다면 굳이 사진을 올릴 필요가 없으니까. 사진은 그대로 올려두되

        getUniversitySpinner()


        //TODO 이메일 작성은 필수 안하면 에러뜨도록
        binding.buttonSendVerificationCode.setOnClickListener {
            if (binding.editTextEmail.text.toString() == "") {
                binding.editTextEmail.error = getString(R.string.error_email_required)
            } else {
                val myEmail = binding.editTextEmail.text.toString()
                if (!Patterns.EMAIL_ADDRESS.matcher(myEmail)
                        .matches() || !binding.editTextEmail.text?.endsWith(emailDomain)!!
                ) {
                    // 이메일 형식이 유효하지 않으면, 에러 메시지를 설정합니다.
                    binding.editTextEmail.error = getString(R.string.error_invalid_email, emailDomain)
                } else {
                    // 이메일 형식이 유효하면, 특정 작업을 수행합니다. 예를 들어, 에러 메시지를 지웁니다.
                    binding.editTextEmail.error = null

                    val dialog = Dialoger(requireContext(), Dialoger.TYPE_LOADING)
                        .setTitle(getString(R.string.verification_code_sending))
                        .setDescription(getString(R.string.please_wait))
//                        .setDrawable(R.drawable.loading)
                        .setProgressBarColor(R.color.black)
                        .show()


                    Handler(Looper.getMainLooper()).postDelayed({
                        dialog.dismiss();
                        UnivCert.certify(BuildConfig.COLLEGE_KEY, myEmail, selectedUniversity, false)
                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val b = Bundle().apply {
                                    putParcelable(
                                        "collegeData",
                                        CollegeData(myEmail, selectedUniversity)
                                    )
                                }
                                findNavController().navigate(
                                    R.id.action_collegeAuthFragment_to_collegeAuthNumberFragment,
                                    b
                                )


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }, 2000)


                }
            }
        }


        // 프로필 프래그먼트로 돌아가는 함수
//        binding.backButton.setOnClickListener {
//            findNavController().navigate(R.id.action_collegeAuthFragment_to_profileFragment)
//
//        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun getUniversitySpinner() {
        val universityArray = mutableListOf<String>()

//        val language = LanguageUtils.getDeviceLanguage(requireContext())


        Universities.universityInfoList.forEach {
            universityArray.add(it.nameKo)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            universityArray
        )


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerUniversityEmail.adapter = adapter
        binding.spinnerUniversityEmail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedUniversity = parent.getItemAtPosition(position).toString()

                // 선택된 대학교 이름으로 대학교 정보를 가져옵니다.
                val universityInfo = Universities.getInfoByName(selectedUniversity, "ko")

                // 가져온 대학교 정보를 바탕으로 이메일 도메인을 설정합니다.
                emailDomain = universityInfo?.email ?: ""
                binding.collegeEmail.text = emailDomain
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택이 해제되었을 때의 기본 동작을 정의합니다.
                // 예시로 국립부경대학교의 정보를 기본값으로 설정합니다.
                val defaultUniversityInfo = Universities.getInfoByName("국립부경대학교", "ko")
                binding.collegeEmail.text = defaultUniversityInfo?.email ?: ""
            }
        }
    }

    // 사진을 처리하는 과정
    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }

    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        showBottomNavigationView()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}