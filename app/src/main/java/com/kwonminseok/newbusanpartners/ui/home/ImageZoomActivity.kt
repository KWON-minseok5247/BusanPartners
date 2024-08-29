package com.kwonminseok.newbusanpartners.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.adapter.ImageZoomAdapter
import com.kwonminseok.newbusanpartners.extensions.setStatusBarTransparent
import me.relex.circleindicator.CircleIndicator3

class ImageZoomActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var imageZoomAdapter: ImageZoomAdapter
    private lateinit var indicator: CircleIndicator3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)

        viewPager = findViewById(R.id.zoom_view_pager)
        indicator = findViewById(R.id.zoom_indicator)

        val images = intent.getStringArrayListExtra("images") ?: arrayListOf()
        val position = intent.getIntExtra("position", 0)

        imageZoomAdapter = ImageZoomAdapter()
        viewPager.adapter = imageZoomAdapter
        imageZoomAdapter.submitList(images)

        viewPager.setCurrentItem(position, false)
        indicator.setViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        this.setStatusBarTransparent()
    }

    override fun onPause() {
        super.onPause()
//        this.setStatusBarVisible()
    }

}

