package com.kwonminseok.busanpartners.mainScreen.connect

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kelineyt.adapter.makeIt.StudentCardAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentConnectBinding
import com.kwonminseok.busanpartners.databinding.UniversityCardFrontBinding
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.ConnectViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG = "ConnectFragment"

@AndroidEntryPoint
class ConnectFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentConnectBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var studentsByUniversity: Map<String?, List<User>>? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userList: MutableList<User>? = null


    private val viewModel by viewModels<ConnectViewModel>()


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

        val universityNames = listOf("국립부경대학교", "부산교육대학교", "부산대학교", "한국방송통신대학교", "국립한국해양대학교",
            "경성대학교", "고신대학교", "동명대학교", "동서대학교", "동아대학교", "동의대학교", "부산가톨릭대학교",
            "부산외국어대학교", "신라대학교", "영산대학교", "인제대학교"
        )

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        binding.labelChange.text = "부산 시에서 연락 가능한 대학생은 총 ${it.data?.size}명입니다. "
                        userList = it.data

                        // 대학교 이름에 따라 대학생 리스트를 분류
                        studentsByUniversity = userList?.groupBy { it.college }
                        for (name in universityNames) {
                            // 대학교 이름을 키로 사용하여 학생 리스트를 맵에서 가져옵니다.
                            if (studentsByUniversity?.get(name) != null) {
                                // 마커 추가, infoWindow 추가 과정 들어가야 함.
                                Log.e("어떤게 null이 아니지", studentsByUniversity?.get(name).toString())
                            }
                        }



                    }

                    is Resource.Error -> {

                    }

                    else -> Unit

                }
            }
        }

//        val pukyongUniversityStudents = studentsByUniversity?.get("국립부경대학교")
//        val pusanEducationUniversityStudents = studentsByUniversity?.get("부산교육대학교 ")
//        val pusanUniversityStudents = studentsByUniversity?.get("부산대학교")
//        val knouUniversityStudents = studentsByUniversity?.get("한국방송통신대학교")
//        val kmouUniversityStudents = studentsByUniversity?.get("국립한국해양대학교")
//        val ksUniversityStudents = studentsByUniversity?.get("경성대학교")
//        val kosinUniversityStudents = studentsByUniversity?.get("고신대학교")
//        val tuUniversityStudents = studentsByUniversity?.get("동명대학교")
//        val dongseoUniversityStudents = studentsByUniversity?.get("동서대학교")
//        val dongaUniversityStudents = studentsByUniversity?.get("동아대학교")
//        val deuUniversityStudents = studentsByUniversity?.get("동의대학교")
//        val cupUniversityStudents = studentsByUniversity?.get("부산가톨릭대학교")
//        val bufsUniversityStudents = studentsByUniversity?.get("부산외국어대학교")
//        val sillaUniversityStudents = studentsByUniversity?.get("신라대학교")
//        val ysuUniversityStudents = studentsByUniversity?.get("영산대학교")
//        val injeUniversityStudents = studentsByUniversity?.get("인제대학교")


        val universityStudentMap: MutableMap<String, List<User>?> = mutableMapOf()






        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 지도 허가권
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

        binding.floatingButton.setOnClickListener {
            // 여기서 관광객인증을 못하면 버튼을 누르면 관광객 인증하라고 알리기.
            findNavController().navigate(R.id.action_connectFragment_to_selectedUniversityStudentListFragment)

        }

    }


    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow


        // 지도 좌표 고정하는 단계
        val BUSAN_SW = LatLng(34.8799083, 128.7384361) // 부산시 남서쪽 좌표를 조정
        val BUSAN_NE = LatLng(35.3959361, 129.3728194) // 부산시 북동쪽 좌표를 조정
        val busanBounds = LatLngBounds(BUSAN_SW, BUSAN_NE)
        naverMap.extent = busanBounds



        universityMarker(naverMap)

    }

    private fun universityMarker(naverMap: NaverMap) {

        val infoWindow = InfoWindow().apply {
            anchor = PointF(0.5f, 1f)
            offsetX = -27
            offsetY = -60

            adapter = InfoWindowAdapter(requireContext())
            setOnClickListener {
                close()
                binding.floatingButton.animate()
                    .alpha(0f) // 투명도를 0으로 변경하여 뷰를 점진적으로 사라지게 합니다.
                    .setDuration(300) // 애니메이션 지속 시간을 300밀리초로 설정
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.floatingButton.visibility = View.GONE // 애니메이션이 끝나면 뷰를 숨깁니다.
                        }
                    })
                true
            }
        }
        naverMap.setOnMapClickListener { pointF, latLng ->
            // 지도의 어느 부분이든 클릭되면, 활성화된 모든 인포 윈도우를 닫습니다.
            infoWindow.close()
            binding.floatingButton.animate()
                .alpha(0f) // 투명도를 0으로 변경하여 뷰를 점진적으로 사라지게 합니다.
                .setDuration(300) // 애니메이션 지속 시간을 300밀리초로 설정
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.floatingButton.visibility = View.GONE // 애니메이션이 끝나면 뷰를 숨깁니다.
                    }
                })
        }


        // 국립부경대학교
        pukyongMarker(naverMap, infoWindow)
//
//        // 부산교육대학교
//        Marker().apply {
//            position = LatLng(35.1964809, 129.0741424)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 부산대학교
//        Marker().apply {
//            position = LatLng(35.2333739, 129.0798495)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 한국방송통신대학교
//        Marker().apply {
//            position = LatLng(35.2240908, 129.0064649)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 국립한국해양대학교
//        Marker().apply {
//            position = LatLng(35.076359, 129.0892064)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 경성대학교
//        Marker().apply {
//            position = LatLng(35.1422464, 129.0969305)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 고신대학교
//        Marker().apply {
//            position = LatLng(35.0789683, 129.0631343)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 동명대학교
//        Marker().apply {
//            position = LatLng(35.1220638, 129.1016726)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 동서대학교
//        Marker().apply {
//            position = LatLng(35.1449836, 129.0084605)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 동아대학교
//        Marker().apply {
//            position = LatLng(35.1161319, 128.9675199)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 동의대학교
//        Marker().apply {
//            position = LatLng(35.1418247, 129.0346167)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 부산가톨릭대학교
//        Marker().apply {
//            position = LatLng(35.2447053, 129.0975521)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//
//        // 부산외국어대학교
//        Marker().apply {
//            position = LatLng(35.2670447, 129.0790562)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//        // 신라대학교
//        Marker().apply {
//            position = LatLng(35.1682795, 128.9977094)
//            map = naverMap
//            icon = MarkerIcons.BLACK
//
//        }
//
//        // 영산대학교
//        Marker().apply {
//            position = LatLng(35.2239352, 129.1574846)
//            map = naverMap
//            icon = MarkerIcons.GRAY
//
//        }
//
//        // 인제대학교
//        Marker().apply {
//            position = LatLng(35.2487276, 128.9026734)
//            map = naverMap
//            icon = MarkerIcons.GRAY
//
//        }


    }

    private fun pukyongMarker(
        naverMap: NaverMap,
        infoWindow: InfoWindow
    ) {
        val pukyongUniversityStudents = studentsByUniversity?.get("국립부경대학교")
        Log.e("pukyongUniversityStudents", pukyongUniversityStudents.toString())
        Marker().apply {
            position = LatLng(35.1335411, 129.1059852)
            map = naverMap
            icon = MarkerIcons.GRAY
            setOnClickListener {
                if (infoWindow.marker == null) {
                    infoWindow.open(this)
                    binding.floatingButton.apply {
                        visibility = View.VISIBLE // 뷰를 보이게 설정
                        alpha = 0f // 투명도를 0으로 설정하여 뷰를 투명하게 만듭니다.
                        animate()
                            .alpha(1f) // 투명도를 1로 변경하여 뷰를 점진적으로 나타나게 합니다.
                            .setDuration(300) // 애니메이션 지속 시간을 300밀리초로 설정
                            .setListener(null) // 애니메이션 리스너를 설정할 필요가 없을 때는 null을 사용
                    }
                } else {
                    infoWindow.close()
                    binding.floatingButton.animate()
                        .alpha(0f) // 투명도를 0으로 변경하여 뷰를 점진적으로 사라지게 합니다.
                        .setDuration(300) // 애니메이션 지속 시간을 300밀리초로 설정
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                binding.floatingButton.visibility = View.GONE // 애니메이션이 끝나면 뷰를 숨깁니다.
                            }
                        })
                }


                true
            }
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
            .camera(CameraPosition(userLocation, 15.0)) // 사용자 위치로 카메라 설정
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

    // 일단 여기서는 대학교명, 리스트 통계,
    private class InfoWindowAdapter(private val context: Context) : InfoWindow.ViewAdapter() {

        private var binding: UniversityCardFrontBinding? = null
        override fun getView(infoWindow: InfoWindow): View {
            // binding이 null인 경우에만 inflate를 수행합니다.
            val binding =
                binding ?: UniversityCardFrontBinding.inflate(LayoutInflater.from(context))
                    .also { binding = it }

            val marker = infoWindow.marker
            if (marker != null) {
                // 이미지와 텍스트를 설정합니다.
                binding.imageViewPhoto.setImageResource(R.drawable.pukyong_logo)
                binding.tvUniversity.text = "국립부경대학교"
                binding.tvStudents.text = "현재 연락할 수 있는 국립부경대학교 학생은 2명입니다."
            }

            // 루트 뷰를 반환합니다.
            return binding.root


//
//            private var rootView: View? = null
//        private var icon: ImageView? = null
//        private var text: TextView? = null
//
//        override fun getView(infoWindow: InfoWindow): View {
//            val view = rootView ?: View.inflate(context, R.layout.university_card_front, null).also { rootView = it }
//            val icon = icon ?: view.findViewById<ImageView>(R.id.imageView_photo).also { icon = it }
//            val text = text ?: view.findViewById<TextView>(R.id.tv_students).also { text = it }
//
//
//            val marker = infoWindow.marker
//            if (marker != null) {
//
//                icon.setImageResource(R.drawable.pukyong_logo)
//                text.text = "현재 연락할 수 있는 국립부경대학교 학생은 2명입니다."
//            } else {
//
//
//            }
//
//
//            return view
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
