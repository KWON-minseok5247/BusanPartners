package com.kwonminseok.newbusanpartners.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

//    private val _getToken = MutableStateFlow<Resource<String?>>(Resource.unspecified())
//    val getToken = _user.asStateFlow()

    init {
//        getToken()
        getUser()
    }


    //    fun getToken() {
//
////        viewModelScope.launch {
////            _user.emit(Resource.Loading())
////        }
////        //auth.uid!!라고 정의한 이유는 로그아웃을 하지 않았을 때 절대로 auth.uid가 없을 수 없기 때문임.
////        firestore.collection("user").document(auth.uid!!)
////            // 유저가 사진을 바꾸는 등의 행동을 하기에 addsnap으로 한다.
////            .addSnapshotListener { value, error ->
////                if (error != null) {
////                    viewModelScope.launch {
////                        _user.emit(Resource.Error(error.message.toString()))
////                    }
////                } else {
////                    val user = value?.toObject(User::class.java)
////                    // 유저가 null이 된다는 불상사를 사전에 방지하기 위하여 let을 쓴다.
////                    user?.let {
////                        viewModelScope.launch {
////                            _user.emit(Resource.Success(user))
////                        }
////                    }
////                }
////            }
//        viewModelScope.launch {
//            _user.emit(Resource.Loading())
//        }
//        auth.currentUser!!.getIdToken(true).addOnSuccessListener {
//            val token = it.token
//            Log.e("Token", token!!)
//            viewModelScope.launch {
//                _getToken.emit(Resource.Success(token))
//            }
//
//        }.addOnFailureListener {
//            viewModelScope.launch {
//                _getToken.emit(Resource.Error(it.message.toString()))
//            }
//        }
//    }
    fun getUid(): String {
        return auth.currentUser!!.uid
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


    fun logout() {
        auth.signOut()
    }
}