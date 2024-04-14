package com.kwonminseok.busanpartners.mainScreen.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.adapter.FestivalAdapter
import com.kwonminseok.busanpartners.adapter.TouristDestinationAdapter
import com.kwonminseok.busanpartners.api.WorldTimeApiService
import com.kwonminseok.busanpartners.api.WorldTimeResponse
import com.kwonminseok.busanpartners.data.FestivalResponse
import com.kwonminseok.busanpartners.data.TouristDestinationResponse
import com.kwonminseok.busanpartners.databinding.FragmentHomeBinding
import com.kwonminseok.busanpartners.repository.TimeRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG = "HomeFragment"
class HomeFragment : Fragment() {

    private val touristDestinationAdapter by lazy { TouristDestinationAdapter() }
    private val festivalAdapter by lazy { FestivalAdapter() }
    lateinit var binding: FragmentHomeBinding
    lateinit var time: WorldTimeResponse
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


        lifecycleScope.launch {
            TimeRepository.fetchCurrentTime()
        }


        // festival 정보 가져오는 함수
        getFestivalInformation()


        BusanFestivalApiService.getInstance().getTouristDestination(BuildConfig.BUSAN_FESTIVAL_KEY, 10, 1, "json").enqueue(object :
            Callback<TouristDestinationResponse> {
            override fun onResponse(call: Call<TouristDestinationResponse>,
                                    response: Response<TouristDestinationResponse>) {
                if (response.isSuccessful) {
                    binding.touristRecyclerView.adapter = touristDestinationAdapter
                    touristDestinationAdapter.differ.submitList(response.body()?.getAttractionKr?.item)
                }
            }
            override fun onFailure(call: Call<TouristDestinationResponse>, t: Throwable) {
                Log.e(TAG,t.message.toString())

            }
        })



    }

    private fun getFestivalInformation() {
        BusanFestivalApiService.getInstance()
            .getFestivalsKr(BuildConfig.BUSAN_FESTIVAL_KEY, 10, 1, "json").enqueue(object :
                Callback<FestivalResponse> {
                override fun onResponse(
                    call: Call<FestivalResponse>,
                    response: Response<FestivalResponse>
                ) {
                    if (response.isSuccessful) {

                        binding.festivalViewPager.adapter = festivalAdapter

                        festivalAdapter.differ.submitList(response.body()?.getFestivalKr?.item)

                    }
                }

                override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
                    Log.e(TAG, t.message.toString())

                }
            })
    }


}