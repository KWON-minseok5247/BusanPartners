package com.kwonminseok.busanpartners.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
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


fun saveUserUniversity(university: String) {
    //auth.uid!!라고 정의한 이유는 로그아웃을 하지 않았을 때 절대로 auth.uid가 없을 수 없기 때문임.
    val userRef = firestore.collection("user").document(auth.uid!!)

    userRef.update("college", university)
        .addOnSuccessListener {
            Log.w("대학교", "정상적으로 수정되었습니다.")
        }.addOnFailureListener {
            Log.w("대학교", "실패했습니다. ${it.message}")
        }

}
fun clearEmailAuthentication() {
        //auth.uid!!라고 정의한 이유는 로그아웃을 하지 않았을 때 절대로 auth.uid가 없을 수 없기 때문임.
        val userRef = firestore.collection("user").document(auth.uid!!)

        userRef.update("authentication.studentEmailAuthenticationComplete", true)
            .addOnSuccessListener {
                Log.w("이메일 인증 절차", "정상적으로 수정되었습니다.")
            }.addOnFailureListener {
                Log.w("이메일 인증 절차", "실패했습니다. ${it.message}")
            }
}

    // 스토리지에 업데이트하는 함수
private fun uploadImagesToFirebaseStorage(
    imageUris: List<Uri>,
    onComplete: (List<String>) -> Unit
) {
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


    // 데이터베이스에 사진리스트를 추가하는 함수
private fun updateUserImagesInFirestore(imageUrls: List<String> ,status: String) {
    val userRef = firestore.collection("user").document(auth.uid!!)

    if (status == "student") {
        userRef.update("authentication.studentIdentificationCard", imageUrls).addOnSuccessListener {
            Log.w("updateUserImagesInFirestore", "학생증 사진이 등록되었습니다.")
        }.addOnFailureListener { e ->
            Log.w("updateUserImagesInFirestore", "학생증 사진 등록에 실패했습니다.")
        }
    } else {
        userRef.update("authentication.travelerAuthenticationImage", imageUrls).addOnSuccessListener {
            Log.w("updateUserImagesInFirestore", "관광객 인증 내역이 등록되었습니다.")
        }.addOnFailureListener { e ->
            Log.w("updateUserImagesInFirestore", "관광객 인증 내역 등록에 실패했습니다.")
        }
    }


}

fun processUserImageSelection(selectedImageUris: List<Uri>, status: String) {
    uploadImagesToFirebaseStorage(selectedImageUris) { uploadedImageUrls ->
        updateUserImagesInFirestore(uploadedImageUrls, status)
    }
}

    //스토리지 이미지 삭제 함수
 fun deleteImageFromStorage(imageUrl: String) {
    // Firebase Storage 인스턴스를 얻습니다.

    // 주어진 URL로부터 Storage 참조를 얻습니다.
    // 이때, imageUrl이 gs://로 시작하는 Storage의 URL이라고 가정합니다.
    val storageRef = storageReference.storage.getReferenceFromUrl(imageUrl)
    // 해당 참조의 파일을 삭제합니다.
    storageRef.delete().addOnSuccessListener {
        // 파일 삭제 성공
        Log.d("deleteImageFromStorage", "이미지 삭제 성공")
    }.addOnFailureListener {
        // 파일 삭제 실패
        Log.e("deleteImageFromStorage", "이미지 삭제 실패", it)
    }
}

fun deleteImageFromDatabase(imageUrl: String, status: String) {

    // 학생일 때
    if (status == "student") {
        val userRef = firestore.collection("user").document(auth.uid!!)
        // 여기서는 FieldValue.arrayRemove를 사용하여 특정 URL을 리스트에서 제거합니다.
        userRef.update("authentication.studentIdentificationCard", FieldValue.arrayRemove(imageUrl))
            .addOnSuccessListener {
                Log.e(TAG, "Firestore에서 이미지 URL 제거 성공 ")

            }.addOnFailureListener {
                Log.e(TAG, "Firestore에서 이미지 URL 제거 실패: ${it.message}")
            }

    } else { // 여행객일 때
        val userRef = firestore.collection("user").document(auth.uid!!)
        // 여기서는 FieldValue.arrayRemove를 사용하여 특정 URL을 리스트에서 제거합니다.
        userRef.update("authentication.travelerAuthenticationImage", FieldValue.arrayRemove(imageUrl))
            .addOnSuccessListener {
                Log.e(TAG, "Firestore에서 이미지 URL 제거 성공 ")

            }.addOnFailureListener {
                Log.e(TAG, "Firestore에서 이미지 URL 제거 실패: ${it.message}")
            }
    }


}


// 이미지 삭제 함수 구현 예시
//    fun deleteImage(imageUrl: String, position: Int) {
//        // Firebase Storage의 참조를 얻습니다.
//        val storageRef = storageReference
//
//        // 이미지 URL로부터 Firebase Storage 내의 파일 경로를 추출합니다.
//        val imagePath = Uri.parse(imageUrl).pathSegments.drop(1).joinToString("/")
//
//        // 해당 파일 참조를 얻고 삭제합니다.
//        storageRef.child(imagePath).delete().addOnSuccessListener {
//            // Storage에서 이미지 삭제 성공 후, Firestore Database에서 URL 제거
//            removeImageUrlFromFirestore(imageUrl) {
//                // Firestore Database에서 URL 제거 성공 후, 어댑터 데이터셋에서 이미지 URL 제거 및 UI 갱신
//                images.removeAt(position)
//                notifyItemRemoved(position)
//            }
//        }.addOnFailureListener {
//            // 이미지 삭제 실패 처리
//            Log.e(TAG, "이미지 삭제 실패: ${it.message}")
//        }
//    }

// Firestore Database에서 이미지 URL 제거하는 함수
fun removeImageUrlFromFirestore(imageUrl: String, onSuccess: () -> Unit) {
    val userRef = firestore.collection("user").document(auth.uid!!)
    // 여기서는 FieldValue.arrayRemove를 사용하여 특정 URL을 리스트에서 제거합니다.
    userRef.update("authentication.studentIdentificationCard", FieldValue.arrayRemove(imageUrl))
        .addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            Log.e(TAG, "Firestore에서 이미지 URL 제거 실패: ${it.message}")
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
