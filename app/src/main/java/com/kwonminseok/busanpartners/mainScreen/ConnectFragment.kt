package com.kwonminseok.busanpartners.mainScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.FragmentConnectBinding
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding

class ConnectFragment : Fragment() {

    lateinit var binding: FragmentConnectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




    }
}