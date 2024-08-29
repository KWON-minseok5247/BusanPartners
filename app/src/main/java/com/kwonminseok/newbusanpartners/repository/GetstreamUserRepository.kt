package com.kwonminseok.newbusanpartners.repository

import io.getstream.chat.android.models.User


interface GetstreamUserRepository {
    fun getCurrentUser(): User?
    fun setCurrentUser(user: User)
    fun clearCurrentUser()
}