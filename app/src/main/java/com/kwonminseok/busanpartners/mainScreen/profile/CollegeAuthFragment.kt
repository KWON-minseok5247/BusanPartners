package com.kwonminseok.busanpartners.mainScreen.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.ImagesAdapter
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthBinding
import com.kwonminseok.busanpartners.util.Constants.STUDENT
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.AuthenticationInformationViewModel
import com.kwonminseok.busanpartners.viewmodel.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs


@AndroidEntryPoint
class CollegeAuthFragment : Fragment() {
    lateinit var binding: FragmentCollegeAuthBinding
    lateinit var selectedUniversity: String
    private val viewModel by viewModels<AuthenticationViewModel>()
    private val authenticationCollegeViewModel by viewModels<AuthenticationInformationViewModel>()
    private var emailDomain: String = "@pukyong.ac.kr" // 기본값으로 초기화
    private val REQUEST_CODE_IMAGE_PICK = 1000
    lateinit var imagesAdapter: ImagesAdapter

    // 갤러리에 있는 이미지를 URI로 저장할 리스트
    private val imageUris = ArrayList<Uri>()


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



        // 얘는 일단 user 데이터를 불러오고 만약 학생증 사진이 이미 올라와져있다면 굳이 사진을 올릴 필요가 없으니까. 사진은 그대로 올려두되
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showProgressBar()
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        // 얘도 함수로 만드는 게 맞고.
                        Log.e("제일제일 중요", it.data.toString())
                        if (it.data?.authentication?.studentIdentificationCard != null) {
                            // TODO 이미지 불러오고 삭제하는 기능을 효율적으로 만들지 않았다. 나중에 따로 시간나면 다시 만들자.
                            val urlList = it.data?.authentication?.studentIdentificationCard
                            imagesAdapter = ImagesAdapter(requireContext(), urlList).apply {
                                onClick = { imageUrl, position ->
                                    Log.e("click을 했을 때", "${imageUrl} ${position}")
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("삭제 확인")
                                        .setMessage("정말 삭제하시겠습니까?")
                                        .setPositiveButton("삭제") { dialog, which ->
                                            lifecycleScope.launch {
                                                viewModel.deleteImageFromStorage(imageUrl)
                                                viewModel.deleteImageFromDatabase(imageUrl, STUDENT)
                                            }
                                        }
                                        .setNegativeButton("취소", null)
                                        .show()


                                }
                            }
                            binding.viewPagerImages.adapter = imagesAdapter
                            binding.viewPagerImages.adapter?.notifyDataSetChanged() // 어댑터에 데이터 변경 알림

                        }
                        if (it.data?.authentication?.studentEmailAuthenticationComplete == true) {
                            Log.e("이메일 인증이 되어야 하는데?", "이유?")
                            binding.apply {
                                spinnerUniversityEmail.visibility = View.GONE
                                collegeEmail.visibility = View.GONE
                                editTextEmail.visibility = View.GONE
                                buttonSendVerificationCode.visibility = View.GONE
                                authenticationComplete.visibility = View.VISIBLE
                            }
                        }
                        if (it.data?.authentication?.authenticationStatus == "loading") {
                            binding.btnSendAllData.isClickable = false
                            binding.btnOpenGallery.isClickable = false
                        }




                        else {
                            Log.e("이메일 인증이 되어야 하는데?", "${it.data?.authentication?.studentEmailAuthenticationComplete}")
                            binding.btnSendAllData.isClickable = true
                            binding.btnOpenGallery.isClickable = true

                        }

                    }

                    is Resource.Error -> {
                        hideProgressBar()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

        getUniversitySpinner()


        //TODO 이메일 작성은 필수 안하면 에러뜨도록
        binding.buttonSendVerificationCode.setOnClickListener {
            if (binding.editTextEmail.text.toString() == "") {
                binding.editTextEmail.error = "이메일을 입력해주세요."
            } else {
                val myEmail = binding.editTextEmail.text.toString()
                if (!Patterns.EMAIL_ADDRESS.matcher(myEmail)
                        .matches() || !binding.editTextEmail.text.endsWith(emailDomain)
                ) {
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
                }
            }
        }


        // 이미지 버튼 클릭 시
        binding.btnOpenGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(
                Intent.createChooser(intent, "사진을 선택하세요"),
                REQUEST_CODE_IMAGE_PICK
            )
        }

        // 관리하는 페이지 수. default = 1
        binding.viewPagerImages.offscreenPageLimit = 4
        // item_view 간의 양 옆 여백을 상쇄할 값
        val offsetBetweenPages =
            resources.getDimensionPixelOffset(R.dimen.offsetBetweenPages).toFloat()
        binding.viewPagerImages.setPageTransformer { page, position ->
            val myOffset = position * -(2 * offsetBetweenPages)
            if (position < -1) {
                page.translationX = -myOffset
            } else if (position <= 1) {
                // Paging 시 Y축 Animation 배경색을 약간 연하게 처리
                val scaleFactor = 0.95f.coerceAtLeast(1 - abs(position))
                page.translationX = myOffset
                page.scaleY = scaleFactor
                page.alpha = scaleFactor
            } else {
                page.alpha = 0f
                page.translationX = myOffset
            }
        }


        // 프로필 프래그먼트로 돌아가는 함수
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_collegeAuthFragment_to_profileFragment)
        }


        //TODO 데이터들이 전부 갖춰져야 클릭을 할 수 있다거나 버튼이 보여선 안됨. 지금은 테스트용이라 냅둔다.
        binding.btnSendAllData.setOnClickListener {
            authenticationCollegeViewModel.attachToAuthenticationFolder(STUDENT)
        }
        // firebase 폴더를 따로 만들어 uid와 status를 알림


    }











    private fun getUniversitySpinner() {
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
    }

    // 사진을 처리하는 과정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK) {
            imageUris.clear() // 기존 목록을 클리어

            // 선택한 이미지 처리
            if (data?.clipData != null) { // 여러 이미지 선택 처리
                val clipData = data.clipData!!
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    imageUris.add(imageUri)
                }
            } else if (data?.data != null) { // 단일 이미지 선택 처리
                val imageUri = data.data!!
                imageUris.add(imageUri)
            }

            binding.viewPagerImages.adapter?.notifyDataSetChanged() // 어댑터에 데이터 변경 알림

            // 데이터를 파이어베이스에 저장하는 과정
            lifecycleScope.launch {
                viewModel.processUserImageSelection(imageUris, STUDENT)
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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }


}