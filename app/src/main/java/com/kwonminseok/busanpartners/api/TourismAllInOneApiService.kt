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

}
