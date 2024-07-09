package com.kwonminseok.busanpartners.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

// Paging3 용
//class TourismViewModel(private val repository: TourismRepository) : ViewModel() {
//
//    fun fetchTourismPagingData(longitude: Double, latitude: Double, contentTypeId: Int): Flow<PagingData<TourismItem>> {
//        return repository.getTourismPagingData(longitude, latitude, contentTypeId)
//            .cachedIn(viewModelScope)
//    }
//
//
//}

class TourismViewModel(private val repository: TourismRepository) : ViewModel() {

    private val _tourismData = MutableLiveData<List<TourismItem>>()
    val tourismData: LiveData<List<TourismItem>> get() = _tourismData

    private val _festivalData = MutableLiveData<List<Festival>>()
    val festivalData: LiveData<List<Festival>> get() = _festivalData

    fun fetchTourismData(longitude: Double, latitude: Double, contentTypeId: Int) {
        viewModelScope.launch {
            val response = repository.getTourismData(longitude, latitude, contentTypeId)
            _tourismData.postValue(response)
        }
    }

    fun fetchFestivalData(startDate: String, endDate: String) {
        viewModelScope.launch {
            val response = repository.getFestivalData(startDate, endDate)
            _festivalData.postValue(response)
        }
    }
}



