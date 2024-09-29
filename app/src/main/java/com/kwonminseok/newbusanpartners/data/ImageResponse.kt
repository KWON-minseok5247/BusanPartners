package com.kwonminseok.newbusanpartners.data

data class ImageResponse(
    val response: ImageResponseBody
)

data class ImageResponseBody(
    val header: ImageResponseHeader,
    val body: ImageBodyContent
)

data class ImageResponseHeader(
    val resultCode: String,
    val resultMsg: String
)

data class ImageBodyContent(
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
    val items: ImageItemsWrapper
)

data class ImageItemsWrapper(
    val item: List<ImageItem> // 여기서 List로 수정합니다.
)

data class ImageItem(
    val contentid: String,
    val imgname: String,
    val originimgurl: String,
    val serialnum: String,
    val smallimageurl: String,
    val cpyrhtDivCd: String
)
