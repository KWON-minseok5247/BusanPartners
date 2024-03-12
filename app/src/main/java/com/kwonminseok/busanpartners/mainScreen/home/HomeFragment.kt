package com.kwonminseok.busanpartners.mainScreen.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.FestivalResponse
import com.kwonminseok.busanpartners.databinding.FragmentConnectBinding
import com.kwonminseok.busanpartners.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
private val TAG = "HomeFragment"
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/6260000/FestivalService/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

// Create an API service
        val service = retrofit.create(BusanFestivalApiService::class.java)

// Make an asynchronous API call
        service.getFestivals(BuildConfig.BUSAN_FESTIVAL_KEY, 10, 1, "json").enqueue(object :
            Callback<FestivalResponse> {
            override fun onResponse(call: Call<FestivalResponse>, response: Response<FestivalResponse>) {
                if (response.isSuccessful) {
                    Log.e(TAG,response.message())
                    Toast.makeText(requireContext(), "${response.message()}",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
                // TODO: Handle the failure case
                Log.e(TAG,t.message.toString())

            }
        })
    }


}