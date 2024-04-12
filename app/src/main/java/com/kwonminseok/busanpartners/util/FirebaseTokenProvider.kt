package com.kwonminseok.busanpartners.util

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.kwonminseok.busanpartners.BusanPartners
import com.kwonminseok.busanpartners.mainScreen.TAG
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface TokenProvider {
    fun loadToken(): String
}

//class FirebaseTokenProvider(private val functions: FirebaseFunctions) : TokenProvider {
//    override fun loadToken(): String {
//
//        suspendCoroutine { continuation ->
//            val functions = FirebaseFunctions.getInstance("asia-northeast3")
//            functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
//                .call()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        // 함수 호출이 성공했습니다. 반환된 데이터에서 토큰을 추출합니다.
//                        val token = task.result?.data as String
//                        BusanPartners.preferences.setString("token", token)
//                        continuation.resume(token) // 코루틴을 재개하고 결과를 반환합니다.
//                    } else {
//                        // 호출 실패. 에러를 처리합니다.
//                        Log.e(TAG, "토큰 호출을 실패했습니다.")
//                        continuation.resumeWithException(
//                            task.exception ?: RuntimeException("Unknown token fetch error")
//                        )
//                    }
//                }
//        }
//
//        val getStreamUserToken = functions.getHttpsCallable("ext-auth-chat-getStreamUserToken")
//        // 여기서 필요한 경우 사용자 UID 등의 데이터를 파라미터로 전달할 수 있습니다.
//        // 예를 들어, val data = hashMapOf("uid" to userId)와 같이 사용자 UID를 전달할 수 있습니다.
//
//        return getStreamUserToken.call().continueWith { task ->
//            if (!task.isSuccessful) {
//                throw task.exception ?: IllegalStateException("Failed to get Stream Chat token")
//            }
//            task.result?.data as String
//        }.result // 동기적으로 토큰을 가져오기 위해 get()을 호출합니다. 이 부분을 비동기로 처리하려면 적절한 방법으로 수정해야 합니다.
//    }
//}
