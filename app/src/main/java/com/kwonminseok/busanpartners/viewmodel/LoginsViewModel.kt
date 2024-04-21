package com.kwonminseok.busanpartners.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.util.RegisterValidation
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.validateEmail
import com.kwonminseok.busanpartners.util.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
private val TAG = "LoginsViewModel"
@HiltViewModel
class LoginsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ViewModel() {

//    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
//    val login = _login.asSharedFlow()

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()




    fun saveUserToDatabase(user: User) {
        firestore.collection("user").document(user.uid).set(user)
            .addOnSuccessListener {
                // 데이터 저장 성공 처리
                Log.d(TAG, "User data saved successfully")
            }
            .addOnFailureListener {
                // 데이터 저장 실패 처리
                Log.e(TAG, "Failed to save user data", it)
            }
    }





// 현재 사용자의 idToken을 확인하여 자동 로그인 시킬지 말지 결정



    fun login(email: String, password: String) {
        // 일단 이메일이나 비밀번호 오류가 있는지는 확인해야 한다.
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }
        CoroutineScope(Dispatchers.IO).launch { // 네트워크 속도 엄청 빨라짐!!!!!!!!!!!!!!!!!!!
            if (validation(email, password)) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Success(it.user!!))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Error(it.message.toString()))
                    }
                }
            } else {
                viewModelScope.launch {
                    _login.emit(Resource.Error("Check your id or password"))
                }
            }
        }


    }

    fun validation(email: String, password: String): Boolean {
        val validationEmail = validateEmail(email)
        val validationPassword = validatePassword(password)
        return validationEmail is RegisterValidation.Success && validationPassword is RegisterValidation.Success
    }

    fun resetPasswordFun(email: String) {
        //TODO 여기서는 클릭을 함과 동시에 다이얼로그를 띄워야 하며 이메일을 보낼 수 있도록 한다. + 이메일 양식이 제대로 되어야 한다.
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
            viewModelScope.launch {
                _resetPassword.emit(Resource.Success(email))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _resetPassword.emit(Resource.Error(it.message.toString()))
            }
        }




    }








//
//    fun login(email: String, password: String) {
//        viewModelScope.launch { _login.emit(Resource.Loading()) }
//        firebaseAuth.signInWithEmailAndPassword(
//            email, password
//        ).addOnSuccessListener {
//            viewModelScope.launch {
//                it.user?.let {
//                    // 왜 it.user를 썼지??
//                    _login.emit(Resource.Success(it))
//
//                }
//            }
//        }.addOnFailureListener {
//            viewModelScope.launch {
//                _login.emit(Resource.Error(it.message.toString()))
//            }
//
//        }
//
//    }
//
//    fun resetPassword(email: String) {
//        viewModelScope.launch {
//            _resetPassword.emit(Resource.Loading())
//        }
//
//
//        firebaseAuth.sendPasswordResetEmail(email)
//            .addOnSuccessListener {
//                viewModelScope.launch {
//                    _resetPassword.emit(Resource.Success(email))
//                }
//            }
//            .addOnFailureListener {
//                viewModelScope.launch {
//                    _resetPassword.emit(Resource.Error(it.message.toString()))
//                }
//            }
//    }


}