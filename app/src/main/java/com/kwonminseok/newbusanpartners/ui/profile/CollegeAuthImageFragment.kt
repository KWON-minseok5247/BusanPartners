package com.kwonminseok.newbusanpartners.ui.profile

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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.alertview.lib.AlertView
import com.alertview.lib.OnItemClickListener
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.adapter.ImagesAdapter
import com.kwonminseok.newbusanpartners.databinding.FragmentCollegeAuthImageBinding
import com.kwonminseok.newbusanpartners.util.Constants.STUDENT
import com.kwonminseok.newbusanpartners.util.Resource
import com.kwonminseok.newbusanpartners.util.hideBottomNavigationView
import com.kwonminseok.newbusanpartners.util.showBottomNavigationView
import com.kwonminseok.newbusanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator3
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class CollegeAuthImageFragment : Fragment() {

    private var _binding: FragmentCollegeAuthImageBinding? = null
    private val binding get() = _binding!!
    private lateinit var indicator: CircleIndicator3
    private lateinit var viewPager: ViewPager2


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
        _binding = FragmentCollegeAuthImageBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewPager = binding.viewPager // viewPager 초기화
        indicator = binding.indicator // indicator 초기화



        lifecycleScope.launchWhenStarted {
            viewModel.updateStatus.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // 로딩 인디케이터 표시
                        binding.sendButton.startAnimation()
                    }

                    is Resource.Success -> {
                        // 로딩 인디케이터 숨기기
                        binding.sendButton.revertAnimation()
                        // 성공 메시지 표시 또는 성공 후 작업
                        findNavController().navigate(
                            R.id.action_collegeAuthImageFragment_to_profileFragment,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build()
                        )
                        Toast.makeText(requireContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show()


                    }

                    is Resource.Error -> {
                        // 로딩 인디케이터 숨기기
                        binding.sendButton.revertAnimation()
                        // 에러 메시지 표시
                        Toast.makeText(requireContext(), "${resource.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit // Resource.Unspecified 처리
                }
            }
        }

        // 이미지 버튼 클릭 시
        binding.openGalleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.choose_photo)),
                REQUEST_CODE_IMAGE_PICK
            )
        }

        // 프로필 프래그먼트로 돌아가는 함수
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_collegeAuthImageFragment_to_profileFragment)
        }

        binding.sendButton.setOnClickListener {
            // imageUri가 null이면 안되도록 설정한다.
            if (imageUris.isEmpty()) {
                // 아무것도 실행되지 않도록
                Toast.makeText(requireContext(), getString(R.string.add_student_id_photo), Toast.LENGTH_SHORT).show()
            } else {
                AlertView.Builder()
                    .setContext(requireActivity())
                    .setStyle(AlertView.Style.Alert)
                    .setTitle(getString(R.string.save_alert_title))
                    .setMessage(getString(R.string.save_alert_message))
                    .setDestructive(getString(R.string.confirmation))
                    .setOthers(arrayOf(getString(R.string.cancel)))
                    .setOnItemClickListener(object : OnItemClickListener {
                        override fun onItemClick(o: Any?, position: Int) {
                            if (position == 0) { // 확인 버튼 위치 확인
                                viewModel.viewModelScope.launch {
                                    viewModel.uploadUserImagesAndUpdateToFirestore(imageUris, STUDENT)
                                }
                            } else {
                                (o as AlertView).dismiss() // 다른 버튼 클릭 시 AlertView 닫기
                            }
                        }

                    })
                    .build()
                    .setCancelable(true)
                    .show()



            }
        }
        // firebase 폴더를 따로 만들어 uid와 status를 알림
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_collegeAuthImageFragment_to_profileFragment)
                }
            }
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK) {
            imageUris.clear() // 기존 목록을 클리어
            // 선택한 이미지 처리
            if (data?.clipData != null) { // 여러 이미지 선택 처리
                val clipData = data.clipData!!
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
//                    imageUris.add(imageUri)
                    // 이미지 리사이즈 및 임시 파일로 저장
                    val resizedUri = convertResizeImage(imageUri) // 예시 해상도

                    imageUris.add(resizedUri)

                }

            } else if (data?.data != null) { // 단일 이미지 선택 처리
                val imageUri = data.data!!
                val resizedUri = convertResizeImage(imageUri) // 예시 해상도

                imageUris.add(resizedUri)
            }

            // ImagesAdapter 업데이트
            updateImagesAdapter(imageUris)
        }
    }

    private fun convertResizeImage(imageUri: Uri): Uri { // 사진 용량 줄이기
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
            binding.viewPager.adapter = imagesAdapter
            indicator.setViewPager(viewPager) // ViewPager와 Indicator 연결

        } else {
            imagesAdapter.updateImages(imageUris)
            indicator.setViewPager(viewPager) // ViewPager와 Indicator 연결

        }
        binding.viewPager.adapter?.notifyDataSetChanged() // 어댑터에 데이터 변경 알림
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