package com.kwonminseok.busanpartners.ui.home

import TourismViewModelFactory
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.barnea.dialoger.Dialoger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.FestivalAdapter
import com.kwonminseok.busanpartners.adapter.TourismAdapter
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.data.TourismResponse
import com.kwonminseok.busanpartners.databinding.FragmentHomeBinding
import com.kwonminseok.busanpartners.extensions.setStatusBarTransparent
import com.kwonminseok.busanpartners.extensions.setStatusBarVisible
import com.kwonminseok.busanpartners.extensions.statusBarHeight
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_ID
import com.kwonminseok.busanpartners.ui.EXTRA_CHANNEL_TYPE
import com.kwonminseok.busanpartners.ui.EXTRA_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.EXTRA_PARENT_MESSAGE_ID
import com.kwonminseok.busanpartners.ui.login.SplashActivity
import com.kwonminseok.busanpartners.ui.login.SplashActivity.Companion.currentServerTime
import com.kwonminseok.busanpartners.ui.message.ChannelActivity
import com.kwonminseok.busanpartners.util.LanguageUtils
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.TimeZone

private val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    //여기서 userEntity랑 user랑 같은지?
//    private val touristDestinationAdapter by lazy { TouristDestinationAdapter() }
    private val tourismAdapter by lazy { TourismAdapter() }
    private val festivalAdapter by lazy { FestivalAdapter() }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: TourismViewModel

    private var backPressedTime: Long = 0
    private lateinit var toast: Toast
    private lateinit var firstDate: String
    private lateinit var secondDate: String

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tourismApiService: TourismAllInOneApiService
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private val BUSAN_SW = LatLng(35.0500, 128.9400) // 부산시 남서쪽 좌표를 조정
        private val BUSAN_NE = LatLng(35.2700, 129.2100) // 부산시 북동쪽 좌표를 조정
        private val BUSAN_DEFAULT = LatLng(35.1335411, 129.1059852) // 기본 좌표 (부산 시청)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parseNotificationData()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //shimmer
        binding.shimmerFestival.startShimmer()
        binding.shimmerPlaces.startShimmer()

        // Repository 초기화
        val repository = TourismRepository(TourismAllInOneApiService.getInstance())

        // ViewModel 초기화
        viewModel = ViewModelProvider(this, TourismViewModelFactory(repository)).get(TourismViewModel::class.java)
        currentServerTime?.let { fetchFestivalList(it) }


        sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val travelerFinish = sharedPreferences.getBoolean("traveler_finish",false)
        val isFirstVisitor = sharedPreferences.getBoolean("is_first_visitor",false)
        val isFirstStudent = sharedPreferences.getBoolean("is_first_student",false)


        Log.e("travelerFinish", travelerFinish.toString())
        Log.e("isFirstVisitor", isFirstVisitor.toString())
        Log.e("isFirstStudent", isFirstStudent.toString())

        if (travelerFinish) {
            sharedPreferences.edit().putBoolean("traveler_finish",false).apply()

            Dialoger(requireContext(), Dialoger.TYPE_MESSAGE)
                .setTitle("부산파트너스를 이용해주셔서 감사합니다.")
                .setDescription("다음에도 부산을 꼭 찾아주세요.")
                .setDrawable(R.drawable.logo_transparent_background)
                .setButtonText("확인")
                .setButtonOnClickListener {
                }
                .show()

        }

        if (isFirstVisitor) {
            sharedPreferences.edit().putBoolean("is_first_visitor",false).apply()

            Dialoger(requireContext(), Dialoger.TYPE_MESSAGE)
                .setTitle("인증이 완료되었습니다.")
                .setDescription("대학생들에게 먼저 연락해보세요.")
                .setButtonText("확인")
                .setButtonOnClickListener {
                }
                .show()

        }

        if (isFirstStudent) {
            sharedPreferences.edit().putBoolean("is_first_student",false).apply()

            Dialoger(requireContext(), Dialoger.TYPE_MESSAGE)
                .setTitle("인증이 완료되었습니다.")
                .setDescription("마음껏 관광객들과 어울려보세요.")
                .setButtonText("확인")
                .setButtonOnClickListener {
                }
                .show()

        }


        tourismApiService = TourismAllInOneApiService.getInstance()

        // 뒤로가기 2번시 종료
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - backPressedTime < 2000) {
                        toast.cancel()
                        requireActivity().finish()
                    } else {
                        backPressedTime = System.currentTimeMillis()
                        toast = Toast.makeText(
                            requireContext(),
                            "뒤로가기를 한 번 더 누르면 종료됩니다.",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                    }
                }
            })

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
        } else {
            // 퍼미션이 이미 허용된 경우
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


            fetchFestivalList()
            // 초기 위치 설정 과정
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // 사용자의 현재 위치를 받았습니다. 지도 로딩을 시작합니다.
                location?.let {
                    val currentLatitude = it.latitude
                    val currentLongitude = it.longitude
                    Log.e("currentLatitude", currentLatitude.toString())
                    Log.e("currentLongitude", currentLongitude.toString())
                    binding.vpPlaces.adapter = tourismAdapter

                    val userLatLng = LatLng(currentLatitude, currentLongitude)
                    val finalLatLng = if (isInBusan(userLatLng)) {
                        userLatLng
                    } else {
                        BUSAN_DEFAULT
                    }
                    fetchLocationBasedList(finalLatLng.longitude, finalLatLng.latitude)
                } ?: run {
                    fetchLocationBasedList(BUSAN_DEFAULT.longitude, BUSAN_DEFAULT.latitude)
                }
            }


        }


        festivalAdapter.onFestivaltClick = { festival ->
            val bundle = Bundle()
            bundle.apply {
                putString("contentId", festival.contentid)
                putString("eventstartdate", festival.eventstartdate)
                putString("eventenddate", festival.eventenddate)
                putString("firstImage", festival.firstimage)
            }

            findNavController().navigate(R.id.action_homeFragment_to_festivalDetailFragment, bundle)
        }

        tourismAdapter.onTourismPlaceClick = { tourismItem ->
            val bundle = Bundle()
            bundle.putString("contentId", tourismItem.contentid)
            findNavController().navigate(
                R.id.action_homeFragment_to_tourismPlaceDetailFragment,
                bundle
            )

        }


    }


    private fun fetchLocationBasedList(longitude: Double, latitude: Double) {
        tourismApiService.locationBasedList1(
            numOfRows = 18,
            pageNo = 1,
            mapX = longitude,
            mapY = latitude,
            radius = 20000,
            contentTypeId = LanguageUtils.getContentIdForTourPlace(requireContext())
        ).enqueue(object : Callback<TourismResponse> {
            override fun onResponse(call: Call<TourismResponse>, response: Response<TourismResponse>) {
                if (response.isSuccessful) {

                    _binding?.let { binding ->
                        if (response.isSuccessful) {
                            binding.shimmerPlaces.stopShimmer()
                            binding.shimmerPlaces.visibility = View.GONE
                            binding.vpPlaces.visibility = View.VISIBLE

                            response.body()?.response?.body?.items?.item?.let { itemList ->
                                val itemsWithImages =
                                    itemList.filter { it.firstimage.isNotEmpty() }
                                tourismAdapter.differ.submitList(itemsWithImages)
                            }
                        } else {
                            Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to get tourism data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TourismResponse>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message}")
            }
        })
    }

    private fun fetchFestivalList() {

        tourismApiService.searchFestival1(
            numOfRows = 10,
            pageNo = 1,
            eventStartDate = firstDate,
            eventEndDate = secondDate,
        ).enqueue(object : Callback<FestivalResponse> {
            override fun onResponse(call: Call<FestivalResponse>, response: Response<FestivalResponse>) {
                if (response.isSuccessful) {

                    _binding?.let { binding ->
                        if (response.isSuccessful) {
                            binding.shimmerFestival.stopShimmer()
                            binding.shimmerFestival.visibility = View.GONE
                            binding.vpFestivals.visibility = View.VISIBLE

                            binding.vpFestivals.adapter = festivalAdapter
                            binding.vpFestivals.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            response.body()?.response?.body?.items?.item?.let { itemList ->
                                val itemsWithImages =
                                    itemList.filter { it.firstimage.isNotEmpty() }
                                festivalAdapter.differ.submitList(itemsWithImages)
                            }
                        } else {
                            Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to get festival data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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


    private fun parseNotificationData() {
        Log.e("HomeFragment에서", "parseNotificationData가 실행되었습니다.")
        requireActivity().intent?.let {
            if (it.hasExtra(EXTRA_CHANNEL_ID) && it.hasExtra(EXTRA_MESSAGE_ID) && it.hasExtra(
                    EXTRA_CHANNEL_TYPE
                )
            ) {
                val channelType = it.getStringExtra(EXTRA_CHANNEL_TYPE)
                val channelId = it.getStringExtra(EXTRA_CHANNEL_ID)
                val cid = "$channelType:$channelId"
                val messageId = it.getStringExtra(EXTRA_MESSAGE_ID)
                val parentMessageId = it.getStringExtra(EXTRA_PARENT_MESSAGE_ID)

                requireActivity().intent = null

                // 새로운 인텐트 생성
                val intent = Intent(context, ChannelActivity::class.java).apply {
                    putExtra("key:cid", cid)
                    putExtra(EXTRA_MESSAGE_ID, messageId)
                    putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
                }
                Log.e("HomeFragment에서", "ChannelActivity로 이동을 시작합니다.")

                startActivity(intent)
            }
        }


//    private fun requestLocationPermission() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // 사용자에게 권한이 필요한 이유 설명 후 권한 요청
//                showRationaleDialog("위치 권한 필요", "이 기능을 사용하기 위해 위치 권한이 필요합니다.", Manifest.permission.ACCESS_FINE_LOCATION)
//            } else {
//                // 권한 요청
//                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
//            }
//        } else {
//            // 권한이 이미 허용된 경우
//            proceedWithLocationAccess()
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // 권한이 허용됐을 때
//            proceedWithLocationAccess()
//            fetchTourApi()
//        } else {
//            // 권한이 거부됐을 때
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    requireActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                // 사용자에게 왜 퍼미션이 필요한지 설명을 제공하고, 요청을 다시 시도할 수 있습니다.
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
//            } else {
//                // 처음 퍼미션을 요청하거나, '다시 묻지 않기'를 선택했을 경우
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    LOCATION_PERMISSION_REQUEST_CODE
//                )
//            }
//            Toast.makeText(context, "위치 권한 거부됨", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun proceedWithLocationAccess() {
//        // 위치 권한이 있을 때 수행할 작업
//        Toast.makeText(context, "위치 권한 허용됨", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun showRationaleDialog(title: String, message: String, permission: String) {
//        AlertDialog.Builder(requireContext())
//            .setTitle(title)
//            .setMessage(message)
//            .setPositiveButton("확인") { dialog, which ->
//                requestPermissions(arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
//            }
//            .setNegativeButton("취소") { dialog, which ->
//                dialog.dismiss()
//            }
//            .create().show()
//    }


    }

    override fun onResume() {
        super.onResume()
//        requireActivity().setStatusBarTransparent()
////        binding.fragmentHomeLayout.setPadding(0,getStatusBarHeight(requireContext()), 0, 0)
//        // 상태 바, 네비게이션 높이 만큼 padding 주기
//        view?.let {
//            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
//                val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
//                v.setPadding(0, statusBarHeight, 0, 0)
//                insets
//            }
//        }




    }



    private fun fetchFestivalList(currentServerTime: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 버전 26 이상일 때
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val dateTime = LocalDateTime.parse(currentServerTime, formatter)

            // 첫 번째 날짜: 원래 날짜
            firstDate = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

            // 두 번째 날짜: 3개월 후
            val dateAfterThreeMonths = dateTime.plus(3, ChronoUnit.MONTHS)
            secondDate = dateAfterThreeMonths.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

            Log.e("firstDate secondDate", "$firstDate $secondDate")
        } else {
            // API 버전 26 미만일 때
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(currentServerTime)

            val outputFormat = SimpleDateFormat("yyyyMMdd")
            firstDate = outputFormat.format(date)

            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MONTH, 3)
            secondDate = outputFormat.format(calendar.time)

            Log.e("firstDate secondDate", "$firstDate $secondDate")
        }
    }

    private fun isInBusan(location: LatLng): Boolean {
        return location.latitude in BUSAN_SW.latitude..BUSAN_NE.latitude &&
                location.longitude in BUSAN_SW.longitude..BUSAN_NE.longitude
    }

}