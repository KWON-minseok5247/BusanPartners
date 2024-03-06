package com.kwonminseok.busanpartners.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kwonminseok.busanpartners.MainActivity
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ActivityLoginRegisterBinding
import com.kwonminseok.busanpartners.databinding.FragmentLoginBinding
import com.kwonminseok.busanpartners.setupBottomSheetDialog
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.LoginsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class LoginFragment: Fragment() {
    private val viewModel by viewModels<LoginsViewModel>()

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            cirLoginButton.setOnClickListener {
                val email = editTextEmail.text.toString().trim()
                val password = editTextPassword.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.forgotPassword.setOnClickListener {
            setupBottomSheetDialog { email ->
                if (!email.isNullOrEmpty()) {
                    viewModel.resetPasswordFun(email)
                } else {
                    Snackbar.make(requireView(), "email is empty", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }




        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                    }

                    is Resource.Error -> {

                    }

                    else -> Unit
                }
            }
        }
// 12-05 임시 삭제
//        binding.tvForgotPasswordLogin.setOnClickListener {
//            setupBottomSheetDialog {email ->
//                // email이 입력하는 부분임
//                viewModel.resetPassword(email)
//
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.cirLoginButton.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.cirLoginButton.revertAnimation()

                        val intent =
                            Intent(context, MainActivity::class.java).addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                        startActivity(intent)
                    }

                    is Resource.Error -> {
                        binding.cirLoginButton.revertAnimation()
                        Toast.makeText(context,it.message.toString(),Toast.LENGTH_SHORT).show()

                    }

                    else -> {

                    }
                }
            }
        }


    }



}