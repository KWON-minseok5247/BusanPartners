package com.kwonminseok.busanpartners.ui.profile

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alertview.lib.AlertView
import com.alertview.lib.OnItemClickListener
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.data.TranslatedList
import com.kwonminseok.busanpartners.data.TranslatedText
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentUserAccountBinding
import com.kwonminseok.busanpartners.databinding.FragmentUserAccountForBeginnerBinding
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.extensions.toUser
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


//TODO 여기서도 관광객일 때는 많은 데이터가 필요없으니까 일부 지우는 과정이 필요함.
@AndroidEntryPoint
class UserAccountForBeginnerFragment : Fragment() {

    private var _binding: FragmentUserAccountForBeginnerBinding? = null
    private val binding get() = _binding!!

    private val uid = BusanPartners.preferences.getString("uid", "")

    private val viewModel: UserViewModel by viewModels()
//    private val viewModel: UserViewModel by activityViewModels()

    private val GALLERY = 1
    private var imageData: Uri? = null
    lateinit var oldUser: User

    private lateinit var callback: OnBackPressedCallback


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserAccountForBeginnerBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Room으로부터 데이터를 받는 과정
        viewModel.getUserStateFlowData(uid).observe(viewLifecycleOwner) { userEntity ->
            Log.e("userEntity", userEntity.toString())
            // userEntity가 null이 아닐 때 UI 업데이트
            if (userEntity == null) {
                // 문제가 있었으면 프로필화면에서 이미 처리했지 여기는 굳이 신경안써도 될 듯?
                viewModel.getCurrentUser()

                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest {
                        when (it) {
                            is Resource.Success -> {
//                                hideProgressBar()
                                enterData(it.data!!)
                                oldUser = it.data
                                viewModel.insertUser(oldUser.toEntity())
                            }

                            is Resource.Error -> {
//                                hideProgressBar()
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> Unit
                        }
                    }
                }
            } else { // 여기는 Room으로부터 먼저 가져오되 서버에서도 가져와서 비교를 하고 업데이트 및 수정을 한다.
                // TODO 아무리 빨리해도 속도 제한이 있는데 그 사이에 짧은 로딩바라도 넣는 게 낫나???
                oldUser = userEntity.toUser()
                Log.e("oldUser", oldUser.toString())
                enterData(oldUser)

                viewModel.getCurrentUser()

                lifecycleScope.launchWhenStarted {
                    viewModel.user.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                            }

                            is Resource.Success -> {
                                if (oldUser == it.data) { // room이랑 데이터가 똑같을 때
                                    return@collectLatest
                                } else { // Room이랑 데이터가 다를 때
                                    Log.e("it.data", it.data.toString())

                                    enterData(it.data!!)
                                    oldUser = it.data
                                    viewModel.updateUser(oldUser.toEntity())

                                }

                            }

                            is Resource.Error -> {
//                                hideProgressBar()
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> Unit
                        }
                    }
                }
            }

        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                // TODO 여기서 변경사항을 찾아서 제공하는 게 더 합리적인 것 같다?
                val edName = edName.text.toString()
                if (edName.isBlank()) {
                    Toast.makeText(requireContext(), getString(R.string.enter_name_prompt), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                val newData = mapOf(
                    "name" to edName,
                )

                val changes = mutableMapOf<String, Any?>()
                if (edName != oldUser.name?.ko ) changes["name"] = edName


                if (imageData == null) { // 사진을 변경하지 않은 경우
                    viewModel.setCurrentUser(changes)
                    oldUser = oldUser.copy(
                        name = TranslatedText(ko = edName),
                    )
//                    viewModel.updateUser(oldUser.toEntity())


                } else { // 사진을 변경한 경우
                    val image = convertUriToByteArray(imageData!!)
                    viewModel.setCurrentUserWithImage(image, changes)

                    oldUser = oldUser.copy(
                        name = TranslatedText(ko = edName),
                    )
                    imageData = null
                }
            }


        }


        // 버튼을 눌렀을 때 과정
        lifecycleScope.launchWhenStarted {
            var startTime: Long = 0
            viewModel.updateStatus.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // 로딩 인디케이터 표시
                        binding.buttonSave.startAnimation()
                        startTime = System.currentTimeMillis()
                    }

                    is Resource.Success -> {
                        val elapsedTime = System.currentTimeMillis() - startTime
                        val remainingTime = 1000 - elapsedTime

                        if (remainingTime > 0) {
                            delay(remainingTime)
                        }

                        // 로딩 인디케이터 숨기기
                        binding.buttonSave.revertAnimation {
                            binding.buttonSave.background = ContextCompat.getDrawable(requireContext(), R.drawable.cr24bff009963)
                        }
                        // 성공 메시지 표시 또는 성공 후 작업
                        Toast.makeText(requireContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        // 로딩 인디케이터 숨기기
                        binding.buttonSave.revertAnimation()
                        // 에러 메시지 표시
                        Toast.makeText(requireContext(), "${resource.message}", Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit // Resource.Unspecified 처리
                }
            }
        }


        binding.backButton.setOnClickListener {
            backPress()

        }


        // 갤러리 여는 방법
//https://believecom.tistory.com/722

        binding.imageUser.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, GALLERY)
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, GALLERY)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun backPress() {

        binding.apply {
            val edName = edName.text.toString()

            if (oldUser.name?.ko == edName &&
                imageData == null
            ) {   // 변경이 없을 때.
                findNavController().navigateUp()
            } else { // 변경을 했는데 저장을 하지 않았을 때
                AlertView.Builder()
                    .setContext(requireActivity())
                    .setStyle(AlertView.Style.Alert)
                    .setTitle(getString(R.string.save_alert_title))
                    .setMessage(getString(R.string.unsaved_changes_message))
                    .setDestructive(getString(R.string.confirmation))
                    .setOthers(arrayOf(getString(R.string.cancel)))
                    .setOnItemClickListener(object : OnItemClickListener {
                        override fun onItemClick(o: Any?, position: Int) {
                            if (position == 0) { // 확인 버튼 위치 확인
                                findNavController().navigateUp()
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


    }

    private fun convertUriToByteArray(
        imageUri: Uri,
    ): ByteArray {
        // 비트맵 생성
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
        }

        // ByteArrayOutputStream을 사용해 비트맵을 ByteArray로 변환
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }


    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                imageData = data?.data
                try {
                    binding.imageUser.setImageURI(imageData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로가기 클릭시 동작하는 로직
                backPress()
//                requireActivity().supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(0, R.anim.horizon_exit_front)
//                    .remove(this@CartFragment)
//                    .commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }



    private fun enterData(user: User) {
        Glide.with(this).load(user.imagePath).into(binding.imageUser)
        binding.apply {
            edName.setText(user.name?.ko)
            edEmail.text = user.email
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