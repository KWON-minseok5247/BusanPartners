package com.kwonminseok.busanpartners.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.data.TourismResponse
import com.kwonminseok.busanpartners.ui.home.FestivalResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


// Define an API interface
interface TourismApiService {

    companion object {


        @Volatile //인스턴스가 메인 메모리를 바로 참조하여 인스턴스 중복 생성 방지
        private var INSTANCE: TourismApiService? = null

        /*        private val retrofit: Retrofit
            get() = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()*/
        fun create(context: Context): TourismApiService {

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY


            val client = OkHttpClient.Builder()
//                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
//                .addInterceptor(com.self.dailyqself.AuthInterceptor())
//                .authenticator(TokenAuthenticator)
//                .connectTimeout(5000, TimeUnit.SECONDS)
//                .readTimeout(5000, TimeUnit.SECONDS)
//                .writeTimeout(5000, TimeUnit.SECONDS)
                .build()

            val gson = GsonBuilder()
//                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setLenient()
                .create()


            return Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/B551011/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
                .create(TourismApiService::class.java)
        }


        fun init(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: create(context).also {
                INSTANCE = it
            }
        }

        fun getInstance(): TourismApiService = INSTANCE!!

    }

    // 한국어 위치기반 관광정보 조회
    @GET("KorService1/locationBasedList1")
    fun korLocationBasedList1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") responseType: String = "json",
        @Query("mapX") mapX: Double,
        @Query("mapY") mapY: Double,
        @Query("radius") radius: Int,
        @Query("contentTypeId") contentTypeId: Int = 12,
        @Query("modifiedtime") modifiedtime: String? = null,
        @Query("serviceKey") serviceKey: String,
    ): Call<TourismResponse>


    // 한국어 소개 정보 조회
    @GET("KorService1/detailIntro1")
    fun korDetailIntro1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "BusanPartners",
        @Query("_type") responseType: String = "json",
        @Query("contentTypeId") contentTypeId: Int,
        @Query("contentId") contentId: Int,
        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
    ): Call<TourismResponse>


    // 한국어 공통 정보 조회
    @GET("KorService1/detailCommon1")
    fun korDetailCommon1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "BusanPartners",
        @Query("_type") responseType: String = "json",
//        @Query("firstImageYN") firstImageYN: String = "N",
        @Query("contentTypeId") contentTypeId: Int,
        @Query("contentId") contentId: Int,
        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
        @Query("defaultYN") defaultYN: String = "Y",
        @Query("overviewYN") overviewYN: String = "Y"
    ): Call<TourismResponse>

    // 한국어 이미지 정보 조회
    @GET("KorService1/detailImage1")
    fun korDetailImage1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "BusanPartners",
        @Query("_type") responseType: String = "json",
        @Query("imageYN") imageYN: String ="Y",
        @Query("contentId") contentId: Int,
        @Query("serviceKey") serviceKey: String = BuildConfig.BUSAN_FESTIVAL_KEY,
    ): Call<TourismResponse>


    // 영어
    @GET("EngService1/locationBasedList1")
    fun engLocationBasedList1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") responseType: String = "json",
        @Query("mapX") mapX: Double,
        @Query("mapY") mapY: Double,
        @Query("radius") radius: Int,
        @Query("contentTypeId") contentTypeId: Int = 76,
        @Query("modifiedtime") modifiedtime: String? = null,
        @Query("serviceKey") serviceKey: String,
    ): Call<TourismResponse>

    // 중국어 번체
    @GET("ChtService1/locationBasedList1")
    fun chtLocationBasedList1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") responseType: String = "json",
        @Query("mapX") mapX: Double,
        @Query("mapY") mapY: Double,
        @Query("radius") radius: Int,
        @Query("contentTypeId") contentTypeId: Int = 76,
        @Query("modifiedtime") modifiedtime: String? = null,
        @Query("serviceKey") serviceKey: String,
    ): Call<TourismResponse>

    //중국어 간체
    @GET("ChsService1/locationBasedList1")
    fun chsLocationBasedList1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") responseType: String = "json",
        @Query("mapX") mapX: Double,
        @Query("mapY") mapY: Double,
        @Query("radius") radius: Int,
        @Query("contentTypeId") contentTypeId: Int = 76,
        @Query("modifiedtime") modifiedtime: String? = null,
        @Query("serviceKey") serviceKey: String,
    ): Call<TourismResponse>

    //일본어
    @GET("JpnService1/locationBasedList1")
    fun jpnLocationBasedList1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") responseType: String = "json",
        @Query("mapX") mapX: Double,
        @Query("mapY") mapY: Double,
        @Query("radius") radius: Int,
        @Query("contentTypeId") contentTypeId: Int = 76,
        @Query("modifiedtime") modifiedtime: String? = null,
        @Query("serviceKey") serviceKey: String,
    ): Call<TourismResponse>


    @GET("KorService1/searchFestival1")
    fun searchFestival1(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("MobileOS") mobileOS: String,
        @Query("MobileApp") mobileApp: String,
        @Query("_type") responseType: String = "json",
        @Query("eventStartDate") eventStartDate: String,
        @Query("eventEndDate") eventEndDate: String,
        @Query("serviceKey") serviceKey: String,
        @Query("areaCode") areaCode: Int,
        @Query("sigunguCode") sigunguCode: Int? = null
    ): Call<FestivalResponse>


}
