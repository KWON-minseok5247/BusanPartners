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
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.extensions.toUser
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


//TODO 여기서도 관광객일 때는 많은 데이터가 필요없으니까 일부 지우는 과정이 필요함.
@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private var chipTexts: MutableList<String>? = null

    private var _binding: FragmentUserAccountBinding? = null
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
        _binding = FragmentUserAccountBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.allLayout.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE

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
                val edMajor = edMajor.text.toString()
                val introduction = introduction.text.toString()
                val wantToMeet = switchShowHideTags.isChecked

                chipTexts = mutableListOf<String>()

                for (i in 0 until chipGroupHobbies.childCount) {
                    val chip = chipGroupHobbies.getChildAt(i) as Chip
                    chipTexts!!.add(chip.text.toString())
                }

                val newData = mapOf(
                    "name" to edName,
                    "major" to edMajor,
                    "introduction" to introduction,
                    "chipGroup" to chipTexts,
                    "wantToMeet" to wantToMeet
                )

                val changes = mutableMapOf<String, Any?>()
                if (edName != oldUser.name?.ko || oldUser.name?.en.isNullOrEmpty()) changes["name"] =
                    edName


                if (edMajor != oldUser.major?.ko) changes["major"] = edMajor
                if (introduction != oldUser.introduction?.ko) changes["introduction"] = introduction
                if (chipTexts != oldUser.chipGroup?.ko) changes["chipGroup"] = chipTexts
                if (wantToMeet != oldUser.wantToMeet) changes["wantToMeet"] = wantToMeet


                if (imageData == null) { // 사진을 변경하지 않은 경우
                    viewModel.setCurrentUser(changes)
                    oldUser = oldUser.copy(
                        name = TranslatedText(ko = edName),
                        major = TranslatedText(ko = edMajor),
                        introduction = TranslatedText(ko = introduction),
                        chipGroup = TranslatedList(ko = chipTexts),
                        wantToMeet = wantToMeet
                    )
//                    viewModel.updateUser(oldUser.toEntity())


                } else { // 사진을 변경한 경우
                    val image = convertUriToByteArray(imageData!!)
                    viewModel.setCurrentUserWithImage(image, changes)

                    oldUser = oldUser.copy(
                        name = TranslatedText(ko = edName),
                        major = TranslatedText(ko = edMajor),
                        introduction = TranslatedText(ko = introduction),
                        chipGroup = TranslatedList(ko = chipTexts),
                        wantToMeet = wantToMeet
                    )
                    imageData = null
                }
            }


        }


        // 버튼을 눌렀을 때 과정
        lifecycleScope.launchWhenStarted {
            viewModel.updateStatus.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // 로딩 인디케이터 표시
                        binding.buttonSave.startAnimation()
                    }

                    is Resource.Success -> {
                        // 로딩 인디케이터 숨기기
                        binding.buttonSave.revertAnimation()
                        // 성공 메시지 표시 또는 성공 후 작업
                        Toast.makeText(requireContext(), "성공적으로 처리되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Resource.Error -> {
                        // 로딩 인디케이터 숨기기
                        binding.buttonSave.revertAnimation()
                        // 에러 메시지 표시
                        Toast.makeText(
                            requireContext(),
                            "${resource.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
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

        binding.editTag.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val tagText = binding.editTag.text.toString()
                if (tagText.isNotEmpty()) {
                    if (binding.chipGroupHobbies.childCount < 15) {
                        val chip = Chip(requireContext()).apply {
                            text = tagText
                            isCloseIconVisible = true
                            setChipBackgroundColorResource(R.color.chipgroup_color)
                            setOnCloseIconClickListener {
                                binding.chipGroupHobbies.removeView(this)
                            }
                        }

                        chip.alpha = 0f
                        binding.chipGroupHobbies.addView(chip)

                        chip.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start()

                        binding.editTag.text.clear()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "최대 15개의 태그만 추가할 수 있습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                true
            } else false
        }

//        binding.editTag.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                // 사용자가 완료 버튼을 눌렀을 때의 처리를 작성합니다.
//                // 예: 입력한 텍스트 값을 사용하거나, 다른 화면으로 넘어가는 등의 처리
//                val tagText = binding.editTag.text.toString()
//                if (tagText.isNotEmpty()) {
//                    val chip = Chip(requireContext()).apply {
//                        text = tagText
//                        isCloseIconVisible = true // 닫기 아이콘을 보여줍니다.
//                        setOnCloseIconClickListener {
//                            // Chip을 클릭했을 때 ChipGroup에서 제거합니다.
//                            binding.chipGroupHobbies.removeView(this)
//                        }
//                    }
//
//                    chip.alpha = 0f // 초기 알파 값을 0으로 설정
//                    binding.chipGroupHobbies.addView(chip)
//
//                    // 애니메이션으로 알파 값을 0에서 1로 변경
//                    chip.animate()
//                        .alpha(1f)
//                        .setDuration(300) // 애니메이션 지속 시간 설정
//                        .start()
//
//                    binding.editTag.text.clear() // EditText의 텍스트를 지웁니다.
//                }
//                true
//            } else false
//
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun backPress() {

        binding.apply {
            val edName = edName.text.toString()
            val edMajor = edMajor.text.toString()
            val introduction = introduction.text.toString()
            val wantToMeet = switchShowHideTags.isChecked
            chipTexts = mutableListOf<String>()
            for (i in 0 until chipGroupHobbies.childCount) {
                val chip = chipGroupHobbies.getChildAt(i) as Chip
                chipTexts!!.add(chip.text.toString())
            }

            if (oldUser.name?.ko == edName &&
                oldUser.major?.ko == edMajor &&
                oldUser.wantToMeet == wantToMeet &&
                oldUser.introduction?.ko == introduction &&
                oldUser.chipGroup?.ko == chipTexts &&
                imageData == null
            ) {   // 변경이 없을 때.
                findNavController().navigateUp()
            } else { // 변경을 했는데 저장을 하지 않았을 때

//                AlertView("저장이 되지 않았습니다.", "그래도 나가시겠습니까?", "취소", null, arrayOf("拍照", "从相册中选择"),
//                    requireActivity(), AlertView.Style.ActionSheet, object : OnItemClickListener {
//                        override fun onItemClick(o: Any?, position: Int) {
//                            Toast.makeText(
//                                requireActivity(), "点击了第" + position + "个",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }).show()
                AlertView.Builder()
                    .setContext(requireActivity())
                    .setStyle(AlertView.Style.Alert)
                    .setTitle("알림")
                    .setMessage("저장이 되지 않았습니다.\n정말 나가시겠습니까?")
                    .setDestructive("확인")
                    .setCancelText("dasd")
                    .setOthers(arrayOf("취소"))
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


//                AlertView.Builder().setContext(requireActivity())
//                    .setStyle(AlertView.Style.ActionSheet)
//                    .setTitle("정말 나가시겠습니까?")
//                    .setMessage(null)
//                    .setDestructive("1", "2")
//                    .setOthers()
//                    .setOnItemClickListener(object : OnItemClickListener{
//                        override fun onItemClick(o: Any?, position: Int) {
//                            findNavController().navigateUp()
//                        }
//                    })
//                    .build()
//                    .show()

//                val builder = AlertDialog.Builder(requireContext())
//                    builder.setTitle("저장이 되지 않았습니다.")
//                        .setMessage("그래도 나가시겠습니까?")
//                        .setPositiveButton("확인",
//                            DialogInterface.OnClickListener { dialog, id ->
//                                findNavController().navigateUp()
//
//                            })
//                        .setNegativeButton("취소",
//                            DialogInterface.OnClickListener { dialog, id ->
//                            })
//                    builder.show()
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

        private fun hideUserLoading() {
            binding.apply {
                imageUser.visibility = View.VISIBLE
                imageEdit.visibility = View.VISIBLE
                edName.visibility = View.VISIBLE
                tvUniversity.visibility = View.VISIBLE
                edEmail.visibility = View.VISIBLE
                buttonSave.visibility = View.VISIBLE
            }
        }

        private fun showUserLoading() {
            binding.apply {
                imageUser.visibility = View.INVISIBLE
                imageEdit.visibility = View.INVISIBLE
                edName.visibility = View.INVISIBLE
                tvUniversity.visibility = View.INVISIBLE
                edEmail.visibility = View.INVISIBLE
                buttonSave.visibility = View.INVISIBLE
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

        private fun setupHobbiesChips(hobbies: List<String>) {
            hobbies.forEach { hobby ->
                val chip = Chip(requireContext()).apply {
                    text = hobby
                    isCheckedIconVisible = false
                    isCloseIconVisible = true // 닫기 아이콘을 보여줍니다.
                    setChipBackgroundColorResource(R.color.chipgroup_color)
                    setOnCloseIconClickListener {
                        // Chip을 클릭했을 때 ChipGroup에서 제거합니다.
                        binding.chipGroupHobbies.removeView(this)
                    }
                }
                binding.chipGroupHobbies.addView(chip)
            }

        }


        private fun enterData(user: User) {
            Glide.with(this).load(user.imagePath).into(binding.imageUser)
            binding.apply {
                edName.setText(user.name?.ko)
                tvUniversity.text = user.college
                edMajor.setText(user.major?.ko)
                edEmail.text = user.email
                introduction.setText(user.introduction?.ko)
                switchShowHideTags.isChecked = user.wantToMeet
            }
            binding.chipGroupHobbies.removeAllViews()
            chipTexts = user.chipGroup?.ko?.toMutableList()
            user.chipGroup?.let { it?.ko?.let { it1 -> setupHobbiesChips(it1) } }

            binding.allLayout.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE

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