package com.kwonminseok.busanpartners.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelineyt.adapter.makeIt.StudentCardAdapter
import com.kwonminseok.busanpartners.adapter.FestivalImageAdapter
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.data.CommonResponse
import com.kwonminseok.busanpartners.data.ImageResponse
import com.kwonminseok.busanpartners.data.IntroResponse
import com.kwonminseok.busanpartners.databinding.FragmentFestivalDetailBinding
import com.kwonminseok.busanpartners.databinding.FragmentTourismPlaceDetailBinding
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class TourismPlaceDetailFragment : Fragment() {
    private var _binding: FragmentTourismPlaceDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTourismPlaceDetailBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentId = arguments?.getString("contentId") ?: return
        fetchIntroData(contentId.toInt())
        fetchCommonData(contentId.toInt())
        fetchImageData(contentId.toInt())





    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }

    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        showBottomNavigationView()
        super.onPause()
    }

    private fun fetchIntroData(contentId: Int) {
        TourismApiService.getInstance().korDetailIntro1(
            numOfRows = 1,
            pageNo = 1,
            contentTypeId = 12, // 적절한 contentTypeId 입력
            contentId = contentId
        ).enqueue(object : Callback<IntroResponse> {
            override fun onResponse(call: Call<IntroResponse>, response: Response<IntroResponse>) {
                if (response.isSuccessful) {
                    val introItem = response.body()?.response?.body?.items?.item?.firstOrNull()
                    introItem?.let {
                        binding.textViewUserTime.text = "play Time: ${it.playtime}"
                        binding.textViewRestDate.text = "Duration: ${it.restdate}"
                    }
                } else {
                    Log.e("FestivalDetail", "Intro Response failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<IntroResponse>, t: Throwable) {
                Log.e("FestivalDetail", t.message.toString())
            }
        })
    }

    private fun fetchCommonData(contentId: Int) {
        TourismApiService.getInstance().korDetailCommon1(
            numOfRows = 1,
            pageNo = 1,
            contentTypeId = 12, // 적절한 contentTypeId 입력
            contentId = contentId
        ).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    val commonItem = response.body()?.response?.body?.items?.item?.firstOrNull()
                    commonItem?.let {
                        binding.textViewEventPlace.text = "Location: ${it.addr1}"
                        binding.textViewTitle.text = "${it.title} ${it.mapx} ${it.mapy}"
                        binding.textViewOverview.text = it.overview
                    }
                } else {
                    Log.e("FestivalDetail", "Common Response failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Log.e("FestivalDetail", t.message.toString())
            }
        })
    }

    private fun fetchImageData(contentId: Int) {
        TourismApiService.getInstance().korDetailImage1(
            numOfRows = 10,
            pageNo = 1,
            contentId = contentId
        ).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    val imageItems = response.body()?.response?.body?.items?.item ?: emptyList()
                    Log.e("ImageItems", imageItems.toString()) // JSON 응답을 로그로 출력
                    val images = imageItems.mapNotNull { it.originimgurl } // null 값을 제외
                    Log.e("images", images.toString())
                    // 이미지 목록을 RecyclerView에 표시하는 코드
                    val adapter = FestivalImageAdapter()
                    binding.recyclerViewImages.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) // LayoutManager 설정
                    binding.recyclerViewImages.adapter = adapter
                    adapter.submitList(images)
                } else {
                    Log.e("FestivalDetail", "Image Response failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.e("FestivalDetail", t.message.toString())
            }
        })
    }


}