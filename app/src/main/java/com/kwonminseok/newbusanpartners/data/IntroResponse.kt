package com.kwonminseok.newbusanpartners.data

data class IntroResponse(
    val response: IntroResponseBody
)

data class IntroResponseBody(
    val header: IntroResponseHeader,
    val body: IntroBodyContent
)

data class IntroResponseHeader(
    val resultCode: String,
    val resultMsg: String
)

data class IntroBodyContent(
    val items: IntroItemsWrapper,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class IntroItemsWrapper(
    val item: List<IntroItem>
)

data class IntroItem(
    val eventplace: String,
    val playtime: String,
    val spendtimefestival: String,
    val usetimefestival: String,
    val usertime: String,
    val restdate: String

)
