package com.kwonminseok.busanpartners.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TimeViewModel : ViewModel() {

    fun fetchSeoulTime() {
        viewModelScope.launch {
            try {
//                val timeResponse = BusanPartners.worldTimeApi.getSeoulTime()
//                Log.e("Seoul Time:", "${timeResponse.datetime}")
                println()
            } catch (e: Exception) {
                println("Error fetching Seoul time: ${e.message}")
            }
        }
    }
}
