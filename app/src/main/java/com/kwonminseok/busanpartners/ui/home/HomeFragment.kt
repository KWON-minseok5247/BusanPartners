package com.kwonminseok.busanpartners.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.adapter.FestivalAdapter
import com.kwonminseok.busanpartners.adapter.TourismAdapter
import com.kwonminseok.busanpartners.adapter.TouristDestinationAdapter
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.data.TourismResponse
import com.kwonminseok.busanpartners.databinding.FragmentHomeBinding
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG = "HomeFragment"
class HomeFragment : Fragment() {

    private val touristDestinationAdapter by lazy { TouristDestinationAdapter() }
    private val tourismAdapter by lazy { TourismAdapter() }
    private val festivalAdapter by lazy { FestivalAdapter() }
    lateinit var binding: FragmentHomeBinding


    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

                val currentLatitude = it.latitude
                val currentLongitude = it.longitude
                Log.e("currentLatitude",currentLatitude.toString() )
                Log.e("currentLongitude",currentLongitude.toString() )

                TourismApiService.getInstance().locationBasedList1(10,1,"AND","BusanPartners","json",
                    currentLongitude,currentLatitude,10000,12,null,BuildConfig.BUSAN_FESTIVAL_KEY).enqueue(object :
                    Callback<TourismResponse> {
                    override fun onResponse(call: Call<TourismResponse>,
                                            response: Response<TourismResponse>) {
                        if (response.isSuccessful) {
                            binding.touristRecyclerView.adapter = tourismAdapter
//                    val exceptedImageData = response.body()?.response?.body?.items?.item
//                    tourismAdapter.differ.submitList(response.body()?.response?.body?.items?.item)
                            response.body()?.response?.body?.items?.item?.let { itemList ->
                                val itemsWithImages = itemList.filter { it.firstimage != "" && it.firstimage.isNotEmpty() }
                                tourismAdapter.differ.submitList(itemsWithImages)
                            }

                            Log.e(TAG, response.body()?.response?.body?.items.toString())
                        }
                    }

                    override fun onFailure(call: Call<TourismResponse>, t: Throwable) {
                        Log.e(TAG,t.message.toString())

                    }
                })            } ?: run {
//                // 위치 정보가 없는 경우, 기본 위치 사용 (부산 시청)
//                initializeMap(LatLng(35.1798159, 129.0750222))
            }
        }


        // 토큰을 확인하기 위해 서버 시간을 가져오는 과정
//        lifecycleScope.launch {
//            TimeRepository.fetchCurrentTime()
//        }
//
//        TourismApiService.getInstance().locationBasedList1(100,1,"AND","BusanPartners","json",
//             129.1059852,35.1335411,40000,12,null,BuildConfig.BUSAN_FESTIVAL_KEY).enqueue(object :
//            Callback<TourismResponse> {
//            override fun onResponse(call: Call<TourismResponse>,
//                                    response: Response<TourismResponse>) {
//                if (response.isSuccessful) {
//                    binding.touristRecyclerView.adapter = tourismAdapter
////                    val exceptedImageData = response.body()?.response?.body?.items?.item
////                    tourismAdapter.differ.submitList(response.body()?.response?.body?.items?.item)
//                    response.body()?.response?.body?.items?.item?.let { itemList ->
//                        val itemsWithImages = itemList.filter { it.firstimage != "" && it.firstimage.isNotEmpty() }
//                        tourismAdapter.differ.submitList(itemsWithImages)
//                    }
//
//                    Log.e(TAG, response.body()?.response?.body?.items.toString())
//                }
//            }
//
//            override fun onFailure(call: Call<TourismResponse>, t: Throwable) {
//                Log.e(TAG,t.message.toString())
//
//            }
//        })


//        // festival 정보 가져오는 함수
//        getFestivalInformation()
//
//
//        BusanFestivalApiService.getInstance().getTouristDestination(BuildConfig.BUSAN_FESTIVAL_KEY, 10, 1, "json").enqueue(object :
//            Callback<TouristDestinationResponse> {
//            override fun onResponse(call: Call<TouristDestinationResponse>,
//                                    response: Response<TouristDestinationResponse>) {
//                if (response.isSuccessful) {
//                    binding.touristRecyclerView.adapter = touristDestinationAdapter
//                    touristDestinationAdapter.differ.submitList(response.body()?.getAttractionKr?.item)
//                }
//            }
//            override fun onFailure(call: Call<TouristDestinationResponse>, t: Throwable) {
//                Log.e(TAG,t.message.toString())
//
//            }
//        })



    }
//
//    private fun getFestivalInformation() {
//        BusanFestivalApiService.getInstance()
//            .getFestivalsKr(BuildConfig.BUSAN_FESTIVAL_KEY, 10, 1, "json").enqueue(object :
//                Callback<FestivalResponse> {
//                override fun onResponse(
//                    call: Call<FestivalResponse>,
//                    response: Response<FestivalResponse>
//                ) {
//                    if (response.isSuccessful) {
//
//                        binding.festivalViewPager.adapter = festivalAdapter
//
//                        festivalAdapter.differ.submitList(response.body()?.getFestivalKr?.item)
//
//                    }
//                }
//
//                override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
//                    Log.e(TAG, t.message.toString())
//
//                }
//            })
//    }


}