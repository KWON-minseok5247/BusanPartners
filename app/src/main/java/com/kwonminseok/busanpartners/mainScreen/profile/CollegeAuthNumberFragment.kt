package com.kwonminseok.busanpartners.mainScreen.profile

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthBinding
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthNumberBinding
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.univcert.api.UnivCert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//import com.univcert.api.UnivCert

private val TAG = "CollegeAuthNumberFragment"
class CollegeAuthNumberFragment : Fragment() {
    lateinit var binding: FragmentCollegeAuthNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCollegeAuthNumberBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 첫 번째 EditText에 자동 포커스 설정
        binding.editTextCode1.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.editTextCode1, InputMethodManager.SHOW_IMPLICIT)

        // 숫자 입력 시 다음 EditText로 포커스 이동
        val editTexts = listOf(
            binding.editTextCode1,
            binding.editTextCode2,
            binding.editTextCode3,
            binding.editTextCode4
        )
        // TODO 입력을 끝까지 해야 삭제가 되고 삭제를 끝까지 해야 입력이 되는 문제 발생
        editTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before > count) {
                        // 사용자가 백스페이스를 눌러서 문자를 삭제했을 때
                        if (index > 0) {
                            editTexts[index - 1].requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // 숫자를 입력할 때 다음 EditText로 넘어가는 과정
                    if (s != null && s.length == 1 && index < editTexts.size - 1) {
                        editTexts[index + 1].requestFocus()
                    }
                    // 숫자를 삭제할 때 이전 EditText로 돌아가는 과정
                    if (s != null) {
                        if (s.length == 1 && index < editTexts.size - 1) {
                            editTexts[index + 1].requestFocus()
                        } else if (s.isEmpty() && index > 0) {
                            editTexts[index - 1].requestFocus()
                        }
                    }

                }
                // beforeTextChanged와 onTextChanged 구현 생략
            })

        }
        val collegeData = arguments?.getParcelable<CollegeData>("collegeData")

        binding.buttonSendVerificationCode.setOnClickListener {
            //TODO  실제에서는 코드 4자리를 모두 채워야 한다.
            val code1 = binding.editTextCode1.text.toString()
            val code2 = binding.editTextCode2.text.toString()
            val code3 = binding.editTextCode3.text.toString()
            val code4 = binding.editTextCode4.text.toString()

            val combinedCode = "$code1$code2$code3$code4"

// String을 Int로 변환, 입력값이 빈 문자열이 아닐 경우에만 변환
            val codeAsInt = combinedCode.toIntOrNull() ?: 0
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    UnivCert.certifyCode(BuildConfig.COLLEGE_KEY, collegeData?.email, collegeData?.selectedUniversity, codeAsInt)
                    val isSuccessful = UnivCert.status(BuildConfig.API_KEY, collegeData?.email)["success"].toString()

                    if(isSuccessful == "true") {
                        //TODO 여기서 getstream 토큰을 받는 것이 맞는 것 같은데?
                    }




                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }




    }
}