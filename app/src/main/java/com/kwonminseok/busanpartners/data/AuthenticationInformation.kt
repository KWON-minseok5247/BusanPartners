package com.kwonminseok.busanpartners.data

data class AuthenticationInformation(
    val studentIdentificationCard: MutableList<String>? = null,
    val travelerAuthenticationImage: MutableList<String>? = null,
    val collegeStudent: Boolean = false,
    val traveler: Boolean = false,
    val studentEmailAuthenticationComplete: Boolean = false,
    //authenticationStatus에는 loading, complete, failed, ""로 구성
    val authenticationStatus: String = ""

    )
{
    constructor(): this(null,null, false, false, false)
}

//data class AuthenticationInformation(
//    @get:PropertyName("studentIdentificationCard") @set:PropertyName("studentIdentificationCard") var studentIdentificationCard: MutableList<String>? = null,
//    @get:PropertyName("travelerAuthenticationImage") @set:PropertyName("travelerAuthenticationImage") var travelerAuthenticationImage: MutableList<String>? = null,
//    @get:PropertyName("isCollegeStudent") @set:PropertyName("isCollegeStudent") var isCollegeStudent: Boolean = false,
//    @get:PropertyName("isTraveler") @set:PropertyName("isTraveler") var isTraveler: Boolean = false,
//    @get:PropertyName("isStudentEmailAuthenticationComplete") @set:PropertyName("isStudentEmailAuthenticationComplete") var isStudentEmailAuthenticationComplete: Boolean = false
//) {
//    constructor() : this(null, null, false, false, false)
//}
