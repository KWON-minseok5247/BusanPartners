package com.kwonminseok.newbusanpartners.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.adapter.OnboardingViewPagerAdapter
import com.kwonminseok.newbusanpartners.data.OnboardingPage
import com.kwonminseok.newbusanpartners.databinding.FragmentOnboardingInformationBinding
import com.kwonminseok.newbusanpartners.util.hideBottomNavigationView
import com.kwonminseok.newbusanpartners.util.showBottomNavigationView

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
class OnboardingStudentInformationFragment : Fragment() {

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
                R.string.student_authentication_onboarding_1,
                R.string.student_description_onboarding_1,
                R.raw.email_authentication
            ),
            OnboardingPage(
                R.string.student_authentication_onboarding_2,
                R.string.student_description_onboarding_2,
                R.raw.student_card_authentication
            ),
            OnboardingPage(
                R.string.student_authentication_onboarding_3,
                R.string.student_description_onboarding_3,
                R.raw.message
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
