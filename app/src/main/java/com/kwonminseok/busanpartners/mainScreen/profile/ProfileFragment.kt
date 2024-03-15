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
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.univcert.api.UnivCert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding

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

        binding.collegeAuthentication.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_collegeAuthFragment)
        }

        binding.travelerAuthentication.setOnClickListener {

        }


        binding.linearLogOut.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    UnivCert.clear(BuildConfig.COLLEGE_KEY)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }




    }
}