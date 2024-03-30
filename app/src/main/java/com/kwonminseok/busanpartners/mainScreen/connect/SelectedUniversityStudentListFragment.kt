package com.kwonminseok.busanpartners.mainScreen.connect

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kelineyt.adapter.makeIt.StudentCardAdapter
import com.kwonminseok.busanpartners.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.CollegeData
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentConnectBinding
import com.kwonminseok.busanpartners.databinding.FragmentSelectedUniversityStudentListBinding
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import com.kwonminseok.busanpartners.viewmodel.ConnectViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.models.Channel
import kotlinx.coroutines.flow.collectLatest


private val TAG = "SelectedUniversityStudentListFragment"
@AndroidEntryPoint
class SelectedUniversityStudentListFragment : Fragment() {
    private var chipTexts: MutableList<String>? = null

    lateinit var binding: FragmentSelectedUniversityStudentListBinding
//    private val viewModel by viewModels<ConnectViewModel>()
    private val adapter by lazy { StudentCardAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectedUniversityStudentListBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: Array<out Parcelable>? = arguments?.getParcelableArray("selectedUniversityStudents")
        val usersList = args?.toList()?.mapNotNull { it as? User }
        adapter.submitList(usersList)

        studentCardRv()
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
//                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                    else -> Unit
//                }
//            }
//        }
        binding.floatingMessageButton.setOnClickListener {
            // TODo 메시지 프래그먼트로 가서 해당 데이터를 가지고 채널을 추가하는게 바람직하다?
            val currentPosition = binding.viewPagerImages.currentItem
            if (usersList != null) {
                val b = Bundle().apply {
                    putString("studentUid",usersList[currentPosition].uid )
                }
                findNavController().navigate(R.id.action_selectedUniversityStudentListFragment_to_messageFragment, b)
            }



        }




    }

    private fun studentCardRv() {
        binding.viewPagerImages.adapter = adapter
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