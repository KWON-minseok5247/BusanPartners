package com.kwonminseok.newbusanpartners.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.newbusanpartners.util.RegisterFieldsState
import com.kwonminseok.newbusanpartners.util.RegisterValidation
import com.kwonminseok.newbusanpartners.util.Resource
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.util.Constants.USER_COLLECTION
import com.kwonminseok.newbusanpartners.util.validateEmail
import com.kwonminseok.newbusanpartners.util.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
    // 위에 애들을 실행하는 순간 Appmodule의 애들이 실행되는 것 같다.
) : ViewModel() {
//     이메일에 오류가 없는지 확인하는 절차가 필요하다. 이게 우선순위임
//
//     파이어베이스에 정상적으로 이메일과 아이디를 만들 수 었어야 한다.
    // 로그인시점에 firebaseAuth.user가 업데이트되어야 한다.
//
//    private val _register = MutableStateFlow<Resource<User>>(Resource.unspecified())
//    val register = _register.asStateFlow()
//
//    private val _validation = MutableSharedFlow<Resource<User>>()
//    val validation = _validation.asSharedFlow()
//
//
//    fun registerId(user: User, password: String) {
//        viewModelScope.launch {
//            _register.emit(Resource.Loading())
//        }
//        val result = validateProfile(user, password)
//        if (result) {
//            firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
//                saveDb(user)
//
//            }.addOnFailureListener {
//                viewModelScope.launch {
//                    _register.emit(Resource.Error(it.message.toString()))
//                }
//            }
//        }
//
//
//    }
//
//    // UUID.randomUUID와 firebaseAuth.uid는 무슨 차이지?
//    private fun saveDb(user: User) {
////        val uid = UUID.randomUUID().toString()
//        db.collection("user").document(firebaseAuth.uid!!).set(user).addOnSuccessListener {
//            viewModelScope.launch {
//                _register.emit(Resource.Success(user))
//            }
//        }.addOnFailureListener {
//            viewModelScope.launch {
//                _register.emit(Resource.Error(it.message.toString()))
//            }
//        }
//
//    }
//
//    private fun validateProfile(user: User, password: String): Boolean {
//        val validateEmail = validateEmail(user.email)
//        val validatePassword = validatePassword(password)
//        return validateEmail is RegisterValidation.Success && validatePassword is RegisterValidation.Success
//
//    }



    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    // ui에 딱 한번만 쓰는 경우 sharedFlow를 사용하는 것 같다.
    // ui에 이벤트를 보내는 경우 sharedflow를 쓰고 아니면 channel을 쓴다더라. 그냥 유튜버가 그렇게 씀.
    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()


    fun createAccountWithEmailAndPassword(user: User, password: String) {


//        checkValidation(user, password)
        if (checkValidation(user,password)) {


            // runBlocking은 얘가 끝날 때까지 다른 거 일시중단하는 함수인듯?
            runBlocking {
                _register.emit(Resource.Loading())
            }

            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid,user)
//                        _register.value = Resource.Success(it)
                    }
                }
                    .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())

                }
        } else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user:User) {
        val newUser = user.copy(uid = userUid)

        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(newUser)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
                firebaseAuth.signOut()
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
        return shouldRegister
    }
}