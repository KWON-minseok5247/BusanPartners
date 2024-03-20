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
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.ImageAdapter
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthBinding
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.util.MarginItemDecoration
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.AuthenticationViewModel
import com.kwonminseok.busanpartners.viewmodel.ProfileViewModel
import com.univcert.api.UnivCert
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
    private var emailDomain: String = "@pukyong.ac.kr" // 기본값으로 초기화
    private val REQUEST_CODE_IMAGE_PICK = 1000

    // 갤러리에 있는 이미지를 URI로 저장할 리스트
    private val imageUris = ArrayList<Uri>()

    // 업로드된 이미지 URL을 저장할 리스트
    val uploadedImageUrls = ArrayList<String>()

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

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Success -> {
                        hideProgressBar()
//                        fetchUserData(it.data!!)

                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


        // 이미지 버튼 클릭 시
        binding.btnOpenGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "사진을 선택하세요"), REQUEST_CODE_IMAGE_PICK)
        }

//        binding.viewPagerImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val imageAdapter = ImageAdapter(imageUris)
        binding.viewPagerImages.adapter = imageAdapter


        // 관리하는 페이지 수. default = 1
        binding.viewPagerImages.offscreenPageLimit = 4
        // item_view 간의 양 옆 여백을 상쇄할 값
        val offsetBetweenPages = resources.getDimensionPixelOffset(R.dimen.offsetBetweenPages).toFloat()
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

        binding.btnSaveStudentIdentificationCard.setOnClickListener {
            lifecycleScope.launch {
                viewModel.processUserImageSelection(imageUris)
            }
        }





//        binding.viewPagerImages.apply {
//            // 페이지 간 여백 설정
//            val spaceSize = resources.getDimensionPixelSize(R.dimen.pageMargin)
//            addItemDecoration(MarginItemDecoration(spaceSize))
//
//            // 현재 페이지와 양 옆 페이지가 동시에 보이도록 설정
//            setPageTransformer { page, position ->
//                val r = 1 - abs(position)
//                page.scaleY = 0.85f + r * 0.15f
//                page.translationX = -page.width * position
//                page.alpha = 0.25f + r
//            }
//
//            offscreenPageLimit = 2
//            // 적용 예시
////            val itemDecoration = HorizontalMarginItemDecoration(horizontalMarginInPx = 20)
////            viewPager2.addItemDecoration(itemDecoration)
//
//        }






    }
    // 결과 처리
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