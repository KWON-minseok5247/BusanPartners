package com.kwonminseok.busanpartners.data

import com.kwonminseok.busanpartners.R
import com.naver.maps.geometry.LatLng

object Universities {
    val universityInfoList = listOf(
        UniversityInfo(
            name = "국립부경대학교",
            location = LatLng(35.1335411, 129.1059852),
            logoResourceId = R.drawable.pukyong_logo // drawable에 저장된 로고 리소스
        ),
        // 다른 대학교 정보 추가...
        UniversityInfo(
            name = "부산교육대학교",
            location = LatLng(35.1964809, 129.0741424),
            logoResourceId = R.drawable.pusan_education_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "부산대학교",
            location = LatLng(35.2333739, 129.0798495),
            logoResourceId = R.drawable.pusan_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "한국방송통신대학교",
            location = LatLng(35.2240908, 129.0064649),
            logoResourceId = R.drawable.korea_communication // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "국립한국해양대학교",
            location = LatLng(35.076359, 129.0892064),
            logoResourceId = R.drawable.sea_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "경성대학교",
            location = LatLng(35.1422464, 129.0969305),
            logoResourceId = R.drawable.kyongsung_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "고신대학교",
            location = LatLng(35.0789683, 129.0631343),
            logoResourceId = R.drawable.kosin_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "동명대학교",
            location = LatLng(35.1220638, 129.1016726),
            logoResourceId = R.drawable.tongmyong_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "동서대학교",
            location = LatLng(35.1449836, 129.0084605),
            logoResourceId = R.drawable.dsu_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "동아대학교",
            location = LatLng(35.1161319, 128.9675199),
            logoResourceId = R.drawable.donga_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "동의대학교",
            location = LatLng(35.1418247, 129.0346167),
            logoResourceId = R.drawable.dongeui_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "부산가톨릭대학교",
            location = LatLng(35.2447053, 129.0975521),
            logoResourceId = R.drawable.catholic_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "부산외국어대학교",
            location = LatLng(35.2670447, 129.0790562),
            logoResourceId = R.drawable.bufs_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "신라대학교",
            location = LatLng(35.1682795, 128.9977094),
            logoResourceId = R.drawable.silla_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "영산대학교",
            location = LatLng(35.2239352, 129.1574846),
            logoResourceId = R.drawable.youngsan_logo // drawable에 저장된 로고 리소스
        ),
        UniversityInfo(
            name = "인제대학교",
            location = LatLng(35.2487276, 128.9026734),
            logoResourceId = R.drawable.inje_logo // drawable에 저장된 로고 리소스
        ),
    )

    // 대학교 이름으로 정보 검색
    fun getInfoByName(name: String): UniversityInfo? =
        universityInfoList.find { it.name == name }
}


data class UniversityInfo(
    val name: String,
    val location: LatLng,
    val logoResourceId: Int
)
