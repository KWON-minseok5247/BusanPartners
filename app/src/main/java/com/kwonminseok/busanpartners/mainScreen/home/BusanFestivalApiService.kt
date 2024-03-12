package com.kwonminseok.busanpartners.mainScreen.home

import com.kwonminseok.busanpartners.data.FestivalResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Define an API interface
interface BusanFestivalApiService {
    @GET("getFestivalKr")
    fun getFestivals(
        @Query("serviceKey") apiKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("_type") type: String
    ): Call<FestivalResponse>
}


