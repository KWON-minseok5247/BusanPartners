package com.kwonminseok.newbusanpartners.ui.message

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.application.BusanPartners
import com.kwonminseok.newbusanpartners.databinding.ActivityAttachmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import java.util.Locale

class AttachmentMapActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityAttachmentMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedLocale()
        binding = ActivityAttachmentMapBinding.inflate(layoutInflater)
        setContentView(binding.root) // 바인딩된 루트 뷰 설정

        // backButton 클릭 리스너 설정
        binding.backButton.setOnClickListener {
            // FragmentActivity에서는 findNavController().popBackStack()을 직접 사용할 수 없습니다.
            // 대신 finish()를 호출하여 현재 액티비티를 종료합니다.
            finish()
        }

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment_attachment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment_attachment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 인텐트에서 위도와 경도 가져오기
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        Log.e("asd", "$latitude $longitude")

        val location = LatLng(latitude, longitude)
        naverMap.moveCamera(CameraUpdate.scrollTo(location))

        val marker = Marker().apply {
            position = location
            map = naverMap
        }

        marker.setOnClickListener {
            val uri = Uri.parse("geo:$latitude,$longitude")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            startActivity(intent)
            true
        }
    }

    private fun applySavedLocale() {
        val localeString = BusanPartners.preferences.getString("selected_locale", "")
        val locale = if (localeString.isEmpty()) {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(localeString)
        }

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

