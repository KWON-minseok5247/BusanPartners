package com.kwonminseok.newbusanpartners.data


//data class TourismResponse(
//    val response: ResponseBody // Root object as "response"
//)
//
//data class ResponseBody(
//    val header: ResponseHeader, // Includes resultCode and resultMsg
//    val body: BodyContent // Container for the items array and pagination details
//)
//
//data class ResponseHeader(
//    val resultCode: String,
//    val resultMsg: String
//)
//
//data class BodyContent(
//    val items: List<TourismItem>,
//    val numOfRows: Int,
//    val pageNo: Int,
//    val totalCount: Int
//)
//
//data class TourismItem(
//    val contentid: String,
//    val title: String,
//    val addr1: String,
//    val firstImage: String?, // Optional field for the image URL
//    val mapx: Double,
//    val mapy: Double,
//    val cat1: String, // Additional fields like category codes if necessary
//    val cat2: String,
//    val cat3: String
//)

//data class TourismResponse(
//    val response: TourismContent
//)
//
//data class TourismContent(
//    val item: List<TourismItem>
//)
//
//
//data class TourismItem(
//    val contentid: String,
//    val addr2: String,
//    val firstimage2: String,
//    val cpyrhtDivCd: String,
//    val addr1: String,
//    val contenttypeid: String,
//    val createdtime: String,
//    val dist: String,
//    val firstimage: String,
//    val areacode: String,
//    val booktour: String,
//    val mapx: String,
//    val mapy: String,
//    val mlevel: String,
//    val modifiedtime: String,
//    val sigungucode: String,
//    val tel: String,
//    val title: String,
//    val cat1: String,
//    val cat2: String,
//    val cat3: String
//)

data class TourismResponse(
    val response: ResponseBody
)

data class ResponseBody(
    val header: ResponseHeader,
    val body: BodyContent
)

data class ResponseHeader(
    val resultCode: String,
    val resultMsg: String
)

data class BodyContent(
    val items: ItemsWrapper, // 이전에는 바로 List<TourismItem>이었지만, 실제 JSON 구조에는 items 객체가 한 단계 더 있음
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class ItemsWrapper(
    val item: List<TourismItem> // item 배열
)

data class TourismItem(
    val addr1: String,
    val addr2: String,
    val dist: String,
    val firstimage: String,
    val firstimage2: String,
    val mapx: String,
    val mapy: String,
    val modifiedtime: String,
    val tel: String,
    val title: String,
    val contentid: String
)