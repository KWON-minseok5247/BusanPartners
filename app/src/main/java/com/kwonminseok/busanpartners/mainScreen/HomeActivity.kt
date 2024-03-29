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
import com.kwonminseok.busanpartners.mainScreen.home.HomeFragment
import com.kwonminseok.busanpartners.mainScreen.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
//class HomeActivity: AppCompatActivity() {
//    private val fragmentManager = supportFragmentManager
//
//    val binding by lazy {
//        ActivityHomeBinding.inflate(layoutInflater)
//    }
//
//    private var homeFragment: HomeFragment? = null
//    private var connectFragment: ConnectFragment? = null
//    private var messageFragment: MessageFragment? = null
//    private var profileFragment: ProfileFragment? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//
//        val navOptions = NavOptions.Builder()
//            .setLaunchSingleTop(true) // 현재 화면이 이미 스택에 있으면 해당 화면을 재사용
//            .build()
//
//
//        initBottomNavigation()
//
//
//    }
//    private fun initBottomNavigation(){
//        // 최초로 보이는 프래그먼트
//        homeFragment = HomeFragment()
//        fragmentManager.beginTransaction().replace(R.id.homeHostFragment,homeFragment!!).commit()
//
//        binding.bottomNavigation.setOnItemSelectedListener {
//
//            // 최초 선택 시 fragment add, 선택된 프래그먼트 show, 나머지 프래그먼트 hide
//            when(it.itemId){
//                R.id.homeFragment ->{
//                    if(homeFragment == null){
//                        homeFragment = HomeFragment()
//                        fragmentManager.beginTransaction().add(R.id.homeHostFragment,homeFragment!!).commit()
//                    }
//                    if(homeFragment != null) fragmentManager.beginTransaction().show(homeFragment!!).commit()
//                    if(connectFragment != null) fragmentManager.beginTransaction().hide(connectFragment!!).commit()
//                    if(messageFragment != null) fragmentManager.beginTransaction().hide(messageFragment!!).commit()
//                    if(profileFragment != null) fragmentManager.beginTransaction().hide(profileFragment!!).commit()
//
//                    return@setOnItemSelectedListener true
//                }
//                R.id.connectFragment ->{
//                    if(connectFragment == null){
//                        connectFragment = ConnectFragment()
//                        fragmentManager.beginTransaction().add(R.id.homeHostFragment,connectFragment!!).commit()
//                    }
//                    if(homeFragment != null) fragmentManager.beginTransaction().hide(homeFragment!!).commit()
//                    if(connectFragment != null) fragmentManager.beginTransaction().show(connectFragment!!).commit()
//                    if(messageFragment != null) fragmentManager.beginTransaction().hide(messageFragment!!).commit()
//                    if(profileFragment != null) fragmentManager.beginTransaction().hide(profileFragment!!).commit()
//
//                    return@setOnItemSelectedListener true
//                }
//                R.id.messageFragment ->{
//                    if(messageFragment == null){
//                        messageFragment = MessageFragment()
//                        fragmentManager.beginTransaction().add(R.id.homeHostFragment,messageFragment!!).commit()
//                    }
//                    if(homeFragment != null) fragmentManager.beginTransaction().hide(homeFragment!!).commit()
//                    if(connectFragment != null) fragmentManager.beginTransaction().hide(connectFragment!!).commit()
//                    if(messageFragment != null) fragmentManager.beginTransaction().show(messageFragment!!).commit()
//                    if(profileFragment != null) fragmentManager.beginTransaction().hide(profileFragment!!).commit()
//
//                    return@setOnItemSelectedListener true
//                }
//                R.id.profileFragment ->{
//                    if(profileFragment == null){
//                        profileFragment = ProfileFragment()
//                        fragmentManager.beginTransaction().add(R.id.homeHostFragment,profileFragment!!).commit()
//                    }
//                    if(homeFragment != null) fragmentManager.beginTransaction().hide(homeFragment!!).commit()
//                    if(connectFragment != null) fragmentManager.beginTransaction().hide(connectFragment!!).commit()
//                    if(messageFragment != null) fragmentManager.beginTransaction().hide(messageFragment!!).commit()
//                    if(profileFragment != null) fragmentManager.beginTransaction().show(profileFragment!!).commit()
//
//                    return@setOnItemSelectedListener true
//                }
//                else ->{
//                    return@setOnItemSelectedListener true
//                }
//            }
//        }
//    }
//
//}
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


//        // 아래 이것들로 인해 프래그먼트가 움직인다?
        val navController = findNavController(R.id.homeHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)


    }
}