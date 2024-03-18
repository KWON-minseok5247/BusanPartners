package com.kwonminseok.busanpartners.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.mainScreen.TAG
import com.kwonminseok.busanpartners.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthCompleteViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.unspecified())
    val user = _user.asStateFlow()


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
                            firestore.collection("user").document(auth.uid!!).update("isCollegeStudent", true)
                                .addOnSuccessListener {
                                    Log.d(TAG,"isCollegeStudent가 정상적으로 업데이트됐습니다.")
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

}