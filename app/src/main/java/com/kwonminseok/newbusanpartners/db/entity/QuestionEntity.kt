package com.kwonminseok.newbusanpartners.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*


@Entity(tableName="question")
data class QuestionEntity (
    @PrimaryKey
    val id: LocalDate,
    val text: String,
    val answerCount: Int,
    val updatedAt: Date,
    val createdAt: Date
        )