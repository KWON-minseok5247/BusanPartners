//package com.kwonminseok.busanpartners.ui.home
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Location
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.OnBackPressedCallback
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import com.kwonminseok.busanpartners.R
//import com.kwonminseok.busanpartners.adapter.FestivalAdapter
//import com.kwonminseok.busanpartners.adapter.TourismAdapter
//import com.kwonminseok.busanpartners.adapter.TouristDestinationAdapter
//import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
//import com.kwonminseok.busanpartners.api.TourismApiService
//import com.kwonminseok.busanpartners.data.TourismResponse
//import com.kwonminseok.busanpartners.databinding.FragmentHomeBinding
//import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_ID
//import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_TYPE
//import com.kwonminseok.busanpartners.ui.EXTRA_MESSAGE_ID
//import com.kwonminseok.busanpartners.ui.EXTRA_PARENT_MESSAGE_ID
//import com.kwonminseok.busanpartners.ui.login.SplashActivity
//import com.kwonminseok.busanpartners.ui.message.ChannelActivity
//import com.kwonminseok.busanpartners.util.LanguageUtils
//import com.naver.maps.map.util.FusedLocationSource
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class HomeFragment3 : Fragment() {
//
//    //여기서 userEntity랑 user랑 같은지?
//    private val touristDestinationAdapter by lazy { TouristDestinationAdapter() }
//    private val tourismAdapter by lazy { TourismAdapter() }
//    private val festivalAdapter by lazy { FestivalAdapter() }
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//
//    private lateinit var locationSource: FusedLocationSource
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    private var backPressedTime: Long = 0
//    private lateinit var toast: Toast
//    private lateinit var tourismApiService: TourismAllInOneApiService
//
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        parseNotificationData()
//
//        locationSource =
//            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
//
//        _binding = FragmentHomeBinding.inflate(layoutInflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        Log.e("currentUser userEntity", SplashActivity.currentUser.toString())
//        tourismApiService = TourismAllInOneApiService.getInstance()
//
//        // 뒤로가기 2번시 종료
//        requireActivity().onBackPressedDispatcher.addCallback(
//            viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (System.currentTimeMillis() - backPressedTime < 2000) {
//                        toast.cancel()
//                        requireActivity().finish()
//                    } else {
//                        backPressedTime = System.currentTimeMillis()
//                        toast = Toast.makeText(
//                            requireContext(),
//                            "뒤로가기를 한 번 더 누르면 종료됩니다.",
//                            Toast.LENGTH_SHORT
//                        )
//                        toast.show()
//                    }
//                }
//            })
//
////
//        // 위치 기반 퍼미션 허가 절차
////        if (ActivityCompat.checkSelfPermission(
////                requireContext(),
////                Manifest.permission.ACCESS_FINE_LOCATION
////            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
////                requireContext(),
////                Manifest.permission.ACCESS_COARSE_LOCATION
////            ) != PackageManager.PERMISSION_GRANTED
////        ) {
////            // TODO: 권한 요청 처리
////            return
////        }
//
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // 이전에 퍼미션 요청을 거부당한 적이 있는 경우
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    requireActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                // 사용자에게 왜 퍼미션이 필요한지 설명을 제공하고, 요청을 다시 시도할 수 있습니다.
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    LOCATION_PERMISSION_REQUEST_CODE
//                )
//                fetchTourApi()
//            } else {
//                // 처음 퍼미션을 요청하거나, '다시 묻지 않기'를 선택했을 경우
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    LOCATION_PERMISSION_REQUEST_CODE
//                )
//                fetchTourApi()
//
//            }
//        } else {
//            // 퍼미션이 이미 허용된 경우
//            Log.e("else", "${LanguageUtils.getDeviceLanguage(requireContext())}")
//
//            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//
//            fetchFestivalList()
//            // 초기 위치 설정 과정
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                // 사용자의 현재 위치를 받았습니다. 지도 로딩을 시작합니다.
//                location?.let {
//                    val currentLatitude = it.latitude
//                    val currentLongitude = it.longitude
//                    Log.e("currentLatitude", currentLatitude.toString())
//                    Log.e("currentLongitude", currentLongitude.toString())
//
//                    fetchLocationBasedList(currentLongitude,currentLatitude)
////                    TourismApiService.getInstance().korLocationBasedList1(
////                        10,
////                        1,
////                        currentLongitude,
////                        currentLatitude,
////                        10000,
////                    ).enqueue(object :
////                        Callback<TourismResponse> {
////                        override fun onResponse(
////                            call: Call<TourismResponse>,
////                            response: Response<TourismResponse>
////                        ) {
////                            if (!isAdded) {
////                                Log.e("call and response", "${call.toString()} /////${response}")
////                                return
////                            }
////                            Log.e("call and response", "${call.toString()} /////${response}")
////
////                            // 이제 안전하게 UI 업데이트를 진행합니다.
////                            _binding?.let { binding ->
////                                if (response.isSuccessful) {
////                                    binding.touristRecyclerView.adapter = tourismAdapter
////                                    response.body()?.response?.body?.items?.item?.let { itemList ->
////                                        val itemsWithImages =
////                                            itemList.filter { it.firstimage.isNotEmpty() }
////                                        tourismAdapter.differ.submitList(itemsWithImages)
////                                    }
////                                } else {
////                                    Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
////                                }
////                            }
////
////                        }
////
////                        override fun onFailure(call: Call<TourismResponse>, t: Throwable) {
////
////                            Log.e(TAG, t.message.toString())
////
////                        }
////                    })
//
//
//                } ?: run {
//                    //                // 위치 정보가 없는 경우, 기본 위치 사용 (부산 시청)
//                    //                initializeMap(LatLng(35.1798159, 129.0750222))
//                }
//            }
//
////            TourismApiService.getInstance().korSearchFestival1(
////                10,
////                1,
////                "20240529", // 이벤트 시작 날짜 (예시)
////                "20240829", // 이벤트 종료 날짜 (예시)
////                BuildConfig.BUSAN_FESTIVAL_KEY,
////            ).enqueue(object : Callback<FestivalResponse> {
////                override fun onResponse(
////                    call: Call<FestivalResponse>,
////                    response: Response<FestivalResponse>
////                ) {
////                    if (!isAdded) {
////                        return
////                    }
////                    Log.e("response Festival", response.body().toString())
////
////                    // 이제 안전하게 UI 업데이트를 진행합니다.
////                    _binding?.let { binding ->
////                        if (response.isSuccessful) {
////                            binding.festivalViewPager.adapter = festivalAdapter
////                            response.body()?.response?.body?.items?.item?.let { itemList ->
////                                val itemsWithImages =
////                                    itemList.filter { it.firstimage.isNotEmpty() }
////                                festivalAdapter.differ.submitList(itemsWithImages)
////                            }
////                        } else {
////                            Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
////                        }
////                    }
////                }
////
////                override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
////                    Log.e(TAG, t.message.toString())
////                }
////            })
//
//        }
//
//
//        festivalAdapter.onFestivaltClick = { festival ->
//            val bundle = Bundle()
//            bundle.apply {
//                putString("contentId", festival.contentid)
//                putString("eventstartdate", festival.eventstartdate)
//                putString("eventenddate", festival.eventenddate)
//            }
//
//            findNavController().navigate(R.id.action_homeFragment_to_festivalDetailFragment, bundle)
//        }
//
//        tourismAdapter.onTourismPlaceClick = { tourismItem ->
//            val bundle = Bundle()
//            bundle.putString("contentId", tourismItem.contentid)
//            findNavController().navigate(
//                R.id.action_homeFragment_to_tourismPlaceDetailFragment,
//                bundle
//            )
//
//        }
//
//
//    }
//
//    private fun fetchTourApi() { //TODO 얘도 전면수정이 필요해.
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//        // 초기 위치 설정 과정
//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            // 사용자의 현재 위치를 받았습니다. 지도 로딩을 시작합니다.
//            location?.let {
//
//                val currentLatitude = it.latitude
//                val currentLongitude = it.longitude
//                Log.e("currentLatitude", currentLatitude.toString())
//                Log.e("currentLongitude", currentLongitude.toString())
//
//                TourismApiService.getInstance().korLocationBasedList1(
//                    10,
//                    1,
//                    currentLongitude,
//                    currentLatitude,
//                    10000,
//                ).enqueue(object :
//                    Callback<TourismResponse> {
//                    override fun onResponse(
//                        call: Call<TourismResponse>,
//                        response: Response<TourismResponse>
//                    ) {
//                        if (!isAdded) {
//                            return
//                        }
//
//                        // 이제 안전하게 UI 업데이트를 진행합니다.
//                        _binding?.let { binding ->
//                            if (response.isSuccessful) {
//                                binding.touristRecyclerView.adapter = tourismAdapter
//                                response.body()?.response?.body?.items?.item?.let { itemList ->
//                                    val itemsWithImages =
//                                        itemList.filter { it.firstimage.isNotEmpty() }
//                                    tourismAdapter.differ.submitList(itemsWithImages)
//                                }
//                            } else {
//                                Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
//                            }
//                        }
//
//                    }
//
//                    override fun onFailure(call: Call<TourismResponse>, t: Throwable) {
//                        Log.e(TAG, t.message.toString())
//
//                    }
//                })
//            } ?: run {
//                //                // 위치 정보가 없는 경우, 기본 위치 사용 (부산 시청)
//                //                initializeMap(LatLng(35.1798159, 129.0750222))
//            }
//        }
//    }
//
//    private fun fetchLocationBasedList(longitude: Double, latitude: Double) {
//        tourismApiService.locationBasedList1(
//            numOfRows = 10,
//            pageNo = 1,
//            mapX = longitude,
//            mapY = latitude,
//            radius = 20000,
//            contentTypeId = LanguageUtils.getContentIdForTourPlace(requireContext())
//        ).enqueue(object : Callback<TourismResponse> {
//            override fun onResponse(call: Call<TourismResponse>, response: Response<TourismResponse>) {
//                if (response.isSuccessful) {
//
//                    _binding?.let { binding ->
//                        if (response.isSuccessful) {
//                            binding.touristRecyclerView.adapter = tourismAdapter
//                            response.body()?.response?.body?.items?.item?.let { itemList ->
//                                val itemsWithImages =
//                                    itemList.filter { it.firstimage.isNotEmpty() }
//                                tourismAdapter.differ.submitList(itemsWithImages)
//                            }
//                        } else {
//                            Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
//                        }
//                    }
//                } else {
//                    Toast.makeText(context, "Failed to get tourism data", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<TourismResponse>, t: Throwable) {
//                Log.e(TAG, "Error: ${t.message}")
//            }
//        })
//    }
//
//    private fun fetchFestivalList() {
//        tourismApiService.searchFestival1(
//            numOfRows = 10,
//            pageNo = 1,
//            eventStartDate = "20240501",
//            eventEndDate = "20241231",
//        ).enqueue(object : Callback<FestivalResponse> {
//            override fun onResponse(call: Call<FestivalResponse>, response: Response<FestivalResponse>) {
//                if (response.isSuccessful) {
//
//                    _binding?.let { binding ->
//                        if (response.isSuccessful) {
//                            binding.festivalViewPager.adapter = festivalAdapter
//                            response.body()?.response?.body?.items?.item?.let { itemList ->
//                                val itemsWithImages =
//                                    itemList.filter { it.firstimage.isNotEmpty() }
//                                festivalAdapter.differ.submitList(itemsWithImages)
//                            }
//                        } else {
//                            Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
//                        }
//                    }
//                } else {
//                    Toast.makeText(context, "Failed to get festival data", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
//                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    //
////    private fun getFestivalInformation() {
////        BusanFestivalApiService.getInstance()
////            .getFestivalsKr(BuildConfig.BUSAN_FESTIVAL_KEY, 10, 1, "json").enqueue(object :
////                Callback<FestivalResponse> {
////                override fun onResponse(
////                    call: Call<FestivalResponse>,
////                    response: Response<FestivalResponse>
////                ) {
////                    if (response.isSuccessful) {
////
////                        binding.festivalViewPager.adapter = festivalAdapter
////
////                        festivalAdapter.differ.submitList(response.body()?.getFestivalKr?.item)
////
////                    }
////                }
////
////                override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
////                    Log.e(TAG, t.message.toString())
////
////                }
////            })
////    }
//
//
//    private fun parseNotificationData() {
//        Log.e("HomeFragment에서", "parseNotificationData가 실행되었습니다.")
//        requireActivity().intent?.let {
//            if (it.hasExtra(EXTRA_CHANNEL_ID) && it.hasExtra(EXTRA_MESSAGE_ID) && it.hasExtra(
//                    EXTRA_CHANNEL_TYPE
//                )
//            ) {
//                val channelType = it.getStringExtra(EXTRA_CHANNEL_TYPE)
//                val channelId = it.getStringExtra(EXTRA_CHANNEL_ID)
//                val cid = "$channelType:$channelId"
//                val messageId = it.getStringExtra(EXTRA_MESSAGE_ID)
//                val parentMessageId = it.getStringExtra(EXTRA_PARENT_MESSAGE_ID)
//
//                requireActivity().intent = null
//
//                // 새로운 인텐트 생성
//                val intent = Intent(context, ChannelActivity::class.java).apply {
//                    putExtra("key:cid", cid)
//                    putExtra(EXTRA_MESSAGE_ID, messageId)
//                    putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
//                }
//                Log.e("HomeFragment에서", "ChannelActivity로 이동을 시작합니다.")
//
//                startActivity(intent)
//            }
//        }
//
//
////    private fun requestLocationPermission() {
////        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
////                // 사용자에게 권한이 필요한 이유 설명 후 권한 요청
////                showRationaleDialog("위치 권한 필요", "이 기능을 사용하기 위해 위치 권한이 필요합니다.", Manifest.permission.ACCESS_FINE_LOCATION)
////            } else {
////                // 권한 요청
////                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
////            }
////        } else {
////            // 권한이 이미 허용된 경우
////            proceedWithLocationAccess()
////        }
////    }
////
////    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
////        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////            // 권한이 허용됐을 때
////            proceedWithLocationAccess()
////            fetchTourApi()
////        } else {
////            // 권한이 거부됐을 때
////            if (ActivityCompat.shouldShowRequestPermissionRationale(
////                    requireActivity(),
////                    Manifest.permission.ACCESS_FINE_LOCATION
////                )
////            ) {
////                // 사용자에게 왜 퍼미션이 필요한지 설명을 제공하고, 요청을 다시 시도할 수 있습니다.
////                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
////            } else {
////                // 처음 퍼미션을 요청하거나, '다시 묻지 않기'를 선택했을 경우
////                ActivityCompat.requestPermissions(
////                    requireActivity(),
////                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
////                    LOCATION_PERMISSION_REQUEST_CODE
////                )
////            }
////            Toast.makeText(context, "위치 권한 거부됨", Toast.LENGTH_SHORT).show()
////        }
////    }
////
////    private fun proceedWithLocationAccess() {
////        // 위치 권한이 있을 때 수행할 작업
////        Toast.makeText(context, "위치 권한 허용됨", Toast.LENGTH_SHORT).show()
////    }
////
////    private fun showRationaleDialog(title: String, message: String, permission: String) {
////        AlertDialog.Builder(requireContext())
////            .setTitle(title)
////            .setMessage(message)
////            .setPositiveButton("확인") { dialog, which ->
////                requestPermissions(arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
////            }
////            .setNegativeButton("취소") { dialog, which ->
////                dialog.dismiss()
////            }
////            .create().show()
////    }
//
//
//    }
//}