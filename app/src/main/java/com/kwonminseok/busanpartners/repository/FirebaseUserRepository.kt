package com.kwonminseok.busanpartners.repository

import com.kwonminseok.busanpartners.data.User

interface FirebaseUserRepository {
    fun getCurrentUser(): User?
    fun setCurrentUser(user: User)
    fun clearCurrentUser()
}