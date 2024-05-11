package com.kwonminseok.busanpartners.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.OnboardingViewPagerAdapter
import com.kwonminseok.busanpartners.data.OnboardingPage
import com.kwonminseok.busanpartners.databinding.FragmentOnboardingInformationBinding
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView

//class OnboardingStudentInformationActivity : AppCompatActivity() {
//
//    private lateinit var mViewPager: ViewPager2
//    private lateinit var textSkip: TextView
//
//    private lateinit var binding: ActivityOnboardingStudentInformationBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityOnboardingStudentInformationBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        mViewPager = binding.viewPager
//        mViewPager.adapter = OnboardingViewPagerAdapter(this, this)
//        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()
//        textSkip = findViewById(R.id.text_skip)
//        textSkip.setOnClickListener {
//            finish()
//            val intent =
//                Intent(applicationContext, OnboardingFinishActivity::class.java)
//            startActivity(intent)
//            Animatoo.animateSlideLeft(this)
//        }
//
//        val btnNextStep: Button = findViewById(R.id.btn_next_step)
//
//        btnNextStep.setOnClickListener {
//            if (getItem() > mViewPager.childCount) {
//                finish()
//                val intent =
//                    Intent(applicationContext, OnboardingFinishActivity::class.java)
//                startActivity(intent)
//                Animatoo.animateSlideLeft(this)
//            } else {
//                mViewPager.setCurrentItem(getItem() + 1, true)
//            }
//        }
//
//    }
//
//    private fun getItem(): Int {
//        return mViewPager.currentItem
//    }
//
//}
class OnboardingInitImageFragment : Fragment() {

    private var _binding: FragmentOnboardingInformationBinding? = null
    private val binding get() = _binding!!
    private val mViewPager get() = binding.viewPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val onboardingPages = listOf(
            OnboardingPage(
                R.string.init_introduction_one,
                R.string.init_introduction_one,
                R.raw.lottie_watch_videos
            ),
            OnboardingPage(
                R.string.init_introduction_two,
                R.string.init_introduction_two,
                R.raw.lottie_delivery_boy_bumpy_ride
            ),
            OnboardingPage(
                R.string.init_introduction_three,
                R.string.init_introduction_three,
                R.raw.lottie_girl_with_a_notebook
            )
        )

        val adapter = activity?.let { OnboardingViewPagerAdapter(it, onboardingPages) }
        mViewPager.adapter = adapter



//        mViewPager.adapter = OnboardingViewPagerAdapter(requireActivity(), requireContext())

        // TabLayoutMediator를 사용하여 탭 레이아웃과 뷰페이저 연동
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()

        // "건너뛰기" 버튼 클릭 시 이벤트 처리
        binding.textSkip.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingStudentInformationFragment_to_collegeAuthFragment)
        }

        // "다음 단계" 버튼 클릭 시 이벤트 처리
        binding.btnNextStep.setOnClickListener {
            val nextItem = getItem() + 1
            if (nextItem >= mViewPager.adapter?.itemCount ?: 0) {
                findNavController().navigate(R.id.action_onboardingStudentInformationFragment_to_collegeAuthFragment)
            } else {
                mViewPager.setCurrentItem(nextItem, true)
            }
        }
    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }

    override fun onPause() {

        showBottomNavigationView()
        super.onPause()
    }
}
