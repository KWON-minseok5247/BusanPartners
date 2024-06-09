package com.kwonminseok.busanpartners.ui.connect

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.Universities
import com.kwonminseok.busanpartners.data.UniversityInfo
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentConnectBinding
import com.kwonminseok.busanpartners.databinding.UniversityCardFrontBinding
import com.kwonminseok.busanpartners.ui.home.HomeFragment
import com.kwonminseok.busanpartners.ui.login.SplashActivity
import com.kwonminseok.busanpartners.ui.login.SplashActivity.Companion.currentUser
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG = "ConnectFragment"

//TODO 연락하기를 바로 눌러버리면 nullpoint 에러가 발생한다.
@AndroidEntryPoint
class ConnectFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentConnectBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var studentsByUniversity: Map<String?, List<User>>? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userList: MutableList<User>? = null
//    private val infoWindows = mutableListOf<InfoWindow>()
    private var infoWindow = InfoWindow()

    private var selectedUniversityStudents: List<User>? = null
    private val viewModel: UserViewModel by viewModels()

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

//    private val viewModel by viewModels<ConnectViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

    _binding = FragmentConnectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("currentUser userEntity", SplashActivity.currentUser.toString())
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 이전에 퍼미션 요청을 거부당한 적이 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // 사용자에게 왜 퍼미션이 필요한지 설명을 제공하고, 요청을 다시 시도할 수 있습니다.
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                Log.e("homeFragment 1", "거절했을 때")
//                fetchTourApi()
            } else {
                // 처음 퍼미션을 요청하거나, '다시 묻지 않기'를 선택했을 경우
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                Toast.makeText(requireContext(), "위치 알림을 허용해주세요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)

//                fetchTourApi()

            }
        }
        // ViewModel 함수 호출
        viewModel.getUniversityStudentsWantToMeet()

        lifecycleScope.launchWhenStarted {
            viewModel.students.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        Log.e("Resource.Loading" , "Resource.Loading")

                    }

                    is Resource.Success -> {
                        Log.e("Resource.Success" , "Resource.Success")
                        // blockList에 포함되지 않은 사용자만 userList에 추가
                        userList = it.data


                        //TODO 만약 대학생이라면 환영합니다 뭐 이런 느낌으로.
                        binding.labelChange.text = "부산 시에서 연락 가능한 대학생은 총 ${userList?.size}명입니다. "
//                        userList = it.data
                        onDataLoaded()

                    }

                    is Resource.Error -> {
                        Log.e("Resource.Error" , it.message.toString())


                    }

                    else -> Unit

                }
            }
        }

        binding.floatingButton.setOnClickListener {
            // TODO 여기서 관광객인증을 못하면 버튼을 누르면 관광객 인증하라고 알리기.
            if (currentUser?.authentication?.authenticationStatus != "complete") {
                Snackbar.make(it, "인증을 먼저 진행해주시기 바랍니다.", Snackbar.LENGTH_SHORT).show()
                val bundle = Bundle().apply {
                    putBoolean("showAuthenticationPrompt", true)
                }

                findNavController().navigate(R.id.action_connectFragment_to_profileFragment, bundle)


            } else {
                val b = Bundle().apply {
                    putParcelableArray("selectedUniversityStudents",
                        selectedUniversityStudents?.toTypedArray()
                    )
//                putParcelableArrayList("selectedUniversityStudents", selectedUniversityStudents.toTypedArray())

                }
                findNavController().navigate(R.id.action_connectFragment_to_selectedUniversityStudentListFragment, b)

            }



        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    // 연락할 수 있는 대학생 리스트 목록을 받은 후 과정
    private fun onDataLoaded() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 위치 기반 퍼미션 허가 절차
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
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow


        // 지도 좌표 고정하는 단계
//        val BUSAN_SW = LatLng(35.0400, 128.9700) // 부산시 남서쪽 좌표를 조정
//        val BUSAN_NE = LatLng(35.2400, 129.1500) // 부산시 북동쪽 좌표를 조정
        val BUSAN_SW = LatLng(35.0500, 128.9400) // 부산시 남서쪽 좌표를 조정
        val BUSAN_NE = LatLng(35.2700, 129.2100) // 부산시 북동쪽 좌표를 조정

        val busanBounds = LatLngBounds(BUSAN_SW, BUSAN_NE)
        naverMap.extent = busanBounds


        studentsByUniversity = userList?.groupBy { it.college }

        // 대학생이 있으면 마커를 등록하는 과정
        Universities.universityInfoList.forEach { university ->
            val students = studentsByUniversity?.get(university.name)
            if (!students.isNullOrEmpty()) {
                val marker = Marker().apply {
                    position = university.location
                    map = naverMap
                    icon = MarkerIcons.GRAY
                }

                marker.setOnClickListener {
                    // InfoWindowAdapter에 현재 마커 정보를 업데이트
                    infoWindow.adapter = InfoWindowAdapter(requireContext(), university, students.size)
                    selectedUniversityStudents = students
                    // InfoWindow가 이미 이 마커에 대해 열려있지 않다면, 열기
                    if (infoWindow.marker != marker) {
                        infoWindow.open(marker)

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

        }




    }

    private class InfoWindowAdapter(
        private val context: Context,
        private val university: UniversityInfo, // 대학교 정보 객체 추가
        private val studentCount: Int // 학생 수 추가
    ) : InfoWindow.ViewAdapter() {

        private var binding: UniversityCardFrontBinding? = null

        override fun getView(infoWindow: InfoWindow): View {
            val binding = this.binding ?: UniversityCardFrontBinding.inflate(LayoutInflater.from(context)).also {
                this.binding = it
            }

            // 대학교 정보를 사용하여 로고와 텍스트 설정
            binding.imageViewPhoto.setImageResource(university.logoResourceId)
            binding.tvUniversity.text = university.name
            // 학생 수를 동적으로 설정
            binding.tvStudents.text = "현재 연락할 수 있는 ${university.name} 학생은 ${studentCount}명입니다."

            return binding.root
        }
    }



//    private fun createMarkerForUniversity(university: UniversityInfo, studentCount: Int) {
////        Marker().apply {
////            position = university.location
////            map = naverMap
////            icon = MarkerIcons.GRAY
////            // 마커 클릭 시 InfoWindow 표시
////            setOnClickListener {
////                val infoWindow = InfoWindow().apply {
////                    adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
////                        override fun getText(infoWindow: InfoWindow): CharSequence {
////                            return "${university.name}: 현재 연락할 수 있는 학생은 $studentCount 명입니다."
////                        }
////                    }
////                    infoWindow.open(marker)
//////                    open(this@apply)
////                }
////                true
////            }
////        }
//        val marker = Marker().apply {
//            position = university.location
//            map = naverMap
//            icon = MarkerIcons.GRAY
//        }
//
//        // InfoWindow 인스턴스 생성 및 설정
//        val infoWindow = InfoWindow().apply {
//            adapter = InfoWindowAdapter(requireContext(), university, studentCount)
//            // 마커 클릭 시 InfoWindow 열기
//            marker.setOnClickListener {
//                if (this.marker == null) {
//                    open(marker)
//                    binding.floatingButton.apply {
//                        visibility = View.VISIBLE // 뷰를 보이게 설정
//                        alpha = 0f // 투명도를 0으로 설정하여 뷰를 투명하게 만듭니다.
//                        animate()
//                            .alpha(1f) // 투명도를 1로 변경하여 뷰를 점진적으로 나타나게 합니다.
//                            .setDuration(300) // 애니메이션 지속 시간을 300밀리초로 설정
//                            .setListener(null) // 애니메이션 리스너를 설정할 필요가 없을 때는 null을 사용
//                    }
//                } else {
//                    close()
//                    binding.floatingButton.animate()
//                        .alpha(0f) // 투명도를 0으로 변경하여 뷰를 점진적으로 사라지게 합니다.
//                        .setDuration(300) // 애니메이션 지속 시간을 300밀리초로 설정
//                        .setListener(object : AnimatorListenerAdapter() {
//                            override fun onAnimationEnd(animation: Animator) {
//                                binding.floatingButton.visibility = View.GONE // 애니메이션이 끝나면 뷰를 숨깁니다.
//                            }
//                        })
//                }
//                true
//            }
//        }
//
//        naverMap.setOnMapClickListener { pointF, latLng ->
//            // 지도의 어느 부분이든 클릭되면, 활성화된 모든 인포 윈도우를 닫습니다.
//            infoWindow.close() // 모든 InfoWindow 닫기
//            binding.floatingButton.animate()
//                .alpha(0f) // 투명도를 0으로 변경하여 뷰를 점진적으로 사라지게 합니다.
//                .setDuration(300) // 애니메이션 지속 시간을 300밀리초로 설정
//                .setListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator) {
//                        binding.floatingButton.visibility = View.GONE // 애니메이션이 끝나면 뷰를 숨깁니다.
//                    }
//                })
//        }
//
//
//
//    }

        companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
