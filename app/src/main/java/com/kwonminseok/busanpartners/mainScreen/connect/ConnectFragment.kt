package com.kwonminseok.busanpartners.mainScreen.connect

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kelineyt.adapter.makeIt.StudentCardAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.FragmentConnectBinding
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ConnectViewModel
import com.kwonminseok.busanpartners.viewmodel.ProfileViewModel
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.annotation.Nullable


private val TAG = "ConnectFragment"

@AndroidEntryPoint
class ConnectFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentConnectBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mapView: MapView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        binding = FragmentConnectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: 권한 요청 처리
            return
        }




        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // 사용자의 현재 위치를 받았습니다. 지도 로딩을 시작합니다.
            location?.let {
                initializeMap(LatLng(it.latitude, it.longitude))
            } ?: run {
                // 위치 정보가 없는 경우, 기본 위치 사용 (부산 시청)
                initializeMap(LatLng(35.1798159, 129.0750222))
            }
        }

    }


    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        val BUSAN_SW = LatLng(34.8799083, 128.7384361) // 부산시 남서쪽 좌표를 조정
        val BUSAN_NE = LatLng(35.3959361, 129.3728194) // 부산시 북동쪽 좌표를 조정
        val busanBounds = LatLngBounds(BUSAN_SW, BUSAN_NE)

        naverMap.extent = busanBounds


        universityMarker(naverMap)

    }

    private fun universityMarker(naverMap: NaverMap) {
        // 국립부경대학교
        Marker().apply {
            position = LatLng(35.1335411, 129.1059852)
            map = naverMap
        }

        // 부산교육대학교
        Marker().apply {
            position = LatLng(35.1964809, 129.0741424)
            map = naverMap
        }


        // 부산대학교
        Marker().apply {
            position = LatLng(35.2333739, 129.0798495)
            map = naverMap
        }


        // 한국방송통신대학교
        Marker().apply {
            position = LatLng(35.2240908, 129.0064649)
            map = naverMap
        }


        // 국립한국해양대학교
        Marker().apply {
            position = LatLng(35.076359, 129.0892064)
            map = naverMap
        }


        // 경성대학교
        Marker().apply {
            position = LatLng(35.1422464, 129.0969305)
            map = naverMap
        }


        // 고신대학교
        Marker().apply {
            position = LatLng(35.0789683, 129.0631343)
            map = naverMap
        }


        // 동명대학교
        Marker().apply {
            position = LatLng(35.1220638, 129.1016726)
            map = naverMap
        }


        // 동서대학교
        Marker().apply {
            position = LatLng(35.1449836, 129.0084605)
            map = naverMap
        }


        // 동아대학교
        Marker().apply {
            position = LatLng(35.1161319, 128.9675199)
            map = naverMap
        }


        // 동의대학교
        Marker().apply {
            position = LatLng(35.1418247, 129.0346167)
            map = naverMap
        }


        // 부산가톨릭대학교
        Marker().apply {
            position = LatLng(35.2447053, 129.0975521)
            map = naverMap
        }


        // 부산외국어대학교
        Marker().apply {
            position = LatLng(35.2670447, 129.0790562)
            map = naverMap
        }

        // 신라대학교
        Marker().apply {
            position = LatLng(35.1682795, 128.9977094)
            map = naverMap
        }

        // 영산대학교
        Marker().apply {
            position = LatLng(35.2239352, 129.1574846)
            map = naverMap
        }

        // 인제대학교
        Marker().apply {
            position = LatLng(35.2487276, 128.9026734)
            map = naverMap
        }
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

    // 시작하자마자 자기 인근 위치에서 지도를 볼 수 있음.
    private fun initializeMap(userLocation: LatLng) {
        val options = NaverMapOptions()
            .minZoom(11.0)
            .compassEnabled(true)
            .locationButtonEnabled(true)
            .tiltGesturesEnabled(false)
            .camera(CameraPosition(userLocation, 14.0)) // 사용자 위치로 카메라 설정
            .mapType(NaverMap.MapType.Basic)

        // Fragment에서 MapFragment를 다루기 위해 childFragmentManager를 사용합니다.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                childFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        // MapFragment가 준비되면 onMapReady 콜백이 호출됩니다.
        mapFragment.getMapAsync(this)
//
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
