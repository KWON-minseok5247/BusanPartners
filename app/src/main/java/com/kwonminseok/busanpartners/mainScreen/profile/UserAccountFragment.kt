package com.kwonminseok.busanpartners.mainScreen.profile

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentUserAccountBinding
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.UserAccountViewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private var chipTexts: MutableList<String>? = null
    lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModels>()
    private val GALLERY = 1
    private var imageData: Uri? = null
    lateinit var oldUser: User

    private lateinit var callback: OnBackPressedCallback


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showUserLoading()
                    }

                    is Resource.Success -> {
                        hideUserLoading()
                        enterData(it.data!!)
                        oldUser = it.data
                        Log.e("old user", oldUser.toString())
                    }

                    is Resource.Error -> {
                        hideUserLoading()
                        binding.progressbarAccount.visibility = View.GONE

                    }

                    else -> Unit

                }
            }
        }


        lifecycleScope.launchWhenStarted {

            viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
                if (isLoading) {
                    // 로딩 시작
                    binding.buttonSave.startAnimation()
                } else {
                    // 로딩 완료
                    binding.buttonSave.revertAnimation()
                    Toast.makeText(context, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    updateOldUser()

                }
            })

            binding.switchShowHideTags.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // 스위치가 켜지면 태그 목록을 보여줍니다.
                    viewModel.wantToMeet(isChecked)
                } else {
                    // 스위치가 꺼지면 태그 목록을 숨깁니다.
                    viewModel.wantToMeet(isChecked)
                }
            }

//            viewModel.updateUser.collectLatest {
//                when (it) {
//                    is Resource.Loading -> {
//                        binding.buttonSave.startAnimation()
//                    }
//
//                    is Resource.Success -> {
//                        binding.buttonSave.revertAnimation()
//                        Toast.makeText(requireContext(), "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
//                    }
//
//                    is Resource.Error -> {
//                        binding.buttonSave.revertAnimation()
//                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                    else -> Unit
//
//                }
//            }
        }

        binding.buttonSave.setOnClickListener {
            // 얘를 따로 하는 게 아니라 전역변수로 설정하는 게 더 낫나?????
            // 만약에 전역변수로 설정하면 달라졌는지 여부만 확인하면 되니까?
            val edName = binding.edName.text.toString()
            val edMajor = binding.edMajor.text.toString()
            val introduction = binding.introduction.text.toString()
            chipTexts = mutableListOf<String>()
            for (i in 0 until binding.chipGroupHobbies.childCount) {
                val chip = binding.chipGroupHobbies.getChildAt(i) as Chip
                chipTexts!!.add(chip.text.toString())
            }
            Log.e("chiptext", chipTexts.toString())
            val noImageMap = mapOf(
                "name" to edName,
                "major" to edMajor,
                "introduction" to introduction,
                "chipGroup" to chipTexts
            )
            if (imageData == null) { // 사진을 변경하지 않은 경우
                viewModel.saveUser(noImageMap)
            } else { // 사진을 변경한 경우
                viewModel.saveUserWithImage(noImageMap, imageData!!)
            }

        }
        binding.imageCloseUserAccount.setOnClickListener {
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
                // 사용자가 완료 버튼을 눌렀을 때의 처리를 작성합니다.
                // 예: 입력한 텍스트 값을 사용하거나, 다른 화면으로 넘어가는 등의 처리
                val tagText = binding.editTag.text.toString()
                if (tagText.isNotEmpty()) {
                    val chip = Chip(requireContext()).apply {
                        text = tagText
                        isCloseIconVisible = true // 닫기 아이콘을 보여줍니다.
                        setOnCloseIconClickListener {
                            // Chip을 클릭했을 때 ChipGroup에서 제거합니다.
                            binding.chipGroupHobbies.removeView(this)
                        }
                    }

                    chip.alpha = 0f // 초기 알파 값을 0으로 설정
                    binding.chipGroupHobbies.addView(chip)

                    // 애니메이션으로 알파 값을 0에서 1로 변경
                    chip.animate()
                        .alpha(1f)
                        .setDuration(300) // 애니메이션 지속 시간 설정
                        .start()

                    binding.editTag.text.clear() // EditText의 텍스트를 지웁니다.
                }
                true
            } else false

        }

    }

    private fun backPress() {
        val edName = binding.edName.text.toString()
        val edMajor = binding.edMajor.text.toString()
        val introduction = binding.introduction.text.toString()
        chipTexts = mutableListOf<String>()
        for (i in 0 until binding.chipGroupHobbies.childCount) {
            val chip = binding.chipGroupHobbies.getChildAt(i) as Chip
            chipTexts!!.add(chip.text.toString())
        }
        Log.e("olduser", oldUser.toString())
        Log.e("edName", edName.toString())
        Log.e("edMajor", edMajor.toString())
        Log.e("introduction", introduction.toString())
        Log.e("chipTexts", chipTexts.toString())
        Log.e("imageData", imageData.toString())


        if (oldUser.name == edName &&
            oldUser.major == edMajor &&
            oldUser.introduction == introduction &&
            oldUser.chipGroup == chipTexts &&
            imageData == null
        ) {
            findNavController().navigateUp()
        } else {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("저장이 되지 않았습니다.")
                .setMessage("그래도 나가시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        findNavController().navigateUp()
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            builder.show()
        }
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
            progressbarAccount.visibility = View.GONE
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
            progressbarAccount.visibility = View.VISIBLE
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
            edName.setText(user.name)
            tvUniversity.text = user.college
            edMajor.setText(user.major)
            edEmail.text = user.email
            introduction.setText(user.introduction)
            switchShowHideTags.isChecked = user.wantToMeet
        }
        binding.chipGroupHobbies.removeAllViews()
        chipTexts = user.chipGroup?.toMutableList()
        user.chipGroup?.let { setupHobbiesChips(it) }
    }


    // 사용자 정보 저장 성공 후 oldUser 업데이트
    private fun updateOldUser() {
        val newName = binding.edName.text.toString()
        val newMajor = binding.edMajor.text.toString()
        val newIntroduction = binding.introduction.text.toString()
        val newChipTexts = mutableListOf<String>().apply {
            for (i in 0 until binding.chipGroupHobbies.childCount) {
                val chip = binding.chipGroupHobbies.getChildAt(i) as Chip
                add(chip.text.toString())
            }
        }
        imageData = null

        oldUser = oldUser.copy(
            name = newName,
            major = newMajor,
            introduction = newIntroduction,
            chipGroup = newChipTexts,
        )
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