package com.kwonminseok.busanpartners.ui.home


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.data.TourismItem
import com.kwonminseok.busanpartners.util.LanguageUtils

class TourismPagingSource(
    private val apiService: TourismAllInOneApiService,
    private val longitude: Double,
    private val latitude: Double
) : PagingSource<Int, TourismItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TourismItem> {
        val page = params.key ?: 1

        return try {
            val response = apiService.locationBasedList1(
                numOfRows = 10,
                pageNo = page,
                mapX = longitude,
                mapY = latitude,
                radius = 20000,
                contentTypeId = 12 // or another valid content type ID
            ).execute()

            if (response.isSuccessful) {
                val tourismItems = response.body()?.response?.body?.items?.item ?: emptyList()
                LoadResult.Page(
                    data = tourismItems,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (tourismItems.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Failed to load data"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TourismItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}