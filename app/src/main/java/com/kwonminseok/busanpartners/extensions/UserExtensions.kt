package com.kwonminseok.busanpartners.extensions

import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.db.entity.UserEntity

fun User.toEntity(): UserEntity = UserEntity(
    uid = this.uid,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    imagePath = this.imagePath,
    gender = this.gender,
    college = this.college,
    introduction = this.introduction,
    name = this.name,
    authentication = this.authentication,
    universityEmail = this.universityEmail,
    tokenTime = this.tokenTime,
    chipGroup = this.chipGroup,
    major = this.major,
    wantToMeet = this.wantToMeet
)

// UserEntityExtensions.kt
fun UserEntity.toUser(): User = User(
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    imagePath = this.imagePath,
    uid = this.uid,
    gender = this.gender,
    college = this.college,
    introduction = this.introduction,
    name = this.name,
    authentication = this.authentication,
    universityEmail = this.universityEmail,
    tokenTime = this.tokenTime,
    chipGroup = this.chipGroup,
    major = this.major,
    wantToMeet = this.wantToMeet
)

//fun UserEntity.toUser(): User {
//    // major 필드를 Map<String, Any?> 타입으로 변환
//    val majorMap: Map<String, Any?> = if (this.major != null) {
//        mapOf("KO" to this.major)
//    } else {
//        emptyMap()
//    }
//
//    return User(
//        firstName = this.firstName,
//        lastName = this.lastName,
//        email = this.email,
//        imagePath = this.imagePath,
//        uid = this.uid,
//        gender = this.gender,
//        college = this.college,
//        introduction = this.introduction,
//        name = this.name,
//        authentication = this.authentication,
//        universityEmail = this.universityEmail,
//        tokenTime = this.tokenTime,
//        chipGroup = this.chipGroup,
//        major = majorMap,  // 변환된 Map을 설정
//        wantToMeet = this.wantToMeet
//    )
//}

