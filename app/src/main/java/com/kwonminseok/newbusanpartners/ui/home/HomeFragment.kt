package com.kwonminseok.newbusanpartners.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.barnea.dialoger.Dialoger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.inappmessaging.ktx.inAppMessaging
import com.google.firebase.ktx.Firebase
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.adapter.FestivalAdapter
import com.kwonminseok.newbusanpartners.adapter.TourismAdapter
import com.kwonminseok.newbusanpartners.api.TourismAllInOneApiService
import com.kwonminseok.newbusanpartners.data.TourismResponse
import com.kwonminseok.newbusanpartners.databinding.FragmentHomeBinding
import com.kwonminseok.newbusanpartners.extensions.setStatusBarTransparent
import com.kwonminseok.newbusanpartners.extensions.setStatusBarVisible
import com.kwonminseok.newbusanpartners.ui.EXTRA_CHANNEL_ID
import com.kwonminseok.newbusanpartners.ui.EXTRA_CHANNEL_TYPE
import com.kwonminseok.newbusanpartners.ui.EXTRA_MESSAGE_ID
import com.kwonminseok.newbusanpartners.ui.EXTRA_PARENT_MESSAGE_ID
import com.kwonminseok.newbusanpartners.ui.login.SplashActivity.Companion.currentServerTime
import com.kwonminseok.newbusanpartners.ui.login.SplashActivity.Companion.currentUser
import com.kwonminseok.newbusanpartners.ui.message.ChannelActivity
import com.kwonminseok.newbusanpartners.util.LanguageUtils
import com.kwonminseok.newbusanpartners.util.LanguageUtils.getDeviceLanguage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.util.FusedLocationSource
import org.threeten.bp.OffsetDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.random.Random

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.e("homeFramgent까지는", "왔다는 증거")
        parseNotificationData()

        // 인앱메시지
//        setupInAppMessaging()


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
//        viewModel = ViewModelProvider(this, TourismViewModelFactory(repository)).get(TourismViewModel::class.java)
        currentServerTime?.let { fetchFestivalList(it) }

        FirebaseAnalytics.getInstance(requireContext()).logEvent("main_activity_inappmessaging",null)
        FirebaseInAppMessaging.getInstance().triggerEvent("main_activity_inappmessaging");

//        setupInAppMessaging()

        val travelerFinish = sharedPreferences.getBoolean("traveler_finish", false)
        val isFirstVisitor = sharedPreferences.getBoolean("is_first_visitor", false)
        val isFirstStudent = sharedPreferences.getBoolean("is_first_student", false)


        Log.e("travelerFinish", travelerFinish.toString())
        Log.e("isFirstVisitor", isFirstVisitor.toString())
        Log.e("isFirstStudent", isFirstStudent.toString())

        if (travelerFinish) {
            sharedPreferences.edit().putBoolean("traveler_finish", false).apply()
            sharedPreferences.edit().putBoolean("is_first_visit", true).apply()

            Dialoger(requireContext(), Dialoger.TYPE_MESSAGE)
                .setTitle(getString(R.string.thanks_for_using))
                .setDescription(getString(R.string.great_experience_in_busan))
                .setDrawable(R.drawable.logo_transparent_background_only_logo)
                .setButtonText(getString(R.string.confirmation))
                .setButtonOnClickListener {
                }
                .show()


        }
        if (isFirstVisitor) {
//            if (isFirstVisitor) {

            sharedPreferences.edit().putBoolean("is_first_visitor", false).apply()

            val languageTag = getDeviceLanguage(requireContext())
            val locale = Locale.forLanguageTag(languageTag)
            val tokenTime = formatDateTime(currentUser!!.tokenTime.toString(), locale)

            Dialoger(requireContext(), Dialoger.TYPE_MESSAGE)
                .setDrawable(R.drawable.logo_transparent_background_only_logo)
                .setTitle(getString(R.string.authentication_completed))
//                .setDescription("${getString(R.string.welcome_traveler, tokenTime)} ${getString(R.string.contact_students_first)}")
                .setDescription(getString(R.string.welcome_traveler, tokenTime))
//                .setDescription("${outputFormat.format(date!!)} ${getString(R.string.contact_students_first)}")
                .setButtonText(getString(R.string.confirmation))
                .setButtonOnClickListener {
                }
                .show()

        }

        if (isFirstStudent) {
            sharedPreferences.edit().putBoolean("is_first_student", false).apply()

            Dialoger(requireContext(), Dialoger.TYPE_MESSAGE)
                .setTitle(getString(R.string.authentication_completed))
                .setDrawable(R.drawable.logo_transparent_background_only_logo)
                .setDescription(getString(R.string.make_precious_memories))
                .setButtonText(getString(R.string.confirmation))
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
                            getString(R.string.press_back_again_to_exit),
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
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_permission_required),
                    Toast.LENGTH_SHORT
                ).show()
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
                    binding.vpPlaces.adapter = tourismAdapter

                    val userLatLng = LatLng(currentLatitude, currentLongitude)
                    val finalLatLng = if (isInBusan(userLatLng)) {
                        userLatLng
                    } else {
                        getRandomLatLngInBusan() // BUSAN_DEFAULT 대신 랜덤 좌표 사용
                    }
                    fetchLocationBasedList(finalLatLng.longitude, finalLatLng.latitude)
                } ?: run {
//                    fetchLocationBasedList(BUSAN_DEFAULT.longitude, BUSAN_DEFAULT.latitude)
                    fetchLocationBasedList(
                        getRandomLatLngInBusan().longitude,
                        getRandomLatLngInBusan().latitude
                    )
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
            bundle.putString("firstImage", tourismItem.firstimage)

            findNavController().navigate(
                R.id.action_homeFragment_to_tourismPlaceDetailFragment,
                bundle,

                )

        }

//        Firebase.inAppMessaging.isAutomaticDataCollectionEnabled = true

    }


    private fun fetchLocationBasedList(longitude: Double, latitude: Double, retryCount: Int = 3) {
        tourismApiService.locationBasedList1(
            numOfRows = 18,
            pageNo = 1,
            mapX = longitude,
            mapY = latitude,
            radius = 20000,
            contentTypeId = LanguageUtils.getContentIdForTourPlace(requireContext())
        ).enqueue(object : Callback<TourismResponse> {
            override fun onResponse(
                call: Call<TourismResponse>,
                response: Response<TourismResponse>
            ) {
                if (response.isSuccessful) {
                    _binding?.let { binding ->
                        binding.shimmerPlaces.stopShimmer()
                        binding.shimmerPlaces.visibility = View.GONE
                        binding.vpPlaces.visibility = View.VISIBLE

                        response.body()?.response?.body?.items?.item?.let { itemList ->
                            val itemsWithImages = itemList.filter { it.firstimage.isNotEmpty() }
                            tourismAdapter.differ.submitList(itemsWithImages)
                        }
                    }
                } else {
                    Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
                    handleFailure(call, this, retryCount)
                }
            }

            override fun onFailure(call: Call<TourismResponse>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message}")
                handleFailure(call, this, retryCount)

            }
        })
    }

    private fun fetchFestivalList(retryCount: Int = 3) {

        tourismApiService.searchFestival1(
            numOfRows = 10,
            pageNo = 1,
            eventStartDate = firstDate,
            eventEndDate = secondDate,
        ).enqueue(object : Callback<FestivalResponse> {
            override fun onResponse(
                call: Call<FestivalResponse>,
                response: Response<FestivalResponse>
            ) {
                if (response.isSuccessful) {
                    _binding?.let { binding ->
                        binding.shimmerFestival.stopShimmer()
                        binding.shimmerFestival.visibility = View.GONE
                        binding.vpFestivals.visibility = View.VISIBLE

                        binding.vpFestivals.adapter = festivalAdapter
                        binding.vpFestivals.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        response.body()?.response?.body?.items?.item?.let { itemList ->
                            val itemsWithImages = itemList.filter { it.firstimage.isNotEmpty() }
                            festivalAdapter.differ.submitList(itemsWithImages)
                        }
                    }
                } else {
                    Log.e(TAG, "Response failed: ${response.errorBody()?.string()}")
                    handleFailure(call, this, retryCount)
                }
            }

            override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
                Log.e(TAG, "Response failed: ${t.message}")
                handleFailure(call, this, retryCount)

            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


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


    }

    override fun onResume() {
        super.onResume()
        requireActivity().setStatusBarTransparent()

//        inAppMessagingInitialization(context,false,"main_activity_inappmessaging"); //Starts inAppMessaging


    }

    override fun onPause() {
        super.onPause()
        requireActivity().setStatusBarVisible()

    }


    private fun fetchFestivalList(currentServerTime: String) {
        // API 버전 26 이상일 때
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = LocalDateTime.parse(currentServerTime, formatter)

        // 첫 번째 날짜: 원래 날짜
        firstDate = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        // 두 번째 날짜: 3개월 후
        val dateAfterThreeMonths = dateTime.plus(3, ChronoUnit.MONTHS)
        secondDate =
            dateAfterThreeMonths.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        Log.e("firstDate secondDate", "$firstDate $secondDate")
    }

    private fun isInBusan(location: LatLng): Boolean {
        return location.latitude in BUSAN_SW.latitude..BUSAN_NE.latitude &&
                location.longitude in BUSAN_SW.longitude..BUSAN_NE.longitude
    }

    private fun getRandomLatLngInBusan(): LatLng {
        val randomLatitude =
            BUSAN_SW.latitude + Random.nextDouble() * (BUSAN_NE.latitude - BUSAN_SW.latitude)
        val randomLongitude =
            BUSAN_SW.longitude + Random.nextDouble() * (BUSAN_NE.longitude - BUSAN_SW.longitude)
        return LatLng(randomLatitude, randomLongitude)
    }

    //    private fun <T> handleFailure(call: Call<T>, callback: Callback<T>, retryCount: Int) {
//        if (retryCount > 0) {
//            Log.e(TAG, "Retrying... ($retryCount retries left)")
//            call.clone().enqueue(callback)
//        } else {
//            Log.e(TAG, "Max retries reached. Giving up.")
////            Toast.makeText(context, getString(R.string.error_retrieving_data), Toast.LENGTH_SHORT).show()
//        }
//    }
    private fun <T> handleFailure(call: Call<T>, callback: Callback<T>, retryCount: Int) {
        if (retryCount > 0) {
            Log.e(TAG, "Retrying... ($retryCount retries left)")
            call.clone().enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        callback.onResponse(call, response)
                    } else {
                        handleFailure(call, callback, retryCount - 1)
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    handleFailure(call, callback, retryCount - 1)
                }
            })
        } else {
            Log.e(TAG, "Max retries reached. Giving up.")
            callback.onFailure(call, Throwable("Max retries reached"))
            // Toast.makeText(context, getString(R.string.error_retrieving_data), Toast.LENGTH_SHORT).show()
        }
    }

    fun formatDateTime(dateTimeString: String, locale: Locale): String {
        val offsetDateTime = OffsetDateTime.parse(dateTimeString)
        Log.e("locale", locale.language)

        // 로케일에 따라 날짜 형식 설정
        val pattern = when (locale.language) {
            "ja" -> "yyyy年MM月dd日" // 일본어
            "en" -> "MMMM dd, yyyy" // 영어
            "zh" -> if (locale.country == "CN") "yyyy年MM月dd日" else "yyyy年MM月dd日" // 중국어 (간체, 번체)
            "es" -> "dd 'de' MMMM 'de' yyyy" // 에스파냐어
            "th" -> "dd MMM yyyy" // 태국어
            "vi" -> "dd MMM yyyy" // 베트남어
            "in" -> "dd MMM yyyy" // 인도네시아어
            "ko" -> "yyyy년 MM월 dd일"
            else -> "MMMM dd, yyyy"//
        }

        val formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern(pattern, locale)
        return offsetDateTime.format(formatter)
    }

    private fun setupInAppMessaging() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        // 로그인 상태에 따라 인앱 메시지 표시 제어
        Firebase.inAppMessaging.isAutomaticDataCollectionEnabled = isLoggedIn

        // 인앱 메시지 표시 여부를 설정
        if (isLoggedIn) {
            FirebaseInAppMessaging.getInstance().setMessagesSuppressed(false)
        } else {
            FirebaseInAppMessaging.getInstance().setMessagesSuppressed(true)
        }
    }

}