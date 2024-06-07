package com.kwonminseok.busanpartners.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.data.TourismItem
import kotlinx.coroutines.flow.Flow

class TourismViewModel(
    private val apiService: TourismAllInOneApiService,
    private val longitude: Double,
    private val latitude: Double
) : ViewModel() {

    val tourismPagingData: Flow<PagingData<TourismItem>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { TourismPagingSource(apiService, longitude, latitude) }
    ).flow.cachedIn(viewModelScope)
}
