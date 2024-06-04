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
    wantToMeet = this.wantToMeet,
    blockList = this.blockList
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
    wantToMeet = this.wantToMeet,
    blockList = this.blockList
)


