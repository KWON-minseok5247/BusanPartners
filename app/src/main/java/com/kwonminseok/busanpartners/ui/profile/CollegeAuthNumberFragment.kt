package com.kwonminseok.busanpartners.ui.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.barnea.dialoger.Dialoger
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthNumberBinding
import com.kwonminseok.busanpartners.util.Constants.COLLEGE_DATA
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OTPListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


//import com.univcert.api.UnivCert

private val TAG = "CollegeAuthNumberFragment"

@AndroidEntryPoint
class CollegeAuthNumberFragment : Fragment() {

    private var _binding: FragmentCollegeAuthNumberBinding? = null
    private val binding get() = _binding!!


    //    private val viewModel by viewModels<AuthenticationInformationViewModel>()
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // 뒤로 가기 버튼 커스텀 동작을 설정합니다.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(
                    context,
                    "인증번호 확인 중에는 뒤로 가실 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCollegeAuthNumberBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {
                // fired when user has entered the OTP fully
                Log.e("onOTPComplete", otp)
//                verifyOtp(otp)
            }
        }




//        // 첫 번째 EditText에 자동 포커스 설정
//        binding.editTextCode1.requestFocus()
//        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//        imm?.showSoftInput(binding.editTextCode1, InputMethodManager.SHOW_IMPLICIT)
//
//        // 숫자 입력 시 다음 EditText로 포커스 이동
//        val editTexts = listOf(
//            binding.editTextCode1,
//            binding.editTextCode2,
//            binding.editTextCode3,
//            binding.editTextCode4
//        )
//
//        editTexts.forEachIndexed { index, editText ->
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    // 아무 동작도 필요 없음
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    // 숫자를 입력할 때 다음 EditText로 넘어가는 과정
//                    if (s != null && s.length == 1 && index < editTexts.size - 1) {
//                        editTexts[index + 1].requestFocus()
//                    } else if (s.isNullOrEmpty() && before == 1 && index > 0) {
//                        // 숫자를 삭제할 때 이전 EditText로 돌아가는 과정
//                        editTexts[index - 1].requestFocus()
//                    }
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//                    // 아무 동작도 필요 없음
//                }
//            })
//        }


        val collegeData = arguments?.getParcelable<CollegeData>(COLLEGE_DATA)

        binding.buttonSendVerificationCode.setOnClickListener {

            val dialog = Dialoger(requireContext(), Dialoger.TYPE_LOADING)
                .setTitle("로딩중...")
                .setDescription("인증번호를 확인하고 있습니다.")
                .setProgressBarColor(R.color.black)
                .show()

// Dismiss the loading dialog after 5 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss();
            }, 500)



            // String을 Int로 변환, 입력값이 빈 문자열이 아닐 경우에만 변환
            val codeAsInt = binding.otpView.otp?.toIntOrNull() ?: 0
            Log.e("codeAsInt", codeAsInt.toString())
            lifecycleScope.launchWhenStarted {
                try {
                    //TODo 여기서도 1초간 로딩이 있었으면 좋겠다.
//                    UnivCert.certifyCode(BuildConfig.COLLEGE_KEY, collegeData?.email, collegeData?.selectedUniversity, codeAsInt)
//                    val isSuccessful = UnivCert.status(BuildConfig.API_KEY, collegeData?.email)["success"].toString()
                    val isSuccessful = "true"
                    if (isSuccessful == "true") {
                        //TODO 여기서 getstream 토큰을 받는 것이 맞는 것 같은데?
                        // 아니면 isCollegeStudent 이거를 true로 만들고 messageFragment에서 먼저 클릭할 때 isCollegeStudent라던지
                        // 확인후 클릭할 수 있도록
                        val university = collegeData!!.selectedUniversity
                        val universityEmail = collegeData.email

                        val userUpdates = mapOf(
                            "college" to university,
                            "universityEmail" to universityEmail,
                            "authentication.studentEmailAuthenticationComplete" to true
                        )

                        viewModel.setCurrentUser(userUpdates)

                        val bundle = Bundle()
                        bundle.putBoolean("isVerified", true)
                        findNavController().navigate(
                            R.id.action_collegeAuthNumberFragment_to_collegeAuthCompleteFragment,
                            bundle
                        )
                    } else {
                        val bundle = Bundle()
                        bundle.putBoolean("isVerified", false)
                        findNavController().navigate(
                            R.id.action_collegeAuthNumberFragment_to_collegeAuthCompleteFragment,
                            bundle
                        )

                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        // 첫 번째 EditText에 자동 포커스 설정
//        binding.editTextCode1.requestFocus()
//        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//        imm?.showSoftInput(binding.editTextCode1, InputMethodManager.SHOW_IMPLICIT)
//
//        // 숫자 입력 시 다음 EditText로 포커스 이동
//        val editTexts = listOf(
//            binding.editTextCode1,
//            binding.editTextCode2,
//            binding.editTextCode3,
//            binding.editTextCode4
//        )
//        // TODO 입력을 끝까지 해야 삭제가 되고 삭제를 끝까지 해야 입력이 되는 문제 발생
//        editTexts.forEachIndexed { index, editText ->
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    if (before > count) {
//                        // 사용자가 백스페이스를 눌러서 문자를 삭제했을 때
//                        if (index > 0) {
//                            editTexts[index - 1].requestFocus()
//                        }
//                    }
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//                    // 숫자를 입력할 때 다음 EditText로 넘어가는 과정
//                    if (s != null && s.length == 1 && index < editTexts.size - 1) {
//                        editTexts[index + 1].requestFocus()
//                    }
//                    // 숫자를 삭제할 때 이전 EditText로 돌아가는 과정
//                    if (s != null) {
//                        if (s.length == 1 && index < editTexts.size - 1) {
//                            editTexts[index + 1].requestFocus()
//                        } else if (s.isEmpty() && index > 0) {
//                            editTexts[index - 1].requestFocus()
//                        }
//                    }
//
//                }
//                // beforeTextChanged와 onTextChanged 구현 생략
//            })
//
//        }
//        val collegeData = arguments?.getParcelable<CollegeData>(COLLEGE_DATA)
//
//        binding.buttonSendVerificationCode.setOnClickListener {
//            //TODO  실제에서는 코드 4자리를 모두 채워야 한다.
//            val code1 = binding.editTextCode1.text.toString()
//            val code2 = binding.editTextCode2.text.toString()
//            val code3 = binding.editTextCode3.text.toString()
//            val code4 = binding.editTextCode4.text.toString()
//
//            val combinedCode = "$code1$code2$code3$code4"
//
//// String을 Int로 변환, 입력값이 빈 문자열이 아닐 경우에만 변환
//            val codeAsInt = combinedCode.toIntOrNull() ?: 0
//            lifecycleScope.launchWhenStarted {
//                try {
//                    //TODo 여기서도 1초간 로딩이 있었으면 좋겠다.
////                    UnivCert.certifyCode(BuildConfig.COLLEGE_KEY, collegeData?.email, collegeData?.selectedUniversity, codeAsInt)
////                    val isSuccessful = UnivCert.status(BuildConfig.API_KEY, collegeData?.email)["success"].toString()
//                    val isSuccessful = "true"
//                    if (isSuccessful == "true") {
//                        //TODO 여기서 getstream 토큰을 받는 것이 맞는 것 같은데?
//                        // 아니면 isCollegeStudent 이거를 true로 만들고 messageFragment에서 먼저 클릭할 때 isCollegeStudent라던지
//                        // 확인후 클릭할 수 있도록
//                        val university = collegeData!!.selectedUniversity
//                        val universityEmail = collegeData!!.email
//
//                        val userUpdates = mapOf(
//                            "college" to university,
//                            "universityEmail" to universityEmail,
//                            "authentication.studentEmailAuthenticationComplete" to true
//                        )
//
//                        viewModel.setCurrentUser(userUpdates)
//
//                        val bundle = Bundle()
//                        bundle.putBoolean("isVerified", true)
//                        findNavController().navigate(
//                            R.id.action_collegeAuthNumberFragment_to_collegeAuthCompleteFragment,
//                            bundle
//                        )
//                    } else {
//                        val bundle = Bundle()
//                        bundle.putBoolean("isVerified", false)
//                        findNavController().navigate(
//                            R.id.action_collegeAuthNumberFragment_to_collegeAuthCompleteFragment,
//                            bundle
//                        )
//
//                    }
//
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//        }
//
//
//    }

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