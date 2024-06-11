package com.kwonminseok.busanpartners.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.StorageReference
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.data.TranslatedList
import com.kwonminseok.busanpartners.data.TranslatedText
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.util.Constants.STUDENT
import com.kwonminseok.busanpartners.util.Constants.USER_COLLECTION
import com.kwonminseok.busanpartners.util.Resource
import io.getstream.chat.android.client.ChatClient

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

interface FirebaseUserRepository {

    // 파이어베이스 전용 함수
    suspend fun getCurrentUser(): Flow<Resource<User>>

//    suspend fun updateCurrentUser(map: Map<String, Any?>): Resource<Boolean>

    //    suspend fun setCurrentUser(map: Map<String, Any?>): Resource<Boolean>
    suspend fun setCurrentUser(map: Map<String, Any?>): Resource<Boolean>
    suspend fun uploadUserImagesAndUpdateToFirestore(
        imageUris: List<Uri>,
        status: String
    ): Resource<Boolean>

    suspend fun setCurrentUserWithImage(
        imageData: ByteArray,
        map: Map<String, Any?>,
    ): Resource<Boolean>

    suspend fun logOutCurrentUser(): Resource<Boolean>

    suspend fun deleteCurrentUser(): Resource<Boolean>


    suspend fun getUniversityStudentsWantToMeet(): Resource<MutableList<User>>

    suspend fun getStreamChatToken(): Resource<String>


//    suspend fun attachToAuthenticationFolder(status: String): Resource<Boolean>

}

class FirebaseUserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: StorageReference,
) : FirebaseUserRepository {


    override suspend fun getCurrentUser(): Flow<Resource<User>> = callbackFlow {

        val docRef = firestore.collection(USER_COLLECTION).document(auth.uid!!)
            val snapshotListener = docRef.addSnapshotListener { snapshot, error ->
                // 에러 처리
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error")).isSuccess
                    return@addSnapshotListener
                }
                // 데이터 처리
                val user = snapshot?.toObject(User::class.java)
                if (user != null) {
                    trySend(Resource.Success(user)).isSuccess
                } else {
                    trySend(Resource.Error("User not found")).isSuccess
                }

            }
            awaitClose { snapshotListener.remove() }




    }

    override suspend fun setCurrentUser(map: Map<String, Any?>): Resource<Boolean> {
        return try {
            val apiKey = BuildConfig.DEEPL_API

            val translatedMap = mutableMapOf<String, Any?>()

            map.forEach { (key, value) ->
                when (key) {
                    "introduction", "major", "name" -> {
                        if (value is String) {
                            if (value.isNotEmpty()) {
                                val translations = translateText(value, apiKey)
                                translatedMap[key] = TranslatedText(
                                    ko = translations["KO"] ?: "",
                                    en = translations["EN"],
                                    ja = translations["JA"],
                                    zh = translations["ZH"]
                                )
                            } else {
                                translatedMap[key] = TranslatedText("", "", "", "")
                            }
                        }
                    }
                    "chipGroup" -> {
                        if (value is List<*> && value.isNotEmpty()) {
                            val listAsString = value.joinToString(separator = ",")
                            val translations = translateText(listAsString, apiKey)
                            translatedMap[key] = TranslatedList(
                                ko = translations["KO"]?.split(",")?.map { it.trim() },
                                en = translations["EN"]?.split("、", ",")?.map { it.trim() },
                                ja = translations["JA"]?.split("、", ",")?.map { it.trim() },
                                zh = translations["ZH"]?.split("、", ",")?.map { it.trim() }
                            )
                        } else {
                            translatedMap[key] = TranslatedList(listOf(), listOf(), listOf(), listOf())
                        }
                    }
                    else -> {
                        // 이 경우에 대해서는 특별한 처리 없이 바로 업데이트
                        translatedMap[key] = value
                    }
                }
            }

            firestore.collection(USER_COLLECTION).document(auth.uid!!).update(translatedMap).await()

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }


    //Text를 중국어, 일본어, 영어로 번역하는 함수
    suspend fun translateText(
        text: String,
        apiKey: String
    ): Map<String, String> {
        val client = OkHttpClient()
        val languages = listOf("EN", "JA", "ZH")
        val translations = mutableMapOf<String, String>()

        languages.forEach { lang ->
            val formBody = FormBody.Builder()
                .add("auth_key", apiKey)
                .add("text", text)
                .add("target_lang", lang)
                .build()

            val request = Request.Builder()
                .url("https://api-free.deepl.com/v2/translate")
                .post(formBody)
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val responseJson = JSONObject(responseBody)
                val translatedText = responseJson.getJSONArray("translations").getJSONObject(0).getString("text").trim()

                translations[lang] = translatedText
            } else {
                Log.e("translationError", "Failed to translate to $lang: ${response.code}")
            }
        }

        translations["KO"] = text
        Log.e("translations", translations.toString())
        return translations
    }



    override suspend fun uploadUserImagesAndUpdateToFirestore( // 인증 사진을 파이어스토어에 저장을 하는 과정
        imageUris: List<Uri>,
        status: String
    ): Resource<Boolean> {
        return coroutineScope {
            try {
                val storageRef = storage.child("user/${auth.uid!!}/authentication")

                // async로 각 이미지 업로드 작업을 병렬로 시작
                val uploadedImageUrls = imageUris.map { uri ->
                    async {
                        val imageRef =
                            storageRef.child("${System.currentTimeMillis()}-${uri.lastPathSegment}")
                        val uploadTask = imageRef.putFile(uri).await()
                        uploadTask.storage.downloadUrl.await().toString()
                    }
                }.awaitAll()

                // 업로드된 이미지 URL을 사용하여 Firestore 데이터 업데이트
                val userRef = firestore.collection(USER_COLLECTION).document(auth.uid!!)
                val updateField =
                    if (status == STUDENT) "authentication.studentIdentificationCard" else "authentication.travelerAuthenticationImage"
                val map = mapOf(
                    updateField to uploadedImageUrls,
                    "authentication.authenticationStatus" to "loading"
                )
                userRef.update(map).await()

                Resource.Success(true)
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "An error occurred while uploading images.")
            }
        }

    }


    override suspend fun setCurrentUserWithImage(
        imageData: ByteArray,
        map: Map<String, Any?>,
    ): Resource<Boolean> {
        return try {
            val imageDirectory = storage.child("user/${auth.uid}/imagePath")
            // 해당 주소에 데이터를 입력한다.
            val result = imageDirectory.putBytes(imageData).await()
            val imageUrl = result.storage.downloadUrl.await().toString()

            val updatedMap = map + ("imagePath" to imageUrl)
            setCurrentUser(updatedMap)

//            firestore.collection(USER_COLLECTION).document(auth.uid!!).update(updatedMap).await()
            Resource.Success(true) // 업데이트 성공 시 true 반환
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun logOutCurrentUser(): Resource<Boolean> {
        return try {
            auth.signOut()
            // 로그아웃 성공
            Resource.Success(true)
        } catch (e: Exception) {
            // 에러 처리
            Resource.Error(e.message ?: "An error occurred while logging out")
        }


    }

    override suspend fun deleteCurrentUser(): Resource<Boolean> {
        return try {
            val uid =auth.uid!!
            val storageRef = storage.child("user/${uid}")
//            storageRef.delete().await()
            // 사용자의 스토리지 참조 하위의 모든 파일과 디렉토리 목록 가져오기
            val allFiles = storageRef.listAll().await()
            Log.e("allFiles", allFiles.toString())

            // 모든 파일 삭제
            allFiles.items.forEach { fileRef ->
                fileRef.delete().await()
            }


            // 선택사항: 모든 디렉토리 삭제
            allFiles.prefixes.forEach { dirRef ->
                dirRef.listAll().await().items.forEach { fileRef ->
                    fileRef.delete().await()
                }
            }
            firestore.collection(USER_COLLECTION).document(uid).delete().await()

            auth.currentUser?.delete()?.await()





            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred while deleting the user")
        }
    }


    override suspend fun getUniversityStudentsWantToMeet(): Resource<MutableList<User>> {

        return try {
            val querySnapshot =
                firestore.collection(USER_COLLECTION)
                    .whereEqualTo("authentication.collegeStudent", true)
                    .whereEqualTo("wantToMeet", true)
                    .get().await()
            Log.e("querySnapshot.documents", querySnapshot.documents.toString())


            val students = querySnapshot.toObjects(User::class.java)

            Resource.Success(students)

        } catch (e: Exception) {
            // 에러 처리
            Resource.Error(e.message ?: "An error occurred while fetching university students")
        }
    }

    override suspend fun getStreamChatToken(): Resource<String> {
        return try {
            // FirebaseFunctions 인스턴스를 생성합니다.
            val functions = FirebaseFunctions.getInstance("asia-northeast3")
            // HTTPS Callable 함수를 호출하여 Stream Chat 토큰을 가져옵니다.
            val result = functions
                .getHttpsCallable("ext-auth-chat-getStreamUserToken")
                .call()
                .await()

            // 결과에서 데이터를 추출하여 토큰을 얻습니다.
            val token = result.data as? String ?: throw IllegalStateException("Token not found")

            // 토큰을 로컬에 저장합니다.
            BusanPartners.preferences.setString("token", token)

            // 성공적으로 토큰을 받아왔다면, Resource.Success로 감싸서 반환합니다.
            Resource.Success(token)
        } catch (e: Exception) {
            // 예외가 발생했다면, Resource.Error로 감싸서 반환합니다.
            Resource.Error(e.localizedMessage ?: "Failed to fetch Stream Chat token")
        }

    }

}
