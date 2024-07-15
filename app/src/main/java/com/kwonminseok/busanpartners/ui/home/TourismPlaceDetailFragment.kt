package com.kwonminseok.busanpartners.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.kwonminseok.busanpartners.adapter.ImagePlaceAdapter
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.data.CommonResponse
import com.kwonminseok.busanpartners.data.ImageResponse
import com.kwonminseok.busanpartners.databinding.FragmentPlaceBinding
import com.kwonminseok.busanpartners.extensions.setStatusBarTransparent
import com.kwonminseok.busanpartners.extensions.setStatusBarVisible
import com.kwonminseok.busanpartners.ui.message.AttachmentMapActivity
import com.kwonminseok.busanpartners.util.LanguageUtils
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG = "TourismPlaceDetailFragment"

@AndroidEntryPoint
class TourismPlaceDetailFragment : Fragment() {
    private var _binding: FragmentPlaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var tourismApiService: TourismAllInOneApiService

    private lateinit var viewPager: ViewPager2
    private lateinit var imagePlaceAdapter: ImagePlaceAdapter
    private lateinit var indicator: CircleIndicator3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
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

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val contentId = arguments?.getString("contentId") ?: return
        fetchCommonData(contentId.toInt())
        fetchImageData(contentId.toInt())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setStatusBarTransparent()
    }

    override fun onPause() {
        super.onPause()
//        requireActivity().setStatusBarVisible()
    }

    private fun fetchCommonData(contentId: Int, retryCount: Int = 3) {
        tourismApiService.detailCommon1(
            numOfRows = 1,
            pageNo = 1,
            contentTypeId = LanguageUtils.getContentIdForTourPlace(requireContext()),
            contentId = contentId
        ).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    val commonItem = response.body()?.response?.body?.items?.item?.firstOrNull()
                    commonItem?.let {
                        binding.textViewEventPlace.text = HtmlCompat.fromHtml(it.addr1, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        binding.textViewTitle.text = HtmlCompat.fromHtml(it.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        binding.textViewOverview.text = HtmlCompat.fromHtml(it.overview, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

                        binding.textViewEventPlace.setTextIsSelectable(true)
                        binding.textViewTitle.setTextIsSelectable(true)
                        binding.textViewOverview.setTextIsSelectable(true)

                        binding.textviewMapButton.setOnClickListener {
                            val intent = Intent(context, AttachmentMapActivity::class.java).apply {
                                putExtra("longitude", commonItem.mapx.toDouble())
                                putExtra("latitude", commonItem.mapy.toDouble())
                            }
                            context?.startActivity(intent)
                        }

//                        binding.festivalImageLoading.stopShimmer()
//                        binding.festivalImageLoading.visibility = View.GONE
                        binding.placeViewPager.visibility = View.VISIBLE
                        binding.allLayout.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                } else {
                    Log.e("fetchCommonData", "Common Response failed: ${response.errorBody()?.string()}")
                    Log.e("fetchCommonData", "Response code: ${response.code()}, Response message: ${response.message()}")
                    Log.e("fetchCommonData", "Response body: ${response.body()}")
                    handleFailure(call, this, retryCount)

                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Log.e("fetchCommonData", t.message.toString())
                handleFailure(call, this, retryCount)

            }
        })
    }

    private fun fetchImageData(contentId: Int, retryCount: Int = 3) {
        tourismApiService.detailImage1(
            numOfRows = 10,
            pageNo = 1,
            contentId = contentId
        ).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    val imageItems = response.body()?.response?.body?.items?.item ?: emptyList()
                    val images = imageItems.mapNotNull { it.originimgurl }

//                    imagePlaceAdapter = ImagePlaceAdapter()
                    imagePlaceAdapter = ImagePlaceAdapter { position ->
                        val intent = Intent(requireContext(), ImageZoomActivity::class.java).apply {
                            putStringArrayListExtra("images", ArrayList(images))
                            putExtra("position", position)
                        }
                        startActivity(intent)
                    }

                    viewPager.adapter = imagePlaceAdapter
                    imagePlaceAdapter.submitList(images)
                    indicator.setViewPager(viewPager)
                    binding.festivalImageLoading.stopShimmer()
                    binding.festivalImageLoading.visibility = View.GONE

                    viewPager.adapter = imagePlaceAdapter
                    imagePlaceAdapter.submitList(images)
                    indicator.setViewPager(viewPager)
                    binding.festivalImageLoading.stopShimmer()
                    binding.festivalImageLoading.visibility = View.GONE


                } else {
                    Log.e("fetchImageData", "Image Response failed: ${response.errorBody()?.string()}")
                    Log.e("fetchImageData", "Response code: ${response.code()}, Response message: ${response.message()}")
                    Log.e("fetchImageData", "Response body: ${response.body()}")
                    handleFailure(call, this, retryCount)

                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.e("fetchImageData", t.message.toString())
                handleFailure(call, this, retryCount)

            }
        })
    }


    private fun <T> handleFailure(call: Call<T>, callback: Callback<T>, retryCount: Int) {
        if (retryCount > 0) {
            Log.e(TAG, "Retrying... ($retryCount retries left)")
            call.clone().enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        Log.e(TAG, "Failure에서 재시도 성공")

                        callback.onResponse(call, response)
                    } else {
                        Log.e(TAG, "Failure에서 재시도 실패")

                        handleFailure(call, callback, retryCount - 1)
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e(TAG, "Failure에서 응답 자체 실패")

                    handleFailure(call, callback, retryCount - 1)
                }
            })
        } else {
            Log.e(TAG, "Max retries reached. Giving up.")
            _binding?.let { binding ->
                val firstImage = arguments?.getString("firstImage") ?: ""
                val images = listOf(firstImage)

                binding.festivalImageLoading.stopShimmer()
                binding.festivalImageLoading.visibility = View.GONE

                imagePlaceAdapter = ImagePlaceAdapter { position ->
                    val intent = Intent(requireContext(), ImageZoomActivity::class.java).apply {
                        putStringArrayListExtra("images", ArrayList(images))
                        putExtra("position", position)
                    }
                    startActivity(intent)
                }
                viewPager.adapter = imagePlaceAdapter
                imagePlaceAdapter.submitList(images)
                indicator.setViewPager(viewPager)
            }            // Toast.makeText(context, getString(R.string.error_retrieving_data), Toast.LENGTH_SHORT).show()
        }
    }

}