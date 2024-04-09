package com.kwonminseok.busanpartners.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.util.Constants.USER_COLLECTION
import com.kwonminseok.busanpartners.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

interface FirebaseUserRepository {
//    fun getCurrentUser(): User?
//    fun setCurrentUser(user: User)
//    fun clearCurrentUser()

    suspend fun getCurrentUser(): Resource<User>

//    suspend fun updateCurrentUser(map: Map<String, Any?>): Resource<Boolean>

    suspend fun setCurrentUser(map: Map<String, Any?>): Resource<Boolean>

    suspend fun setCurrentUserWithImage(
        imageData: ByteArray,
        map: Map<String, Any?>,
    ): Resource<Boolean>
}

class FirebaseUserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: StorageReference
) : FirebaseUserRepository {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateUser = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateUser = _updateUser.asStateFlow()


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    override suspend fun getCurrentUser(): Resource<User> {
        return try {
            val docSnapshot =
                firestore.collection(USER_COLLECTION).document(auth.uid!!).get().await()
            // 유저가 사진을 바꾸는 등의 행동을 하기에 addsnap으로 한다.
            val user = docSnapshot.toObject(User::class.java)
            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Error("User not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    // 내가 수정해야 하는 데이터를 수정하도록 한다.
    override suspend fun setCurrentUser(map: Map<String, Any?>): Resource<Boolean> {
        return try {
            firestore.collection(USER_COLLECTION).document(auth.uid!!).update(map).await()
            Resource.Success(true) // 업데이트 성공 시 true 반환
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    override suspend fun setCurrentUserWithImage(
        imageData: ByteArray,
        map: Map<String, Any?>,
    ): Resource<Boolean> {
        return try {
//            val imageBitmap = MediaStore.Images.Media.getBitmap(getApplication<BusanPartners>()
//                .contentResolver,uri)
            // 이미지를 비트맵으로 변환
//            val imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                val source = ImageDecoder.createSource(context.contentResolver, uri)
//                ImageDecoder.decodeBitmap(source)
//            } else {
//                @Suppress("DEPRECATION")
//                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
//            }

//            val byteArrayOutputStream = ByteArrayOutputStream()
//            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
//            val imageByteArray = byteArrayOutputStream.toByteArray()
            // 어디에 저장할지 주소를 만들어두고
            // 이미지 파일명의 이름을 고정시켜놔서 굳이 삭제하지 않아도 된다.
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

}