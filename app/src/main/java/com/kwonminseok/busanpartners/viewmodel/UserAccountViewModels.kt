package com.kwonminseok.busanpartners.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kwonminseok.busanpartners.BusanPartners
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

private val TAG = "UserAccountViewModels"
@HiltViewModel
class UserAccountViewModels @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: StorageReference,
    application: Application
) : AndroidViewModel(application) {
    //TODO 여기서는 일단 접속을 함과 동시에 파이어베이스의 데이터를 불러오고 작성할 수 있어야 한다.
    // 그리고 데이터를 수정했으면 다시 파이어베이스에 업로드를 해야 함.
    // 마지막으로 사진을 업로드하고 새로 수정하는 역할도 있어야 한다.

    private val _user = MutableStateFlow<Resource<User>>(Resource.unspecified())
    val user = _user.asStateFlow()

    private val _updateUser = MutableStateFlow<Resource<User>>(Resource.unspecified())
    val updateUser = _updateUser.asStateFlow()

    init {
        getUserData()
    }


    private fun getUserData() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                viewModelScope.launch {
                    _user.emit(Resource.Success(user!!))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun saveUser(map: Map<String, Any>) { // 사진말고 닉네임만 변경시 작동


        // TODO set하니까 전부 초기화되네...
        viewModelScope.launch {
            _updateUser.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!).update(map)
            .addOnSuccessListener {
                Log.w(TAG,"user 데이터가 정상적으로 업데이트 되었습니다.")
            }.addOnFailureListener {
                Log.w(TAG,"user 데이터 업데이트에 실패했습니다. 사유 :${it.message}")

            }
    }

    fun saveUserWithImage(map: Map<String, Any>, uri: Uri) { // 사진만 혹은 사진과 같이 변경할 때 사용
// 근데 속도가 너무 느린데?????????
        viewModelScope.launch {
            _updateUser.emit(Resource.Loading())
        }
        // 따라하니까 속도 2배는 빨라짐
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(getApplication<BusanPartners>()
                    .contentResolver,uri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                // 어디에 저장할지 주소를 만들어두고
                // 이미지 파일명의 이름을 고정시켜놔서 굳이 삭제하지 않아도 된다.
                val imageDirectory = storage.child("user/${auth.uid}/imagePath")
                // 해당 주소에 데이터를 입력한다.
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()

                val updatedMap = map + ("imagePath" to imageUrl)

                saveUser(updatedMap)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _updateUser.emit(Resource.Error(e.message.toString()))
                }
            }
        }
//
//
    }
}