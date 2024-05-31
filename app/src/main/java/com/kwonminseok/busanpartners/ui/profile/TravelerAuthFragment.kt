package com.kwonminseok.busanpartners.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.ImagesAdapter
import com.kwonminseok.busanpartners.databinding.FragmentHomeBinding
import com.kwonminseok.busanpartners.databinding.FragmentTravelerAuthBinding
import com.kwonminseok.busanpartners.util.Constants.TRAVELER
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.AuthenticationViewModel
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class TravelerAuthFragment : Fragment() {

    private var _binding: FragmentTravelerAuthBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentTravelerAuthBinding.inflate(layoutInflater)
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
                        findNavController().navigate(R.id.action_travelerAuthFragment_to_profileFragment)
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
            findNavController().navigate(R.id.action_travelerAuthFragment_to_profileFragment)
        }


        //TODO 데이터들이 전부 갖춰져야 클릭을 할 수 있다거나 버튼이 보여선 안됨. 지금은 테스트용이라 냅둔다.
        binding.btnSendAllData.setOnClickListener {
            // imageUri가 null이면 안되도록 설정한다.
            if (imageUris.isEmpty()) {
                // 아무것도 실행되지 않도록
                Toast.makeText(requireContext(), "증명할 수 있는 사진을 추가해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.viewModelScope.launch {
                    viewModel.uploadUserImagesAndUpdateToFirestore(imageUris, TRAVELER)
//                    viewModel.attachToAuthenticationFolder(STUDENT)
//                    withContext(Dispatchers.Main) {
//                        findNavController().navigate(R.id.action_travelerAuthFragment_to_profileFragment)
//                    }
                }

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

                    val resizeUri = convertResizeImage(imageUri)
                    imageUris.add(resizeUri)
                }
            } else if (data?.data != null) { // 단일 이미지 선택 처리
                val imageUri = data.data!!
                val resizeUri = convertResizeImage(imageUri)

                imageUris.add(resizeUri)
            }

            // ImagesAdapter 업데이트
            updateImagesAdapter(imageUris)
        }
    }
    private fun convertResizeImage(imageUri: Uri): Uri {
//        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
//        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
//
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
//
//        val tempFile = File.createTempFile("resized_image", ".jpg", requireContext().cacheDir)
//        val fileOutputStream = FileOutputStream(tempFile)
//        fileOutputStream.write(byteArrayOutputStream.toByteArray())
//        fileOutputStream.close()
//
//        return Uri.fromFile(tempFile)

        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // 이미지 리사이즈
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        // 이미지 회전을 위한 Matrix 객체 생성
        val matrix = Matrix()
        val rotationInDegrees = exifToDegrees(getRotationAngle(imageUri))
        matrix.preRotate(rotationInDegrees.toFloat())

        // 회전된 Bitmap 생성
        val rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.width, resizedBitmap.height, matrix, true)

        val byteArrayOutputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)

        val tempFile = File.createTempFile("resized_image", ".jpg", requireContext().cacheDir)
        val fileOutputStream = FileOutputStream(tempFile)
        fileOutputStream.write(byteArrayOutputStream.toByteArray())
        fileOutputStream.close()

        return Uri.fromFile(tempFile)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun getRotationAngle(imageUri: Uri): Int {
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val exifInterface = ExifInterface(inputStream!!)
        return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    }

    private fun exifToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }


}