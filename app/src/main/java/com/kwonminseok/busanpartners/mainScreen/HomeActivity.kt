package com.kwonminseok.busanpartners.mainScreen

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity: AppCompatActivity() {
    private lateinit var fragmentManager: FragmentManager

    val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true) // 현재 화면이 이미 스택에 있으면 해당 화면을 재사용
            .build()


        // 아래 이것들로 인해 프래그먼트가 움직인다?
        val navController = findNavController(R.id.homeHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)


    }
}