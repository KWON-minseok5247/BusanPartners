package com.kwonminseok.busanpartners.mainScreen.message

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.kwonminseok.busanpartners.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class AttachmentMapActivity : FragmentActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachment_map) // 맵을 표시할 레이아웃 설정


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // 뒤로가기 동작
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

        val location = LatLng(latitude, longitude)
        naverMap.moveCamera(CameraUpdate.scrollTo(location))
        Marker().apply {
            position = location
            map = naverMap
        }
    }
}
