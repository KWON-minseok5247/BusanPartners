package com.kwonminseok.busanpartners.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.kelineyt.adapter.makeIt.StudentCardAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kwonminseok.busanpartners.adapter.FestivalImageAdapter
import com.kwonminseok.busanpartners.adapter.ImagePlaceAdapter
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.data.CommonResponse
import com.kwonminseok.busanpartners.data.ImageResponse
import com.kwonminseok.busanpartners.data.IntroResponse
import com.kwonminseok.busanpartners.databinding.FragmentFestivalDetailBinding
import com.kwonminseok.busanpartners.databinding.FragmentPlaceBinding
import com.kwonminseok.busanpartners.extensions.setStatusBarTransparent
import com.kwonminseok.busanpartners.extensions.setStatusBarVisible
import com.kwonminseok.busanpartners.ui.message.AttachmentMapActivity
import com.kwonminseok.busanpartners.util.LanguageUtils
import com.kwonminseok.busanpartners.util.hideBottomNavigationView
import com.kwonminseok.busanpartners.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class TourismPlaceDetailFragment : Fragment() {
    private var _binding: FragmentPlaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var tourismApiService: TourismAllInOneApiService


    private lateinit var viewPager: ViewPager2
    private lateinit var imagePlaceAdapter: ImagePlaceAdapter
    private lateinit var indicator: CircleIndicator3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allLayout.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE


        binding.festivalImageLoading.startShimmer()
        tourismApiService = TourismAllInOneApiService.getInstance()

        viewPager = binding.placeViewPager // viewPager 초기화
        indicator = binding.indicator // indicator 초기화

        val contentId = arguments?.getString("contentId") ?: return
//        fetchIntroData(contentId.toInt())
        fetchCommonData(contentId.toInt())
        fetchImageData(contentId.toInt())





    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
//        hideBottomNavigationView()
        requireActivity().setStatusBarTransparent()

    }

    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        super.onPause()
//        showBottomNavigationView()
        requireActivity().setStatusBarVisible()

    }

    private suspend fun fetchIntroData(contentId: Int) {
        tourismApiService.detailIntro1(
            numOfRows = 1,
            pageNo = 1,
            contentTypeId = LanguageUtils.getContentIdForTourPlace(requireContext()), // 적절한 contentTypeId 입력
            contentId = contentId
        ).enqueue(object : Callback<IntroResponse> {
            override fun onResponse(call: Call<IntroResponse>, response: Response<IntroResponse>) {
                if (response.isSuccessful) {
                    val introItem = response.body()?.response?.body?.items?.item?.firstOrNull()
                    introItem?.let {
//                        binding.textViewUserTime.text = "play Time: ${it.playtime}"
//                        binding.textViewRestDate.text = "Duration: ${it.restdate}"
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
        tourismApiService.detailCommon1(
            numOfRows = 1,
            pageNo = 1,
            contentTypeId = LanguageUtils.getContentIdForTourPlace(requireContext()), // 적절한 contentTypeId 입력
            contentId = contentId
        ).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    binding.festivalImageLoading.stopShimmer()
                    binding.festivalImageLoading.visibility = View.GONE
                    binding.placeViewPager.visibility = View.VISIBLE

                    val commonItem = response.body()?.response?.body?.items?.item?.firstOrNull()
                    commonItem?.let {
                        binding.textViewEventPlace.text = "${it.addr1}"
                        binding.textViewTitle.text = "${it.title}"
                        binding.textViewOverview.text = it.overview

                        binding.textviewMapButton.setOnClickListener {
                            Log.e("commonItem.mapx", "${commonItem.mapx} ${commonItem.mapy}")
                            val intent = Intent(context, AttachmentMapActivity::class.java).apply {
                                putExtra("longitude", commonItem.mapx.toDouble())
                                putExtra("latitude", commonItem.mapy.toDouble())
                            }
                            context?.startActivity(intent)
                        }

                    }

                    binding.allLayout.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE


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
        tourismApiService.detailImage1(
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
//                    val adapter = FestivalImageAdapter()
                    imagePlaceAdapter = ImagePlaceAdapter()
                    viewPager.adapter = imagePlaceAdapter

//                    binding.viewPager.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) // LayoutManager 설정
                    imagePlaceAdapter.submitList(images)
                    indicator.setViewPager(viewPager) // ViewPager와 Indicator 연결

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