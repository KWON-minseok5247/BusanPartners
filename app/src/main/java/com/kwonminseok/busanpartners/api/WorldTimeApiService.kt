package com.kwonminseok.busanpartners.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.kwonminseok.busanpartners.mainScreen.home.BusanFestivalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface WorldTimeApiService {
    companion object {


        @Volatile //인스턴스가 메인 메모리를 바로 참조하여 인스턴스 중복 생성 방지
        private var INSTANCE: WorldTimeApiService? = null
        fun create(context: Context): WorldTimeApiService {

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
                .baseUrl("https://worldtimeapi.org/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
                .create(WorldTimeApiService::class.java)
        }


        fun init(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: create(context).also {
                INSTANCE = it
            }
        }
        fun getInstance(): WorldTimeApiService = INSTANCE!!

    }
    @GET("timezone/Asia/Seoul")
    suspend fun getSeoulTime(): WorldTimeResponse
}

data class WorldTimeResponse(
    val abbreviation: String,
    val datetime: String,
    val day_of_week: Int,
    val day_of_year: Int,
    val timezone: String,
    val unixtime: Long,
    val utc_datetime: String,
    val utc_offset: String,
    val week_number: Int
)
