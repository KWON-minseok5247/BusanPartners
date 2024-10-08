package com.kwonminseok.newbusanpartners.repository

import android.util.Log
import com.kwonminseok.newbusanpartners.api.WorldTimeApiService
import com.kwonminseok.newbusanpartners.api.WorldTimeResponse

//object TimeRepository {
//    private var _currentTime: WorldTimeResponse? = null
//    val currentTime: WorldTimeResponse?
//        get() = _currentTime
//
//    suspend fun fetchCurrentTime() {
//        _currentTime = WorldTimeApiService.getInstance().getSeoulTime()
//    }
//}

object TimeRepository {
    private var _currentTime: WorldTimeResponse? = null
    val currentTime: WorldTimeResponse?
        get() = _currentTime

    suspend fun fetchCurrentTime() {
        try {
            _currentTime = WorldTimeApiService.getInstance().getSeoulTime()
        } catch (e: Exception) {
            Log.e("TimeRepository", "시간 정보 가져오기 실패", e)
            throw e
        }
    }
}
