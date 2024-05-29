//package com.kwonminseok.busanpartners.ui.home
//
//import android.content.Context
//import com.google.gson.GsonBuilder
//import com.kwonminseok.busanpartners.data.TouristDestinationResponse
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Call
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//// Define an API interface
//interface BusanFestivalApiService {
//
//    companion object {
//
//
//        @Volatile //인스턴스가 메인 메모리를 바로 참조하여 인스턴스 중복 생성 방지
//        private var INSTANCE: BusanFestivalApiService? = null
//
//
//
//
//
//        /*        private val retrofit: Retrofit
//            get() = Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:5000/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()*/
//        fun create(context: Context): BusanFestivalApiService {
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
//                .baseUrl("http://apis.data.go.kr/6260000/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build()
//                .create(BusanFestivalApiService::class.java)
//        }
//
//
//        fun init(context: Context) = INSTANCE ?: synchronized(this) {
//            INSTANCE ?: create(context).also {
//                INSTANCE = it
//            }
//        }
//        fun getInstance(): BusanFestivalApiService = INSTANCE!!
//
//    }
//
//    @GET("FestivalService/getFestivalKr")
//    fun getFestivalsKr(
//        @Query("serviceKey") apiKey: String,
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("resultType") type: String
//    ): Call<FestivalResponse>
//
//    @GET("AttractionService/getAttractionKr")
//    fun getTouristDestination(
//        @Query("serviceKey") apiKey: String,
//        @Query("numOfRows") numOfRows: Int,
//        @Query("pageNo") pageNo: Int,
//        @Query("resultType") type: String
//    ): Call<TouristDestinationResponse>
//
//}
//
//
