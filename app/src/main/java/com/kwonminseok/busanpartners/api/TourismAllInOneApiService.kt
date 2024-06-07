package com.kwonminseok.busanpartners.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.data.CommonResponse
import com.kwonminseok.busanpartners.data.ImageResponse
import com.kwonminseok.busanpartners.data.IntroResponse
import com.kwonminseok.busanpartners.data.TourismResponse
import com.kwonminseok.busanpartners.ui.home.FestivalResponse
import com.kwonminseok.busanpartners.util.LanguageUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


// Define an API interface
interface TourismAllInOneApiService {

//    companion object {
//
//
//        @Volatile //인스턴스가 메인 메모리를 바로 참조하여 인스턴스 중복 생성 방지
//        private var INSTANCE: TourismAllInOneApiService? = null
//
//        /*        private val retrofit: Retrofit
//            get() = Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:5000/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()*/
//        fun create(context: Context): TourismAllInOneApiService {
//
//            val httpLoggingInterceptor = HttpLoggingInterceptor()
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//
//
//            val client = OkHttpClient.Builder()
////                .addInterceptor(headerInterceptor)
//                .addInterceptor(httpLoggingInterceptor)
////                .addInterceptor(com.self.dailyqself.AuthInterceptor())
////                .authenticator(TokenAuthenticator)
////                .connectTimeout(5000, TimeUnit.SECONDS)
////                .readTimeout(5000, TimeUnit.SECONDS)
////                .writeTimeout(5000, TimeUnit.SECONDS)
//                .build()
//
//            val gson = GsonBuilder()
////                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                .setLenient()
//                .create()
//
//
//            return Retrofit.Builder()
//                .baseUrl("http://apis.data.go.kr/B551011/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build()
//                .create(TourismAllInOneApiService::class.java)
//        }
//
//
//        fun init(context: Context) = INSTANCE ?: synchronized(this) {
//            INSTANCE ?: create(context).also {
//                INSTANCE = it
//            }
//        }
//
//        fun getInstance(): TourismAllInOneApiService = INSTANCE!!
//
//    }

    companion object {

        @Volatile
        private var INSTANCE: TourismAllInOneApiService? = null

        fun create(context: Context): TourismAllInOneApiService {

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .baseUrl(LanguageUtils.getBaseUrl(context))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
                .create(TourismAllInOneApiService::class.java)
        }

        fun init(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: create(context).also {
                INSTANCE = it
            }
        }

        fun getInstance(): TourismAllInOneApiService = INSTANCE!!
    }


    @GET("locationBasedList1")
    fun locationBasedList1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("mapX") mapX: Double,
        @Query("mapY") mapY: Double,
        @Query("radius") radius: Int,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "BusanPartners",
        @Query("_type") responseType: String = "json",
        @Query("contentTypeId") contentTypeId: Int = 12,
        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
    ): Call<TourismResponse>


    // 한국어 소개 정보 조회
    @GET("detailIntro1")
    fun detailIntro1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("contentTypeId") contentTypeId: Int,
        @Query("contentId") contentId: Int,
        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "BusanPartners",
        @Query("_type") responseType: String = "json",

        ): Call<IntroResponse>


    // 한국어 공통 정보 조회
    @GET("detailCommon1")
    fun detailCommon1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
//        @Query("firstImageYN") firstImageYN: String = "N",
        @Query("contentTypeId") contentTypeId: Int,

        @Query("contentId") contentId: Int,
        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
        @Query("defaultYN") defaultYN: String = "Y",
        @Query("addrinfoYN") addrinfoYN: String = "Y",
        @Query("mapinfoYN") mapinfoYN: String = "Y",

        @Query("overviewYN") overviewYN: String = "Y",
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "BusanPartners",
        @Query("_type") responseType: String = "json",

        ): Call<CommonResponse>

    // 한국어 이미지 정보 조회
    @GET("detailImage1")
    fun detailImage1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("contentId") contentId: Int,
        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "BusanPartners",
        @Query("_type") responseType: String = "json",
        @Query("imageYN") imageYN: String = "Y",
        @Query("subImageYN") subImageYN: String = "Y",


        ): Call<ImageResponse>

    //한국어 행사 정보 조회
    @GET("searchFestival1")
    fun searchFestival1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("eventStartDate") eventStartDate: String,
        @Query("eventEndDate") eventEndDate: String,
        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
        @Query("areaCode") areaCode: Int = 6,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "BusanPartners",
        @Query("_type") responseType: String = "json",

        ): Call<FestivalResponse>

//
//    // 영어
//    @GET("EngService1/locationBasedList1")
//    fun engLocationBasedList1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("mapX") mapX: Double,
//        @Query("mapY") mapY: Double,
//        @Query("radius") radius: Int,
//        @Query("contentTypeId") contentTypeId: Int = 76,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<TourismResponse>
//
//    // 영어 소개 정보 조회
//    @GET("EngService1/detailIntro1")
//    fun engDetailIntro1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("contentTypeId") contentTypeId: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<TourismResponse>
//
//
//    // 영어 공통 정보 조회
//    @GET("EngService1/detailCommon1")
//    fun engDetailCommon1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
////        @Query("firstImageYN") firstImageYN: String = "N",
//        @Query("contentTypeId") contentTypeId: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("defaultYN") defaultYN: String = "Y",
//        @Query("overviewYN") overviewYN: String = "Y",
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//        @Query("addrinfoYN") addrinfoYN: String = "Y",
//        @Query("mapinfoYN") mapinfoYN: String = "Y",
//
//
//
//        ): Call<TourismResponse>
//
//    // 영어 이미지 정보 조회
//    @GET("EngService1/detailImage1")
//    fun engDetailImage1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//        @Query("imageYN") imageYN: String = "Y",
//        @Query("subImageYN") subImageYN: String = "Y",
//
//        ): Call<TourismResponse>
//
//
//    // 영어 축제 정보 조회
//    @GET("EngService1/searchFestival1")
//    fun engSearchFestival1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("eventStartDate") eventStartDate: String,
//        @Query("eventEndDate") eventEndDate: String,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("areaCode") areaCode: Int = 6,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<FestivalResponse>
//
//
//    // 중국어 번체
//    @GET("ChtService1/locationBasedList1")
//    fun chtLocationBasedList1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("mapX") mapX: Double,
//        @Query("mapY") mapY: Double,
//        @Query("radius") radius: Int,
//        @Query("contentTypeId") contentTypeId: Int = 76,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<TourismResponse>
//
//    // 중국어 번체 소개 정보 조회
//    @GET("ChtService1/detailIntro1")
//    fun chtDetailIntro1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("contentTypeId") contentTypeId: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<TourismResponse>
//
//
//    // 중국어 번체 공통 정보 조회
//    @GET("ChtService1/detailCommon1")
//    fun chtDetailCommon1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
////        @Query("firstImageYN") firstImageYN: String = "N",
//        @Query("contentTypeId") contentTypeId: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("defaultYN") defaultYN: String = "Y",
//        @Query("overviewYN") overviewYN: String = "Y",
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//        @Query("addrinfoYN") addrinfoYN: String = "Y",
//        @Query("mapinfoYN") mapinfoYN: String = "Y",
//
//
//        ): Call<TourismResponse>
//
//    // 중국어 번체 이미지 정보 조회
//    @GET("ChtService1/detailImage1")
//    fun chtDetailImage1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//        @Query("imageYN") imageYN: String = "Y",
//        @Query("subImageYN") subImageYN: String = "Y",
//
//        ): Call<TourismResponse>
//
//
//    // 중국어 번체 축제 정보 조회
//    @GET("ChtService1/searchFestival1")
//    fun chtSearchFestival1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("eventStartDate") eventStartDate: String,
//        @Query("eventEndDate") eventEndDate: String,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("areaCode") areaCode: Int = 6,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<FestivalResponse>
//
//    //중국어 간체
//    @GET("ChsService1/locationBasedList1")
//    fun chsLocationBasedList1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("mapX") mapX: Double,
//        @Query("mapY") mapY: Double,
//        @Query("radius") radius: Int,
//        @Query("contentTypeId") contentTypeId: Int = 76,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<TourismResponse>
//
//    // 중국어 간체 소개 정보 조회
//    @GET("ChsService1/detailIntro1")
//    fun chsDetailIntro1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("contentTypeId") contentTypeId: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<TourismResponse>
//
//
//    // 중국어 간체 공통 정보 조회
//    @GET("ChsService1/detailCommon1")
//    fun chsDetailCommon1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
////        @Query("firstImageYN") firstImageYN: String = "N",
//        @Query("contentTypeId") contentTypeId: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("defaultYN") defaultYN: String = "Y",
//        @Query("overviewYN") overviewYN: String = "Y",
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//        @Query("addrinfoYN") addrinfoYN: String = "Y",
//        @Query("mapinfoYN") mapinfoYN: String = "Y",
//
//
//        ): Call<TourismResponse>
//
//    // 중국어 간체 이미지 정보 조회
//    @GET("ChsService1/detailImage1")
//    fun chsDetailImage1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//        @Query("imageYN") imageYN: String = "Y",
//        @Query("subImageYN") subImageYN: String = "Y",
//
//        ): Call<TourismResponse>
//
//
//    // 중국어 간체 축제 정보 조회
//    @GET("ChsService1/searchFestival1")
//    fun chsSearchFestival1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("eventStartDate") eventStartDate: String,
//        @Query("eventEndDate") eventEndDate: String,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("areaCode") areaCode: Int = 6,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<FestivalResponse>
//
//    //일본어
//    @GET("JpnService1/locationBasedList1")
//    fun jpnLocationBasedList1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("mapX") mapX: Double,
//        @Query("mapY") mapY: Double,
//        @Query("radius") radius: Int,
//        @Query("contentTypeId") contentTypeId: Int = 76,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<TourismResponse>
//
//    // 일본어 소개 정보 조회
//    @GET("JpnService1/detailIntro1")
//    fun jpnDetailIntro1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("contentTypeId") contentTypeId: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<TourismResponse>
//
//
//    // 일본어 공통 정보 조회
//    @GET("JpnService1/detailCommon1")
//    fun jpnDetailCommon1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
////        @Query("firstImageYN") firstImageYN: String = "N",
//        @Query("contentTypeId") contentTypeId: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("defaultYN") defaultYN: String = "Y",
//        @Query("overviewYN") overviewYN: String = "Y",
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//        @Query("addrinfoYN") addrinfoYN: String = "Y",
//        @Query("mapinfoYN") mapinfoYN: String = "Y",
//
//
//        ): Call<TourismResponse>
//
//    // 일본어 이미지 정보 조회
//    @GET("JpnService1/detailImage1")
//    fun jpnDetailImage1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("contentId") contentId: Int,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//        @Query("imageYN") imageYN: String = "Y",
//        @Query("subImageYN") subImageYN: String = "Y",
//
//        ): Call<TourismResponse>
//
//    // 일본어 축제 정보 조회
//    @GET("JpnService1/searchFestival1")
//    fun jpnSearchFestival1(
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("eventStartDate") eventStartDate: String,
//        @Query("eventEndDate") eventEndDate: String,
//        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
//        @Query("areaCode") areaCode: Int = 6,
//        @Query("MobileOS") mobileOS: String = "AND",
//        @Query("MobileApp") mobileApp: String = "BusanPartners",
//        @Query("_type") responseType: String = "json",
//
//        ): Call<FestivalResponse>


}
