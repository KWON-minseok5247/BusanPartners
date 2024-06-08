package com.kwonminseok.busanpartners.ui.home

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.data.TourismItem
import kotlinx.coroutines.flow.Flow

class TourismRepository(private val tourismApiService: TourismAllInOneApiService) {

    fun getTourismPagingData(longitude: Double, latitude: Double, contentTypeId: Int): Flow<PagingData<TourismItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TourismPagingSource(tourismApiService, longitude, latitude, contentTypeId) }
        ).flow
    }
}