package com.kwonminseok.busanpartners.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentRegisterBinding
import com.kwonminseok.busanpartners.util.RegisterValidation
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {


    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



//        binding.buttonRegisterRegister.setOnClickListener {
//
//            if (!checkItem()) {
//                val firstName = binding.edFirstNameRegister.text.toString()
//                val lastName = binding.edLastNameRegister.text.toString()
//                val email = binding.edEmailRegister.text.toString()
//                val password = binding.edPasswordLoginRegister.text.toString()
//                val user = User(firstName,lastName,email)
//                lifecycleScope.launch {
//                    viewModel.registerId(user, password)
//
//
//
//
//
//
//                }
//            } else {
//                Toast.makeText(requireContext(),"Check all items", Toast.LENGTH_SHORT).show()
//            }
//            lifecycleScope.launchWhenStarted {
//                when (viewModel.register.value) {
//                    is Resource.Loading -> {
//                        binding.buttonRegisterRegister.startAnimation()
//                    }
//                    is Resource.Success -> {
//                        binding.buttonRegisterRegister.revertAnimation()
//                        Toast.makeText(requireContext(),"You make it correctly.", Toast.LENGTH_SHORT).show()
//
//                    }
//                    is Resource.Error -> {
//                        binding.buttonRegisterRegister.revertAnimation()
//                    }
//                    else -> Unit
//                }
//            }
//        }


        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


        binding.apply {
                cirRegisterButton.setOnClickListener {
                        val user = User(
                            edName.text.toString().trim(),
                            edLastName.text.toString().trim(),
                            edEmail.text.toString().trim(),
                            imagePath = "https://firebasestorage.googleapis.com/v0/b/busanpartners-86b94.appspot.com/o/ic_people_image.PNG?alt=media&token=3a8476b1-85d0-44bf-bafe-6fb2a3edee4c"
                        )
                        val password = edPassword.text.toString()
                        viewModel.createAccountWithEmailAndPassword(user, password)
                    }



        }


//        lifecycleScope.launchWhenStarted {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                // Lifecycle-aware한 작업 수행
//                viewModel.register.collect {
//                    when (it) {
//                        is Resource.Loading -> { // 누를 떄 일단 로딩으로 맞춰놨다가
//                            binding.buttonRegisterRegister.startAnimation()
//                        }
//
//                        is Resource.Success -> { // 완료되는 순간 revert로 만들어놓기
//                            binding.buttonRegisterRegister.revertAnimation()
//                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
//                            Toast.makeText(requireActivity(),"your id is registered.",Toast.LENGTH_SHORT).show()
//
//                        }
//
//                        is Resource.Error -> {
//                            Log.e(TAG, it.message.toString())
//                            binding.buttonRegisterRegister.revertAnimation()
//
//                        }
//                        else -> {
//                            Unit
//                        }
//                    }
//                }
//
//            }
//        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // collect는 새로운 데이터가 들어왔을 때 이전 데이터의 처리가 끝난 다음 새로운 데이터를 처리한다.
                // https://kotlinworld.com/252
                // collect는 새로운 데이터가 들어오면 이전 데이터의 처리가 끝난 후에 새로운 데이터를 처리한다.
                // collectLatest는 새로운 데이터가 들어오면 이전 데이터의 처리를 강제 종료시키고 새로운 데이터를 처리한다.
                viewModel.register.collect {
                    when (it) {
                        is Resource.Loading -> { // 누를 떄 일단 로딩으로 맞춰놨다가
                            binding.cirRegisterButton.startAnimation()
                        }

                        is Resource.Success -> { // 완료되는 순간 revert로 만들어놓기
                            binding.cirRegisterButton.revertAnimation()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            Toast.makeText(context,"your id is registered.",Toast.LENGTH_SHORT).show()
                        }

                        is Resource.Error -> {
                            binding.cirRegisterButton.revertAnimation()
                            Toast.makeText(context,it.message.toString(),Toast.LENGTH_SHORT).show()

                        }
                        else -> {
                            Unit
                        }
                    }
                }
            }
        }


        lifecycleScope.launchWhenStarted {

            viewModel.validation.collect() { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmail.apply {
                            requestFocus() // 이게 빨간색 경고같은데??
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPassword.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
//    fun checkItem(): Boolean {
//        return binding.edFirstNameRegister.text.isNullOrEmpty() &&
//                binding.edLastNameRegister.text.isNullOrEmpty() &&
//                binding.edEmailRegister.text.isNullOrEmpty() &&
//                binding.edPasswordLoginRegister.text.isNullOrEmpty()
//    }
}