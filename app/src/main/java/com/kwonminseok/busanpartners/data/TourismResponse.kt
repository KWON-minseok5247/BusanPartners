package com.kwonminseok.busanpartners.data

data class TourismResponse(
    val getFestivalKr: TourismContent
)

data class TourismContent(
    val item: List<TourismItem>
)


data class TourismItem(
    val contentid: String,
    val addr2: String,
    val firstimage2: String,
    val cpyrhtDivCd: String,
    val addr1: String,
    val contenttypeid: String,
    val createdtime: String,
    val dist: String,
    val firstimage: String,
    val areacode: String,
    val booktour: String,
    val mapx: String,
    val mapy: String,
    val mlevel: String,
    val modifiedtime: String,
    val sigungucode: String,
    val tel: String,
    val title: String,
    val cat1: String,
    val cat2: String,
    val cat3: String
)