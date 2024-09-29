package com.kwonminseok.newbusanpartners.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwonminseok.newbusanpartners.data.TourismItem
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



