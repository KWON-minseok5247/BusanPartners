package com.kwonminseok.busanpartners.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService
import com.kwonminseok.busanpartners.data.TourismItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TourismViewModel(private val repository: TourismRepository) : ViewModel() {

    fun fetchTourismPagingData(longitude: Double, latitude: Double, contentTypeId: Int): Flow<PagingData<TourismItem>> {
        return repository.getTourismPagingData(longitude, latitude, contentTypeId)
            .cachedIn(viewModelScope)
    }


}




