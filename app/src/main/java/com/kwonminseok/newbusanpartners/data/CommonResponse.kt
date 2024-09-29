package com.kwonminseok.newbusanpartners.data

data class CommonResponse(
    val response: CommonResponseBody
)

data class CommonResponseBody(
    val header: CommonResponseHeader,
    val body: CommonBodyContent
)

data class CommonResponseHeader(
    val resultCode: String,
    val resultMsg: String
)

data class CommonBodyContent(
    val items: CommonItemsWrapper,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class CommonItemsWrapper(
    val item: List<CommonItem>
)

data class CommonItem(
    val title: String,
    val homepage: String,
    val overview: String,
    val addr1: String,
    val mapx: String,
    val mapy: String,
    )
