package com.kwonminseok.busanpartners.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.kwonminseok.busanpartners.data.CheckAuthentication
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.util.Constants.STUDENT
import com.kwonminseok.busanpartners.util.Constants.TRAVLER_AUTHENTICATION
import com.kwonminseok.busanpartners.util.Constants.UNIVERSITY_AUTHENTICATION
import com.kwonminseok.busanpartners.util.Constants.USER_COLLECTION
import com.kwonminseok.busanpartners.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

interface FirebaseUserRepository {
    //    suspend fun getCurrentUser(): Resource<User>
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

    suspend fun getUniversityStudentsWantToMeet(): Resource<MutableList<User>>

    suspend fun attachToAuthenticationFolder(status: String): Resource<Boolean>

}

class FirebaseUserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: StorageReference
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
        // 채널이 닫힐 때 리스너 해제
        awaitClose { snapshotListener.remove() }
    }
//    override suspend fun getCurrentUser(): Resource<User> {
//        return try {
//            val docSnapshot =
//                firestore.collection(USER_COLLECTION).document(auth.uid!!).get().await()
//            // 유저가 사진을 바꾸는 등의 행동을 하기에 addsnap으로 한다.
//            val user = docSnapshot.toObject(User::class.java)
//            if (user != null) {
//                Resource.Success(user)
//            } else {
//                Resource.Error("User not found")
//            }
//        } catch (e: Exception) {
//            Resource.Error(e.message ?: "Unknown error")
//        }
//    }

    // 데이터를 수정하도록 한다.
    override suspend fun setCurrentUser(map: Map<String, Any?>): Resource<Boolean> {
        return try {
            firestore.collection(USER_COLLECTION).document(auth.uid!!).update(map).await()
            Resource.Success(true) // 업데이트 성공 시 true 반환

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun uploadUserImagesAndUpdateToFirestore(
        imageUris: List<Uri>,
        status: String
    ): Resource<Boolean> {
            return try {
                val storageRef = storage.child("user/${auth.uid!!}/authentication")
                val uploadedImageUrls = mutableListOf<String>()

                for (uri in imageUris) {
                    val imageRef =
                        storageRef.child("${System.currentTimeMillis()}-${uri.lastPathSegment}")
                    val uploadTask = imageRef.putFile(uri).await()
                    val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
                    uploadedImageUrls.add(downloadUrl)
                }

                val userRef = firestore.collection(USER_COLLECTION).document(auth.uid!!)
                val updateField =
                    if (status == STUDENT) "authentication.studentIdentificationCard" else "authentication.travelerAuthenticationImage"
                val map = mapOf(updateField to uploadedImageUrls,
                    "authentication.authenticationStatus" to "loading"
                    )
                userRef.update(map).await()

                Resource.Success(true) // 성공 시 true 반환
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "An error occurred while uploading images.")
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

            firestore.collection(USER_COLLECTION).document(auth.uid!!).update(updatedMap).await()
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

    override suspend fun getUniversityStudentsWantToMeet(): Resource<MutableList<User>> {

        return try {
            val querySnapshot =
                firestore.collection(USER_COLLECTION)
                    .whereEqualTo("authentication.collegeStudent", true)
                    .whereEqualTo("wantToMeet", true)
                    .get().await()

            // 유저가 사진을 바꾸는 등의 행동을 하기에 addsnap으로 한다.
            val students = querySnapshot.toObjects(User::class.java)
            Resource.Success(students)

        } catch (e: Exception) {
            // 에러 처리
            Resource.Error(e.message ?: "An error occurred while fetching university students")
        }
    }

    override suspend fun attachToAuthenticationFolder(status: String): Resource<Boolean> {
        return try {

            val docSnapshot = firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .get().await()

            val user = docSnapshot.toObject(User::class.java)
            val checkAuthentication = CheckAuthentication(
                user!!.uid,
                user.authentication.studentIdentificationCard,
                user.authentication.travelerAuthenticationImage,
                user.universityEmail,
                user.college
            )

//            firestore.collection("user").document(auth.uid!!)
//                .update("authentication.authenticationStatus", "loading")
//                .addOnSuccessListener {
//                    Log.w("authenticationStatus = Loading", "정상적으로 수정되었습니다.")
//
//                }.addOnFailureListener {
//                    Log.w("authenticationStatus = Loading 실패", "${it.message}.")
//
//                }
            // 대학생일 때
            if (status == STUDENT) {
                firestore.collection(UNIVERSITY_AUTHENTICATION).document(auth.uid!!)
                    .set(checkAuthentication).await()
                Resource.Success(true)

            } else { // 관광객일 때
                firestore.collection(TRAVLER_AUTHENTICATION).document(auth.uid!!)
                    .set(checkAuthentication).await()
                Resource.Success(true)


            }


        } catch (e: Exception) {
            // 에러 처리
            Resource.Error(e.message ?: "An error occurred while attach folder on firebase")
        }
    }
}