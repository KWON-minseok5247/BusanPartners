package com.kwonminseok.newbusanpartners.ui.home


import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kwonminseok.newbusanpartners.api.TourismAllInOneApiService
import com.kwonminseok.newbusanpartners.data.TourismItem

class TourismPagingSource(
    private val tourismApiService: TourismAllInOneApiService,
    private val longitude: Double,
    private val latitude: Double,
    private val contentTypeId: Int
) : PagingSource<Int, TourismItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TourismItem> {
        val page = params.key ?: 1
        val call = tourismApiService.locationBasedList1(
            numOfRows = params.loadSize,
            pageNo = page,
            mapX = longitude,
            mapY = latitude,
            radius = 20000,
            contentTypeId = contentTypeId
        )

        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val items = response.body()?.response?.body?.items?.item ?: emptyList()
                val filteredItems = items.filter { it.firstimage.isNotEmpty() }
                Log.e("filteredItems", filteredItems.toString())
                LoadResult.Page(
                    data = filteredItems,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (filteredItems.isEmpty()) null else page + 1
                )

            } else {
                LoadResult.Error(Exception("API call failed with response code ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("TourismPagingSource", "Error loading data", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TourismItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
