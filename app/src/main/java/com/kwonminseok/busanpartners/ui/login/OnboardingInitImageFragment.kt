
package com.kwonminseok.busanpartners.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.OnboardingViewPagerAdapter
import com.kwonminseok.busanpartners.data.OnboardingPage
import com.kwonminseok.busanpartners.databinding.FragmentOnboardingInformationBinding
import com.kwonminseok.busanpartners.databinding.FragmentOnboardingInitImageBinding
import com.kwonminseok.busanpartners.ui.home.HomeFragment
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

    private var _binding: FragmentOnboardingInitImageBinding? = null
    private val binding get() = _binding!!
    private val mViewPager get() = binding.viewPager

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE = 2000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingInitImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        askNotificationPermission()

        val onboardingPages = listOf(
            OnboardingPage(
                R.string.init_title_one,
                R.string.init_introduction_one,
                R.raw.lottie_splash_animation
            ),
            OnboardingPage(
                R.string.init_title_two,
                R.string.init_introduction_two,
                R.raw.lottie_messaging
            ),
            OnboardingPage(
                R.string.init_title_three,
                R.string.init_introduction_three,
                R.raw.drink
            )
        )

        val adapter = activity?.let { OnboardingViewPagerAdapter(it, onboardingPages) }
        mViewPager.adapter = adapter

        // TabLayoutMediator를 사용하여 탭 레이아웃과 뷰페이저 연동
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()

        // "건너뛰기" 버튼 클릭 시 이벤트 처리
        binding.textSkip.setOnClickListener {
            requestPermissionsIfNeeded {
                findNavController().navigate(R.id.action_onboardingInitImageFragment_to_loginFragment)
            }
        }

        // "다음 단계" 버튼 클릭 시 이벤트 처리
        binding.btnNextStep.setOnClickListener {
            val nextItem = getItem() + 1
            if (nextItem >= mViewPager.adapter?.itemCount ?: 0) {
                requestPermissionsIfNeeded {
                    findNavController().navigate(R.id.action_onboardingInitImageFragment_to_loginFragment)
                }
            } else {
                mViewPager.setCurrentItem(nextItem, true)
                askNotificationPermission()  // 이 위치에서 알림 권한을 요청합니다.
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
//        hideBottomNavigationView()
    }

    override fun onPause() {

//        showBottomNavigationView()
        super.onPause()
    }

    private fun requestPermissionsIfNeeded(onPermissionsGranted: () -> Unit) {
        val permissions = mutableListOf<String>()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            onPermissionsGranted()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:BusanPartners"))
                    startActivity(intent)
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }
}
