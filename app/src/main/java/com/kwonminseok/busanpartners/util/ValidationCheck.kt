package com.kwonminseok.busanpartners.util

import android.util.Patterns

// 파일로 따로 만들어서 쓰는 이유는 이따가 로그인 프래그먼트에서도 사용할 예정이기 때문에 중복해서 작성하기 귀찮아서..
fun validateEmail(email: String): RegisterValidation {
    if (email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong email format")
    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empty")

    if (password.length < 6)
        return RegisterValidation.Failed("Password should contains 6 char")

    return RegisterValidation.Success
}


