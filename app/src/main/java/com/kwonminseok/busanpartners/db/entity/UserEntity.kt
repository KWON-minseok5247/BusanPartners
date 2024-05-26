package com.kwonminseok.busanpartners.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kwonminseok.busanpartners.data.AuthenticationInformation
import kotlinx.android.parcel.RawValue
import java.util.*


//@Entity(tableName = "user")
//data class UserEntity(
//    @PrimaryKey
//    var id: String,
//    var name: String?,
//    var description: String?,
//    var photo: String?,
//    var answerCount: Int,
//    var followerCount: Int,
//    var followingCount: Int,
//    var isFollowing: Boolean,
//    var updatedAt: Date?,
//
//    )

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val uid: String = "",
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String,
    val imagePath: String = "",
    val gender: String? = null,
    val college: String? = null,
    val introduction: String? = null,
    val name: String? = "",
    val authentication: AuthenticationInformation = AuthenticationInformation(),
    val universityEmail: String = "",
    // tokenTime이 있어야 관광객들의 경우 7일의 텀을 주고 메시지를 더 이상 못하게 막을 수 있다.
    val tokenTime: String? = "2000-01-01T12:38:11.818609+09:00",
    val chipGroup: List<String>? = null,
    val major: String? = "", // 변경된 필드
    val wantToMeet: Boolean = false
    )