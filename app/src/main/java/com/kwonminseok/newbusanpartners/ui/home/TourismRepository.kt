package com.kwonminseok.newbusanpartners.ui.home

import com.kwonminseok.newbusanpartners.api.TourismAllInOneApiService
import com.kwonminseok.newbusanpartners.data.TourismItem

//Paging3 용
//class TourismRepository(private val tourismApiService: TourismAllInOneApiService) {
//
//    fun getTourismPagingData(longitude: Double, latitude: Double, contentTypeId: Int): Flow<PagingData<TourismItem>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 10,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { TourismPagingSource(tourismApiService, longitude, latitude, contentTypeId) }
//        ).flow
//    }
//}

class TourismRepository(private val tourismApiService: TourismAllInOneApiService) {

    suspend fun getTourismData(longitude: Double, latitude: Double, contentTypeId: Int): List<TourismItem> {
        val response = tourismApiService.locationBasedList1(
            numOfRows = 18,
            pageNo = 1,
            mapX = longitude,
            mapY = latitude,
            radius = 20000,
            contentTypeId = contentTypeId
        ).execute()
        return response.body()?.response?.body?.items?.item ?: emptyList()
    }

    suspend fun getFestivalData(startDate: String, endDate: String): List<Festival> {
        val response = tourismApiService.searchFestival1(
            numOfRows = 10,
            pageNo = 1,
            eventStartDate = startDate,
            eventEndDate = endDate
        ).execute()
        return response.body()?.response?.body?.items?.item ?: emptyList()
    }
}
