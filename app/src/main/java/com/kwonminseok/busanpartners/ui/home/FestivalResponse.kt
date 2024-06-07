package com.kwonminseok.busanpartners.ui.home

//data class FestivalResponse(
//    val getFestivalKr: FestivalContent
//)
//
//data class FestivalContent(
//    val item: List<FestivalItem>
//)
//
//data class FestivalItem(
//    val MAIN_TITLE: String,
//    val LAT: String,
//    val LNG: String,
//    val PLACE: String,
//    val TITLE: String,
//    val SUBTITLE: String,
//    val CNTCT_TEL: String,
//    val HOMEPAGE_URL: String,
//    val TRFC_INFO: String,
//    val USAGE_DAY_WEEK_AND_TIME: String,
//    val USAGE_DAY: String,
//    val MAIN_IMG_NORMAL: String,
//    val MAIN_PLACE: String,
//    val MAIN_IMG_THUMB: String,
//    val ITEMCNTNTS: String,
//    val MIDDLE_SIZE_RM1: String,
//    val ADDR1: String,
//    )
data class FestivalResponse(
    val response: ResponseBody
)

data class ResponseBody(
    val header: Header,
    val body: Body
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Body(
    val items: Items,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class Items(
    val item: List<Festival>
)

data class Festival(
    val title: String,
    val addr1: String,
    val firstimage: String,
    val firstimage2: String,
    val mapx: Double,
    val mapy: Double,
    val tel: String,
    val eventstartdate: String,
    val eventenddate: String,
    val contentid: String,

)
