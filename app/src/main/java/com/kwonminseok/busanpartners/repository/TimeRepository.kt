package com.kwonminseok.busanpartners.repository

import com.kwonminseok.busanpartners.api.WorldTimeApiService
import com.kwonminseok.busanpartners.api.WorldTimeResponse

object TimeRepository {
    private var _currentTime: WorldTimeResponse? = null
    val currentTime: WorldTimeResponse?
        get() = _currentTime

    suspend fun fetchCurrentTime() {
        _currentTime = WorldTimeApiService.getInstance().getSeoulTime()
    }
}