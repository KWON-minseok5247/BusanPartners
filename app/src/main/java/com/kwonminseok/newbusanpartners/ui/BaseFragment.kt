//package com.kwonminseok.busanpartners.ui
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.viewbinding.ViewBinding
//import com.kwonminseok.busanpartners.R
//import com.kwonminseok.busanpartners.extensions.setStatusBarTransparent
//import com.kwonminseok.busanpartners.extensions.setStatusBarVisible
//
//abstract class BaseFragment: Fragment() {
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = getViewBinding()
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        addEmptyView()
//    }
//
//    private fun addEmptyView() {
//        val emptyView = View(requireContext()).apply {
//            id = View.generateViewId()
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                resources.getDimensionPixelSize(R.dimen.empty_view_height) // 25dp를 dimen 리소스로 지정
//            )
//        }
//        (binding.root as ViewGroup).addView(emptyView, 0)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    override fun onResume() {
//        super.onResume()
//        requireActivity().setStatusBarTransparent()
//
//    }
//
//    override fun onPause() {
//        super.onPause()
//        requireActivity().setStatusBarVisible()
//
//    }
//
//}
