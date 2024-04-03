package com.kwonminseok.busanpartners.mainScreen.message

import android.Manifest
import com.kwonminseok.busanpartners.databinding.ActivityShareLocationBinding

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.MaterialDatePicker
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.ActivityChannelBinding
import com.kwonminseok.busanpartners.mainScreen.connect.ConnectFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.ui.ChatUI
import io.getstream.chat.android.ui.common.state.messages.Edit
import io.getstream.chat.android.ui.common.state.messages.MessageMode
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.AttachmentPreviewFactoryManager
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.AudioRecordAttachmentPreviewFactory
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.FileAttachmentPreviewFactory
import io.getstream.chat.android.ui.feature.messages.composer.attachment.preview.factory.MediaAttachmentPreviewFactory
import io.getstream.chat.android.ui.feature.messages.list.adapter.viewholder.attachment.AttachmentFactoryManager
import io.getstream.chat.android.ui.helper.SupportedReactions
import io.getstream.chat.android.ui.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListHeaderViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.ui.viewmodel.messages.MessageListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.messages.bindView
import java.text.SimpleDateFormat
import java.util.Date

class ShareLocationActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityShareLocationBinding


    // 현재 마커의 좌표를 저장할 변수
    private var currentMarkerPosition: LatLng? = null

    // 스냅샷을 저장할 변수
    private var snapshotBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // Step 0 - inflate binding
        binding = ActivityShareLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 기반 퍼미션 허가 절차
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: 권한 요청 처리
            return
        }


        // 초기 위치 설정 과정
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // 사용자의 현재 위치를 받았습니다. 지도 로딩을 시작합니다.
            location?.let {
                initializeMap(LatLng(it.latitude, it.longitude))
            } ?: run {
                // 위치 정보가 없는 경우, 기본 위치 사용 (부산 시청)
                initializeMap(LatLng(35.1798159, 129.0750222))

            }
        }

        binding.fabShareLocation.setOnClickListener {

            //TODO 마커를 찍지 않았을 때도 로직을 구사할 필요가 있다.
            Log.e("위치", currentMarkerPosition.toString())

            naverMap.takeSnapshot { bitmap ->
                snapshotBitmap = bitmap
                // 캡처된 스냅샷과 마커의 좌표를 사용하여 추가 작업을 수행
                // 예: 스냅샷과 좌표를 다른 액티비티로 전달하거나 저장
                val intent = Intent(this, ChannelActivity::class.java).apply {
                    putExtra("image", snapshotBitmap)
                    putExtra("latitude", currentMarkerPosition?.latitude)
                    putExtra("longitude", currentMarkerPosition?.longitude)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                }
                startActivity(intent)
            }


        }


    }

    // 시작하자마자 자기 인근 위치에서 지도를 볼 수 있음.
    private fun initializeMap(userLocation: LatLng) {
        val options = NaverMapOptions()
            .minZoom(11.0)
            .compassEnabled(true)
            .locationButtonEnabled(true)
            .tiltGesturesEnabled(false)
            .camera(CameraPosition(userLocation, 15.0)) // 사용자 위치로 카메라 설정
            .mapType(NaverMap.MapType.Basic)

        // Fragment에서 MapFragment를 다루기 위해 childFragmentManager를 사용합니다.
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        // MapFragment가 준비되면 onMapReady 콜백이 호출됩니다.
        mapFragment.getMapAsync(this)
//
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 지도가 너무 멀리 축소되거나 너무 가까이 확대되는 것을 방지합니다.
        // 지도 좌표 고정하는 단계
        val BUSAN_SW = LatLng(34.8799083, 128.7384361) // 부산시 남서쪽 좌표를 조정
        val BUSAN_NE = LatLng(35.3959361, 129.3728194) // 부산시 북동쪽 좌표를 조정
        val busanBounds = LatLngBounds(BUSAN_SW, BUSAN_NE)
        naverMap.extent = busanBounds

        // 클래스의 멤버 변수로 마커 참조를 유지
        var currentMarker: Marker? = null

//        naverMap.setOnMapClickListener { _, coord ->
//            // 기존 마커 삭제
//            currentMarker?.map = null
//            // 새 마커 생성 및 저장
//            currentMarker = Marker().apply {
//                position = coord
//                map = naverMap
//            }
//            // 마커의 좌표 저장
//            currentMarkerPosition = coord
//        }
// MapView가 준비되면 설정
        naverMap.addOnCameraIdleListener {
            // 카메라가 멈춘 최종 위치를 가져옵니다.
            currentMarkerPosition = naverMap.cameraPosition.target

            // 필요한 작업 수행, 예: finalPosition을 사용하여 위치 정보 검색 또는 서버 요청
        }


    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


}
