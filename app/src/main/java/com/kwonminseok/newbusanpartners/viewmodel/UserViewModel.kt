package com.kwonminseok.newbusanpartners.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.db.entity.UserEntity
import com.kwonminseok.newbusanpartners.repository.FirebaseUserRepository
import com.kwonminseok.newbusanpartners.repository.RoomUserRepository
import com.kwonminseok.newbusanpartners.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private val TAG = "UserAccountViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: FirebaseUserRepository,
    private val roomUserRepository: RoomUserRepository
) :
    ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user: StateFlow<Resource<User>> = _user

    private val _updateStatus = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val updateStatus: StateFlow<Resource<Boolean>> = _updateStatus

    private val _students = MutableStateFlow<Resource<MutableList<User>>>(Resource.Unspecified())
    val students = _students.asStateFlow()

    private val _token = MutableLiveData<Resource<String>>()
    val token: LiveData<Resource<String>> = _token

    // Room이 있으면 init에 넣어서 재빨리 확인할 수도 있지 않을까?
//    init {
//        getCurrentUser()
//    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _user.value = Resource.Loading()
            userRepository.getCurrentUser().collect { resource ->
                _user.value = resource
            }
        }
    }

    fun setDeleteReason(reason: String, details: String?) {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value = userRepository.setDeleteReason(reason, details)
        }
    }

    fun setCurrentUser(map: Map<String, Any?>) {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value = userRepository.setCurrentUser(map)
        }
    }

    fun setCurrentUserForBeginner(map: Map<String, Any?>) {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value = userRepository.setCurrentUserForBeginner(map)
        }
    }


    fun setCurrentUserWithImage(imageData: ByteArray, map: Map<String, Any?>) {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value = userRepository.setCurrentUserWithImage(imageData, map)
        }
    }

    fun logOutCurrentUser() {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value = userRepository.logOutCurrentUser()
        }
    }

    //    fun deleteCurrentUser() {
//        viewModelScope.launch {
//            _updateStatus.value = Resource.Loading()
//            _updateStatus.value = userRepository.deleteCurrentUser()
//        }
//    }
    suspend fun deleteCurrentUser(): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            val resource = userRepository.deleteCurrentUser()
            _updateStatus.value = resource
            resource
        }
    }


    fun getUniversityStudentsWantToMeet() {
        viewModelScope.launch {
            _students.value = Resource.Loading()
            _students.value = userRepository.getUniversityStudentsWantToMeet()
        }
    }

//    fun attachToAuthenticationFolder(status: String) {
//        viewModelScope.launch {
//            _updateStatus.value = Resource.Loading()
//            _updateStatus.value = userRepository.attachToAuthenticationFolder(status)
//        }
//    }

    fun uploadUserImagesAndUpdateToFirestore(selectedImageUris: List<Uri>, status: String) {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value =
                userRepository.uploadUserImagesAndUpdateToFirestore(selectedImageUris, status)
        }
    }

    fun getStreamChatToken() {
        viewModelScope.launch {
            _token.value = Resource.Loading()
            _token.value = userRepository.getStreamChatToken()
        }
    }

    //Room 관련 함수
    // ViewModel을 통해 UI에 제공될 LiveData
//    fun getUserRoomData(userId: String) {
//        viewModelScope.launch {
//            _user.value = Resource.Loading()
//            roomUserRepository.getUser(userId).collect { resource ->
//                _user.value = resource
//            }
//        }
//        return
//
//    }

    fun getUserStateFlowData(userId: String): LiveData<UserEntity?> {
        return roomUserRepository.getUser(userId)

    }

    // 사용자를 데이터베이스에 추가하는 함수
    fun insertUser(user: UserEntity) {
        viewModelScope.launch {
            roomUserRepository.insertUser(user)
        }
    }

    // 사용자 정보를 업데이트하는 함수
    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            roomUserRepository.updateUser(user)
        }
    }

    // 사용자를 데이터베이스에서 삭제하는 함수
    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            roomUserRepository.deleteUser(user)
        }
    }


}
