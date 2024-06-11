package com.kwonminseok.busanpartners.data

import com.kwonminseok.busanpartners.R
import com.naver.maps.geometry.LatLng

object Universities {
//    val universityInfoList = listOf(
//        UniversityInfo(
//            name = "국립부경대학교",
//            location = LatLng(35.1335411, 129.1059852),
//            logoResourceId = R.drawable.pukyong_logo,
//            email = "@pukyong.ac.kr"
//        ),
//        UniversityInfo(
//            name = "부산교육대학교",
//            location = LatLng(35.1964809, 129.0741424),
//            logoResourceId = R.drawable.pusan_education_logo,
//            email = "@bnue.ac.kr"
//        ),
//        UniversityInfo(
//            name = "부산대학교",
//            location = LatLng(35.2333739, 129.0798495),
//            logoResourceId = R.drawable.pusan_logo,
//            email = "@pusan.ac.kr"
//        ),
//        UniversityInfo(
//            name = "한국방송통신대학교",
//            location = LatLng(35.2240908, 129.0064649),
//            logoResourceId = R.drawable.korea_communication,
//            email = "@knou.ac.kr"
//        ),
//        UniversityInfo(
//            name = "국립한국해양대학교",
//            location = LatLng(35.076359, 129.0892064),
//            logoResourceId = R.drawable.sea_logo,
//            email = "@kmou.ac.kr"
//        ),
//        UniversityInfo(
//            name = "경성대학교",
//            location = LatLng(35.1422464, 129.0969305),
//            logoResourceId = R.drawable.kyongsung_logo,
//            email = "@ks.ac.kr"
//        ),
//        UniversityInfo(
//            name = "고신대학교",
//            location = LatLng(35.0789683, 129.0631343),
//            logoResourceId = R.drawable.kosin_logo,
//            email = "@kosin.ac.kr"
//        ),
//        UniversityInfo(
//            name = "동명대학교",
//            location = LatLng(35.1220638, 129.1016726),
//            logoResourceId = R.drawable.tongmyong_logo,
//            email = "@tu.ac.kr"
//        ),
//        UniversityInfo(
//            name = "동서대학교",
//            location = LatLng(35.1449836, 129.0084605),
//            logoResourceId = R.drawable.dsu_logo,
//            email = "@dongseo.ac.kr"
//        ),
//        UniversityInfo(
//            name = "동아대학교",
//            location = LatLng(35.1161319, 128.9675199),
//            logoResourceId = R.drawable.donga_logo,
//            email = "@donga.ac.kr"
//        ),
//        UniversityInfo(
//            name = "동의대학교",
//            location = LatLng(35.1418247, 129.0346167),
//            logoResourceId = R.drawable.dongeui_logo,
//            email = "@deu.ac.kr"
//        ),
//        UniversityInfo(
//            name = "부산가톨릭대학교",
//            location = LatLng(35.2447053, 129.0975521),
//            logoResourceId = R.drawable.catholic_logo,
//            email = "@cup.ac.kr"
//        ),
//        UniversityInfo(
//            name = "부산외국어대학교",
//            location = LatLng(35.2670447, 129.0790562),
//            logoResourceId = R.drawable.bufs_logo,
//            email = "@bufs.ac.kr"
//        ),
//        UniversityInfo(
//            name = "신라대학교",
//            location = LatLng(35.1682795, 128.9977094),
//            logoResourceId = R.drawable.silla_logo,
//            email = "@silla.ac.kr"
//        ),
//        UniversityInfo(
//            name = "영산대학교",
//            location = LatLng(35.2239352, 129.1574846),
//            logoResourceId = R.drawable.youngsan_logo,
//            email = "@ysu.ac.kr"
//        ),
//        UniversityInfo(
//            name = "인제대학교",
//            location = LatLng(35.2487276, 128.9026734),
//            logoResourceId = R.drawable.inje_logo,
//            email = "@inje.ac.kr"
//        )
//    )
val universityInfoList = listOf(
    UniversityInfo(
        nameKo = "국립부경대학교",
        nameEn = "Pukyong National University",
        nameJa = "釜慶大学校",
        nameZh = "国立釜庆大学",
        nameZhTw = "國立釜慶大學",
        location = LatLng(35.1335411, 129.1059852),
        logoResourceId = R.drawable.pukyong_logo,
        email = "@pukyong.ac.kr"
    ),
    UniversityInfo(
        nameKo = "부산교육대학교",
        nameEn = "Busan National University of Education",
        nameJa = "釜山教育大学校",
        nameZh = "釜山教育大学",
        nameZhTw = "釜山教育大學",
        location = LatLng(35.1964809, 129.0741424),
        logoResourceId = R.drawable.pusan_education_logo,
        email = "@bnue.ac.kr"
    ),
    UniversityInfo(
        nameKo = "부산대학교",
        nameEn = "Pusan National University",
        nameJa = "釜山大学校",
        nameZh = "釜山大学",
        nameZhTw = "釜山大學",
        location = LatLng(35.2333739, 129.0798495),
        logoResourceId = R.drawable.pusan_logo,
        email = "@pusan.ac.kr"
    ),
    UniversityInfo(
        nameKo = "한국방송통신대학교",
        nameEn = "Korea National Open University",
        nameJa = "韓国放送通信大学校",
        nameZh = "韩国广播通信大学",
        nameZhTw = "韓國廣播通信大學",
        location = LatLng(35.2240908, 129.0064649),
        logoResourceId = R.drawable.korea_communication,
        email = "@knou.ac.kr"
    ),
    UniversityInfo(
        nameKo = "국립한국해양대학교",
        nameEn = "Korea Maritime and Ocean University",
        nameJa = "韓国海洋大学校",
        nameZh = "韩国海洋大学",
        nameZhTw = "韓國海洋大學",
        location = LatLng(35.076359, 129.0892064),
        logoResourceId = R.drawable.sea_logo,
        email = "@kmou.ac.kr"
    ),
    UniversityInfo(
        nameKo = "경성대학교",
        nameEn = "Kyungsung University",
        nameJa = "京成大学校",
        nameZh = "庆星大学",
        nameZhTw = "慶星大學",
        location = LatLng(35.1422464, 129.0969305),
        logoResourceId = R.drawable.kyongsung_logo,
        email = "@ks.ac.kr"
    ),
    UniversityInfo(
        nameKo = "고신대학교",
        nameEn = "Kosin University",
        nameJa = "高神大学校",
        nameZh = "高神大学",
        nameZhTw = "高神大學",
        location = LatLng(35.0789683, 129.0631343),
        logoResourceId = R.drawable.kosin_logo,
        email = "@kosin.ac.kr"
    ),
    UniversityInfo(
        nameKo = "동명대학교",
        nameEn = "Tongmyong University",
        nameJa = "東明大学校",
        nameZh = "东明大学",
        nameZhTw = "東明大學",
        location = LatLng(35.1220638, 129.1016726),
        logoResourceId = R.drawable.tongmyong_logo,
        email = "@tu.ac.kr"
    ),
    UniversityInfo(
        nameKo = "동서대학교",
        nameEn = "Dongseo University",
        nameJa = "東西大学校",
        nameZh = "东西大学",
        nameZhTw = "東西大學",
        location = LatLng(35.1449836, 129.0084605),
        logoResourceId = R.drawable.dsu_logo,
        email = "@dongseo.ac.kr"
    ),
    UniversityInfo(
        nameKo = "동아대학교",
        nameEn = "Dong-A University",
        nameJa = "東亞大学校",
        nameZh = "东亚大学",
        nameZhTw = "東亞大學",
        location = LatLng(35.1161319, 128.9675199),
        logoResourceId = R.drawable.donga_logo,
        email = "@donga.ac.kr"
    ),
    UniversityInfo(
        nameKo = "동의대학교",
        nameEn = "Dong-Eui University",
        nameJa = "東義大学校",
        nameZh = "东义大学",
        nameZhTw = "東義大學",
        location = LatLng(35.1418247, 129.0346167),
        logoResourceId = R.drawable.dongeui_logo,
        email = "@deu.ac.kr"
    ),
    UniversityInfo(
        nameKo = "부산가톨릭대학교",
        nameEn = "Catholic University of Pusan",
        nameJa = "釜山カトリック大学校",
        nameZh = "釜山天主教大学",
        nameZhTw = "釜山天主教大學",
        location = LatLng(35.2447053, 129.0975521),
        logoResourceId = R.drawable.catholic_logo,
        email = "@cup.ac.kr"
    ),
    UniversityInfo(
        nameKo = "부산외국어대학교",
        nameEn = "Busan University of Foreign Studies",
        nameJa = "釜山外国語大学校",
        nameZh = "釜山外国语大学",
        nameZhTw = "釜山外國語大學",
        location = LatLng(35.2670447, 129.0790562),
        logoResourceId = R.drawable.bufs_logo,
        email = "@bufs.ac.kr"
    ),
    UniversityInfo(
        nameKo = "신라대학교",
        nameEn = "Silla University",
        nameJa = "新羅大学校",
        nameZh = "新罗大学",
        nameZhTw = "新羅大學",
        location = LatLng(35.1682795, 128.9977094),
        logoResourceId = R.drawable.silla_logo,
        email = "@silla.ac.kr"
    ),
    UniversityInfo(
        nameKo = "영산대학교",
        nameEn = "Youngsan University",
        nameJa = "霊山大学校",
        nameZh = "岭南大学",
        nameZhTw = "嶺南大學",
        location = LatLng(35.2239352, 129.1574846),
        logoResourceId = R.drawable.youngsan_logo,
        email = "@ysu.ac.kr"
    ),
    UniversityInfo(
        nameKo = "인제대학교",
        nameEn = "Inje University",
        nameJa = "仁済大学校",
        nameZh = "仁济大学",
        nameZhTw = "仁濟大學",
        location = LatLng(35.2487276, 128.9026734),
        logoResourceId = R.drawable.inje_logo,
        email = "@inje.ac.kr"
    )
)

    // 대학교 이름으로 정보 검색
    fun getInfoByName(name: String, language: String): UniversityInfo? {
        return when (language) {
            "ko" -> universityInfoList.find { it.nameKo == name }
            "en" -> universityInfoList.find { it.nameEn == name }
            "ja" -> universityInfoList.find { it.nameJa == name }
            "zh" -> universityInfoList.find { it.nameZh == name }
            "zh-TW" -> universityInfoList.find { it.nameZhTw == name }
            else -> universityInfoList.find { it.nameEn == name }
        }
    }

//    // 대학교 이름으로 정보 검색
//    fun getInfoByName(name: String): UniversityInfo? =
//        universityInfoList.find { it.name == name }
}


data class UniversityInfo(
    val nameKo: String,
    val nameEn: String,
    val nameJa: String,
    val nameZh: String,
    val nameZhTw: String,
    val location: LatLng,
    val logoResourceId: Int,
    val email: String
)

