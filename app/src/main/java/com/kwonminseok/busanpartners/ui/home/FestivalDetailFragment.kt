package com.kwonminseok.busanpartners.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.kelineyt.adapter.makeIt.StudentCardAdapter
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.adapter.FestivalImageAdapter
import com.kwonminseok.busanpartners.adapter.ImagePlaceAdapter
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.api.TourismApiService
import com.kwonminseok.busanpartners.data.CommonResponse
import com.kwonminseok.busanpartners.data.ImageResponse
import com.kwonminseok.busanpartners.data.IntroResponse
import com.kwonminseok.busanpartners.data.TranslatedText
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentFestivalDetailBinding
import com.kwonminseok.busanpartners.databinding.FragmentSelectedUniversityStudentListBinding
import com.kwonminseok.busanpartners.ui.login.SplashActivity
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
class FestivalDetailFragment : Fragment() {
    private var _binding: FragmentFestivalDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var tourismApiService: TourismAllInOneApiService
    private lateinit var indicator: CircleIndicator3
    private lateinit var viewPager: ViewPager2
    private lateinit var imageEventAdapter: ImagePlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFestivalDetailBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allLayout.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE

        binding.festivalImageLoading.startShimmer()

        val contentId = arguments?.getString("contentId") ?: return
        tourismApiService = TourismAllInOneApiService.getInstance()

        viewPager = binding.eventViewPager // viewPager 초기화
        indicator = binding.indicator // indicator 초기화

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
        hideBottomNavigationView()
    }

    override fun onPause() {
        // ChatFragment가 다른 Fragment로 대체되거나 화면에서 사라질 때
        showBottomNavigationView()
        super.onPause()
    }

    private suspend fun fetchIntroData(contentId: Int) {
        tourismApiService.detailIntro1(
            numOfRows = 1,
            pageNo = 1,
            contentTypeId = LanguageUtils.getContentIdForFestival(requireContext()), // 적절한 contentTypeId 입력
            contentId = contentId
        ).enqueue(object : Callback<IntroResponse> {
            override fun onResponse(call: Call<IntroResponse>, response: Response<IntroResponse>) {
                if (response.isSuccessful) {
                    val introItem = response.body()?.response?.body?.items?.item?.firstOrNull()
                    introItem?.let {
//                        binding.textViewEventPlace.text = "Location: ${it.eventplace}"
//                        binding.textViewPlayTime.text = "Start Time: ${Html.fromHtml(it.playtime, Html.FROM_HTML_MODE_LEGACY)}"
////                        binding.textViewSpendTimeFestival.text = "Duration: ${it.spendtimefestival} minutes"
//                        binding.textViewUseTimeFestival.text = "Usage Fee: ${Html.fromHtml(it.usetimefestival, Html.FROM_HTML_MODE_LEGACY)}"
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
            contentTypeId = LanguageUtils.getContentIdForFestival(requireContext()), // 적절한 contentTypeId 입력
            contentId = contentId
        ).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    val commonItem = response.body()?.response?.body?.items?.item?.firstOrNull()
                    binding.festivalImageLoading.stopShimmer()
                    binding.festivalImageLoading.visibility = View.GONE
                    binding.eventViewPager.visibility = View.VISIBLE

                    commonItem?.let {commonItem ->
                        val eventStartDate = arguments?.getString("eventstartdate")
                        val eventEndDate = arguments?.getString("eventenddate") ?: return

                        binding.textViewSpendTimeFestival.text = "${eventStartDate} - ${eventEndDate}"

                        binding.textViewTitle.text = "${commonItem.title}"
                        binding.textViewEventPlace.text = "${commonItem.addr1}"
                        binding.textViewHomepage.apply {
                            text = Html.fromHtml(commonItem.homepage, Html.FROM_HTML_MODE_LEGACY)
                            movementMethod = LinkMovementMethod.getInstance()
                        }
                        binding.textviewMapButton.setOnClickListener {
                            Log.e("commonItem.mapx", "${commonItem.mapx} ${commonItem.mapy}")
                            val intent = Intent(context, AttachmentMapActivity::class.java).apply {
                                putExtra("longitude", commonItem.mapx.toDouble())
                                putExtra("latitude", commonItem.mapy.toDouble())
                            }
                            context?.startActivity(intent)
                        }
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                            textView.text =
//                        } else {
//                            textView.text = Html.fromHtml(htmlString)
//                        }
                        binding.allLayout.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.INVISIBLE

                        binding.textViewOverview.text = commonItem.overview
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
//                    binding.eventViewPager.adapter = adapter
                    imageEventAdapter = ImagePlaceAdapter()
                    viewPager.adapter = imageEventAdapter


                    imageEventAdapter.submitList(images)
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