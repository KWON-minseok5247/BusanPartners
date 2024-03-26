package com.kwonminseok.busanpartners.mainScreen.connect

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kelineyt.adapter.makeIt.StudentCardAdapter
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.FragmentConnectBinding
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ConnectViewModel
import com.kwonminseok.busanpartners.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG = "ConnectFragment"
@AndroidEntryPoint
class ConnectFragment : Fragment() {
    private var chipTexts: MutableList<String>? = null

    lateinit var binding: FragmentConnectBinding
//    private val viewModel by viewModels<ConnectViewModel>()
    private val adapter by lazy { StudentCardAdapter() }


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


//        studentCardRv()
//        lifecycleScope.launchWhenStarted {
//            viewModel.user.collectLatest {
//                when (it) {
//                    is Resource.Loading -> {
//
//                    }
//                    is Resource.Success -> {
//                        Log.e(TAG, it.data.toString())
//                        adapter.differ.submitList(it.data)
//
//                    }
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                    else -> Unit
//                }
//            }
//        }




    }

//    private fun studentCardRv() {
//        binding.viewPagerImages.adapter = adapter
//    }
}