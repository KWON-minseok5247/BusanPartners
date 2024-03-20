package com.kwonminseok.busanpartners.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.mainScreen.TAG
import com.kwonminseok.busanpartners.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageReference: StorageReference
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    private fun getUser() {

        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        //auth.uid!!라고 정의한 이유는 로그아웃을 하지 않았을 때 절대로 auth.uid가 없을 수 없기 때문임.
        firestore.collection("user").document(auth.uid!!)
            // 유저가 사진을 바꾸는 등의 행동을 하기에 addsnap으로 한다.
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    val user = value?.toObject(User::class.java)
                    // 유저가 null이 된다는 불상사를 사전에 방지하기 위하여 let을 쓴다.
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Success(user))
                        }
                    }
                }
            }
    }


    fun saveCollegeStatus() {
        viewModelScope.launch {
            //auth.uid!!라고 정의한 이유는 로그아웃을 하지 않았을 때 절대로 auth.uid가 없을 수 없기 때문임.
            firestore.collection("user").document(auth.uid!!)
                // 유저가 사진을 바꾸는 등의 행동을 하기에 addsnap으로 한다.
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        viewModelScope.launch {
                            _user.emit(Resource.Error(error.message.toString()))
                        }
                    } else {
                        // `user` 객체 받아오기
                        val user = value?.toObject(User::class.java)
                        user?.let {
                            // Firestore 문서 업데이트
                            firestore.collection("user").document(auth.uid!!)
                                .update("isCollegeStudent", true)
                                .addOnSuccessListener {
                                    Log.d(TAG, "isCollegeStudent가 정상적으로 업데이트됐습니다.")
                                }
                                .addOnFailureListener { e ->
                                    viewModelScope.launch {
                                        _user.emit(Resource.Error(e.message.toString()))
                                    }
                                }
                        }

                    }
                }
        }
    }

//    fun saveUserWithImage(imageUris: ArrayList<Uri>) { // 사진만 혹은 사진과 같이 변경할 때 사용
//        val uploadedImageUrls = ArrayList<String>()
//
//        imageUris.forEachIndexed { index, uri ->
//            // 각 이미지 파일에 대한 고유한 파일 이름 생성 (예: timestamp)
//            val timestamp = System.currentTimeMillis()
//
//            val fileReference: StorageReference =
//                storageReference.child("user/${auth.uid}/img_${timestamp}_$index.jpg")
//
//            fileReference.putFile(uri).continueWithTask { task ->
//                if (!task.isSuccessful) {
//                    task.exception?.let {
//                        throw it
//                    }
//                }
//                // 다운로드 URL을 가져옵니다.
//                fileReference.downloadUrl
//            }.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val downloadUri = task.result
//                    uploadedImageUrls.add(downloadUri.toString())
//
//                    // 모든 이미지가 업로드되었는지 확인
//                    if (uploadedImageUrls.size == imageUris.size) {
//                        // 모든 이미지 업로드 완료 후, Firebase Database에 URL 저장
//                        saveImageUrlsToDatabase(uploadedImageUrls)
//                    }
//                } else {
//                    // 에러 처리
//                }
//            }
//        }
//    }

    private fun uploadImagesToFirebaseStorage(imageUris: List<Uri>, onComplete: (List<String>) -> Unit) {
        val storageRef = storageReference.child("user/${auth.uid}")
        val uploadedImageUrls = mutableListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            imageUris.forEach { uri ->
                val imageRef = storageRef.child("${System.currentTimeMillis()}-${uri.lastPathSegment}")
                val uploadTask = imageRef.putFile(uri).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
                uploadedImageUrls.add(downloadUrl)
            }

            onComplete(uploadedImageUrls)
        }
    }


    private fun updateUserImagesInFirestore(imageUrls: List<String>) {
        val userRef = firestore.collection("user").document(auth.uid!!)

        userRef.update("authentication.studentIdentificationCard", imageUrls).addOnSuccessListener {
            Log.w("updateUserImagesInFirestore","학생증 사진이 등록되었습니다.")
        }.addOnFailureListener { e ->
            Log.w("updateUserImagesInFirestore","학생증 사진 등록에 실패했습니다.")
        }
    }

    fun processUserImageSelection(selectedImageUris: List<Uri>) {
        uploadImagesToFirebaseStorage(selectedImageUris) { uploadedImageUrls ->
            updateUserImagesInFirestore(uploadedImageUrls)
        }
    }










//    fun updateUserImagesInFirestore(imageUrls: List<String>) {
//        val userRef = firestore.collection("user").document(auth.uid!!)
//
//        userRef.update("authentication.studentIdentificationCard", imageUrls).addOnSuccessListener {
//            // 업데이트 성공 처리
//        }.addOnFailureListener { e ->
//            // 업데이트 실패 처리
//        }
//    }


    fun saveImageUrlsToDatabase(imageUrls: ArrayList<String>) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Images")

        // 여기서는 예시로 "imageUrls" 라는 key 아래에 이미지 URL 리스트를 저장합니다.
        // 실제로는 사용자 ID나 게시물 ID 등을 key로 사용할 수 있습니다.
        databaseReference.child("imageUrls").setValue(imageUrls).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 데이터베이스 업데이트 성공
            } else {
                // 에러 처리
            }
        }
    }

}
