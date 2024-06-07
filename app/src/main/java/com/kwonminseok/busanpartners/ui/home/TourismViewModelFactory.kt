package com.kwonminseok.busanpartners.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kwonminseok.busanpartners.api.TourismAllInOneApiService

class TourismViewModelFactory(
    private val apiService: TourismAllInOneApiService,
    private val longitude: Double,
    private val latitude: Double
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourismViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TourismViewModel(apiService, longitude, latitude) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
