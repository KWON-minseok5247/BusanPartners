package com.kwonminseok.newbusanpartners.util

sealed class RegisterValidation() {
    object Success: RegisterValidation()
    // 실패했을 때에만 메세지가 나오도록.
    data class Failed(val message:String): RegisterValidation()
}

data class RegisterFieldsState(
    val email: RegisterValidation,
    val password: RegisterValidation
)
