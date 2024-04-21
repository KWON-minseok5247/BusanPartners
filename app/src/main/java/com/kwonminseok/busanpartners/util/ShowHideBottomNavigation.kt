package com.kwonminseok.busanpartners.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.ui.HomeActivity

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as HomeActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    (activity as HomeActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as HomeActivity).findViewById<BottomNavigationView>(
            R.id.bottomNavigation
        )
    bottomNavigationView.visibility = View.VISIBLE
}