package com.kwonminseok.busanpartners.mainScreen.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.BusanPartners
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.login.LoginRegisterActivity
import com.kwonminseok.busanpartners.util.PreferenceUtil
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ChatListViewModel
import com.kwonminseok.busanpartners.viewmodel.ProfileViewModel
import com.univcert.api.UnivCert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        fetchUserData(it.data!!)

                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }






        binding.collegeAuthentication.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_collegeAuthFragment)
        }

        binding.travelerAuthentication.setOnClickListener {

        }

        binding.chat.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    UnivCert.clear(BuildConfig.COLLEGE_KEY)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.linearLogOut.setOnClickListener {
            lifecycleScope.launch {
                // 계정으로부터 로그아웃
                viewModel.logout()
            }
            // 접근권한으로부터 해제
            GoogleSignIn.getClient(
                requireActivity(),
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).revokeAccess().addOnCompleteListener {
                // 로그아웃 성공 후 LoginRegisterActivity로 이동
                val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        }




    }

    private fun fetchUserData(user: User) {
        binding.apply {
            Glide.with(requireView()).load(user.imagePath).into(binding.imageUser)
            tvUserName.text = "${user.firstName} ${user.lastName}"

        }
    }

    private fun showProgressBar() {
        binding.progressbarSettings.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressbarSettings.visibility = View.GONE
    }
}