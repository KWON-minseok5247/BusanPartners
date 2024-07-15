package com.kwonminseok.busanpartners.api

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

//interface WorldTimeApiService {
//    companion object {
//
//
//        @Volatile //인스턴스가 메인 메모리를 바로 참조하여 인스턴스 중복 생성 방지
//        private var INSTANCE: WorldTimeApiService? = null
//        fun create(context: Context): WorldTimeApiService {
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
//                .baseUrl("https://worldtimeapi.org/api/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build()
//                .create(WorldTimeApiService::class.java)
//        }
//
//
//        fun init(context: Context) = INSTANCE ?: synchronized(this) {
//            INSTANCE ?: create(context).also {
//                INSTANCE = it
//            }
//        }
//        fun getInstance(): WorldTimeApiService = INSTANCE!!
//
//    }
//    @GET("timezone/Asia/Seoul")
//    suspend fun getSeoulTime(): WorldTimeResponse
//}
//
//data class WorldTimeResponse(
//    val abbreviation: String,
//    val datetime: String,
//    val day_of_week: Int,
//    val day_of_year: Int,
//    val timezone: String,
//    val unixtime: Long,
//    val utc_datetime: String,
//    val utc_offset: String,
//    val week_number: Int
//)
interface WorldTimeApiService {
    companion object {

        @Volatile
        private var INSTANCE: WorldTimeApiService? = null

        fun create(context: Context): WorldTimeApiService {
            // SSL 설정
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers = trustManagerFactory.trustManagers
            val trustManager = trustManagers[0] as X509TrustManager

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)

            // HTTP 로깅 인터셉터
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // OkHttpClient 빌더
            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .hostnameVerifier { hostname, session -> true }
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build()

            // Gson 설정
            val gson = GsonBuilder()
                .setLenient()
                .create()

            // Retrofit 빌더
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
