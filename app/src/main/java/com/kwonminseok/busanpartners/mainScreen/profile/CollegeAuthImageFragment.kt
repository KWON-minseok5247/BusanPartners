package com.kwonminseok.busanpartners.mainScreen.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.ImagesAdapter
import com.kwonminseok.busanpartners.databinding.FragmentCollegeAuthImageBinding
import com.kwonminseok.busanpartners.util.Constants.STUDENT
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.AuthenticationViewModel
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs


@AndroidEntryPoint
class CollegeAuthImageFragment : Fragment() {
    lateinit var binding: FragmentCollegeAuthImageBinding
    private val viewModels by viewModels<AuthenticationViewModel>()
    private val viewModel: UserViewModel by viewModels()

//    private val authenticationCollegeViewModel by viewModels<AuthenticationInformationViewModel>()
    private val REQUEST_CODE_IMAGE_PICK = 1000
    lateinit var imagesAdapter: ImagesAdapter

    // 갤러리에 있는 이미지를 URI로 저장할 리스트
    private val imageUris = ArrayList<Uri>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCollegeAuthImageBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// 버튼을 눌렀을 때 과정
        lifecycleScope.launchWhenStarted {
            viewModel.updateStatus.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // 로딩 인디케이터 표시
                        binding.btnSendAllData.startAnimation()
                    }

                    is Resource.Success -> {
                        // 로딩 인디케이터 숨기기
                        binding.btnSendAllData.revertAnimation()
                        // 성공 메시지 표시 또는 성공 후 작업
                        Toast.makeText(requireContext(), "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Resource.Error -> {
                        // 로딩 인디케이터 숨기기
                        binding.btnSendAllData.revertAnimation()
                        // 에러 메시지 표시
                        Toast.makeText(requireContext(), "${resource.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit // Resource.Unspecified 처리
                }
            }
        }

        // 얘는 일단 user 데이터를 불러오고 만약 학생증 사진이 이미 올라와져있다면 굳이 사진을 올릴 필요가 없으니까. 사진은 그대로 올려두되
//        lifecycleScope.launchWhenStarted {
//            viewModel.user.collectLatest {
//                when (it) {
//                    is Resource.Loading -> {
//                        showProgressBar()
//                    }
//
//                    is Resource.Success -> {
//                        hideProgressBar()
//                        // 얘도 함수로 만드는 게 맞고.
//                        Log.e("제일제일 중요", it.data.toString())
//                        if (it.data?.authentication?.studentIdentificationCard != null) {
//                            // TODO 이미지 불러오고 삭제하는 기능을 효율적으로 만들지 않았다. 나중에 따로 시간나면 다시 만들자.
//                            val urlList = it.data?.authentication?.studentIdentificationCard
//                            imagesAdapter = ImagesAdapter(requireContext(), urlList).apply {
//                                onClick = { imageUrl, position ->
//                                    Log.e("click을 했을 때", "${imageUrl} ${position}")
//                                    AlertDialog.Builder(requireContext())
//                                        .setTitle("삭제 확인")
//                                        .setMessage("정말 삭제하시겠습니까?")
//                                        .setPositiveButton("삭제") { dialog, which ->
//                                            lifecycleScope.launch {
//                                                viewModels.deleteImageFromStorage(imageUrl)
//                                                viewModels.deleteImageFromDatabase(imageUrl, STUDENT)
//                                            }
//                                        }
//                                        .setNegativeButton("취소", null)
//                                        .show()
//
//
//                                }
//                            }
//                            binding.viewPagerImages.adapter = imagesAdapter
//                            binding.viewPagerImages.adapter?.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
//
//                        }
//
//                        else {
//                            binding.btnSendAllData.isClickable = true
//                            binding.btnOpenGallery.isClickable = true
//                        }
//                    }
//
//                    is Resource.Error -> {
//                        hideProgressBar()
//                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                    else -> Unit
//                }
//            }
//        }


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
//        binding.viewPagerImages.offscreenPageLimit = 4
        // item_view 간의 양 옆 여백을 상쇄할 값
//        binding.viewPagerImages.setPageTransformer { page, position ->
//            // 페이지 간 간격을 조절하여 단순한 스크롤 효과를 제공
//            page.translationX = position * -(offsetBetweenPages)
//        }
//        val offsetBetweenPages =
//            resources.getDimensionPixelOffset(R.dimen.offsetBetweenPages).toFloat()

//        binding.viewPagerImages.setPageTransformer { page, position ->
//            val myOffset = position * -(2 * offsetBetweenPages)
//            if (position < -1) {
//                page.translationX = -myOffset
//            } else if (position <= 1) {
//                // Paging 시 Y축 Animation 배경색을 약간 연하게 처리
//                val scaleFactor = 0.95f.coerceAtLeast(1 - abs(position))
//                page.translationX = myOffset
//                page.scaleY = scaleFactor
//                page.alpha = scaleFactor
//            } else {
//                page.alpha = 0f
//                page.translationX = myOffset
//            }
//        }


        // 프로필 프래그먼트로 돌아가는 함수
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_collegeAuthImageFragment_to_profileFragment)
        }


        //TODO 데이터들이 전부 갖춰져야 클릭을 할 수 있다거나 버튼이 보여선 안됨. 지금은 테스트용이라 냅둔다.
        binding.btnSendAllData.setOnClickListener {
            // imageUri가 null이면 안되도록 설정한다.
            if (imageUris.isEmpty()) {
                // 아무것도 실행되지 않도록
                Toast.makeText(requireContext(), "학생증 사진을 추가해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.uploadUserImagesAndUpdateToFirestore(imageUris, STUDENT).also {
                    viewModel.attachToAuthenticationFolder(STUDENT)
                }
                findNavController().navigate(R.id.action_collegeAuthImageFragment_to_profileFragment)
            }
        }
        // firebase 폴더를 따로 만들어 uid와 status를 알림


    }

    // 사진을 처리하는 과정
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK) {
//            imageUris.clear() // 기존 목록을 클리어
//
//            // 선택한 이미지 처리
//            if (data?.clipData != null) { // 여러 이미지 선택 처리
//                val clipData = data.clipData!!
//                for (i in 0 until clipData.itemCount) {
//                    val imageUri = clipData.getItemAt(i).uri
//                    imageUris.add(imageUri)
//                }
//            } else if (data?.data != null) { // 단일 이미지 선택 처리
//                val imageUri = data.data!!
//                imageUris.add(imageUri)
//            }
//
//            binding.viewPagerImages.adapter?.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
//
////            // 데이터를 파이어베이스에 저장하는 과정
////            lifecycleScope.launch {
////                viewModels.processUserImageSelection(imageUris, STUDENT)
////            }
//
//        }
//    }
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

            // ImagesAdapter 업데이트
            updateImagesAdapter(imageUris)
        }
    }
    private fun updateImagesAdapter(imageUris: ArrayList<Uri>) {
        if (!::imagesAdapter.isInitialized) {
            imagesAdapter = ImagesAdapter(requireContext(), imageUris)
            binding.viewPagerImages.adapter = imagesAdapter
        } else {
            imagesAdapter.updateImages(imageUris)
        }
        binding.viewPagerImages.adapter?.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
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