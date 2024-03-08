package com.kwonminseok.busanpartners.util

import com.google.firebase.functions.FirebaseFunctions

interface TokenProvider {
    fun loadToken(): String
}

class FirebaseTokenProvider(private val functions: FirebaseFunctions) : TokenProvider {
    override fun loadToken(): String {
        val getStreamUserToken = functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
        // 여기서 필요한 경우 사용자 UID 등의 데이터를 파라미터로 전달할 수 있습니다.
        // 예를 들어, val data = hashMapOf("uid" to userId)와 같이 사용자 UID를 전달할 수 있습니다.

        return getStreamUserToken.call().continueWith { task ->
            if (!task.isSuccessful) {
                throw task.exception ?: IllegalStateException("Failed to get Stream Chat token")
            }
            task.result?.data as String
        }.result // 동기적으로 토큰을 가져오기 위해 get()을 호출합니다. 이 부분을 비동기로 처리하려면 적절한 방법으로 수정해야 합니다.
    }
}
