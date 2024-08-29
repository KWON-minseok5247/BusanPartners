package com.kwonminseok.newbusanpartners.data

data class TouristDestinationResponse(
    val getAttractionKr: TouristDestinationContent
)

data class TouristDestinationContent(
    val item: List<TouristDestination>
)

data class TouristDestination(
    val MAIN_TITLE: String,
    val LAT: String,
    val LNG: String,
    val PLACE: String,
    val TITLE: String,
    val SUBTITLE: String,
    val CNTCT_TEL: String,
    val HOMEPAGE_URL: String,
    val TRFC_INFO: String,
    val USAGE_DAY_WEEK_AND_TIME: String,
    val MAIN_IMG_NORMAL: String,
    val MAIN_PLACE: String,
    val MAIN_IMG_THUMB: String,
    val ITEMCNTNTS: String,
    val MIDDLE_SIZE_RM1: String,
    val ADDR1: String,
)