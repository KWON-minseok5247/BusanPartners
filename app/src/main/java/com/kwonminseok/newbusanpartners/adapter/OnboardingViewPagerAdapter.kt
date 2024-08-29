package com.kwonminseok.newbusanpartners.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kwonminseok.newbusanpartners.data.OnboardingPage
import com.kwonminseok.newbusanpartners.ui.profile.OnboardingFragment

//class OnboardingViewPagerAdapter(
//    fragmentActivity: FragmentActivity,
//    private val context: Context
//) :
//    FragmentStateAdapter(fragmentActivity) {
//
//    override fun createFragment(position: Int): Fragment {
//        return when (position) {
//            0 -> OnboardingFragment.newInstance(
//                context.resources.getString(R.string.title_onboarding_1),
//                context.resources.getString(R.string.description_onboarding_1),
//                R.raw.email_authentication
//            )
//            1 -> OnboardingFragment.newInstance(
//                context.resources.getString(R.string.title_onboarding_2),
//                context.resources.getString(R.string.description_onboarding_2),
//                R.raw.student_card_authentication
//            )
//            else -> OnboardingFragment.newInstance(
//                context.resources.getString(R.string.title_onboarding_3),
//                context.resources.getString(R.string.description_onboarding_3),
////                R.raw.lottie_girl_with_a_notebook
//                        R.raw.lottie_messaging
//            )
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return 3
//    }
//}

class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val pages: List<OnboardingPage>
) : FragmentStateAdapter(fragmentActivity) {

    private val context = fragmentActivity

    override fun createFragment(position: Int): Fragment {
        val page = pages[position]
        return OnboardingFragment.newInstance(
            context.resources.getString(page.titleResId),
            context.resources.getString(page.descriptionResId),
            page.imageResId
        )
    }

    override fun getItemCount(): Int {
        return pages.size
    }

}