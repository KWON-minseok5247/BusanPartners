package com.kwonminseok.busanpartners.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.repository.FirebaseUserRepository
import com.kwonminseok.busanpartners.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = "UserAccountViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: FirebaseUserRepository) :
    ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user: StateFlow<Resource<User>> = _user

    private val _updateStatus = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val updateStatus: StateFlow<Resource<Boolean>> = _updateStatus

    private val _students = MutableStateFlow<Resource<MutableList<User>>>(Resource.Unspecified())
    val students = _students.asStateFlow()

    init {
        getCurrentUser()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _user.value = Resource.Loading()
            userRepository.getCurrentUser().collect { resource ->
                _user.value = resource
            }
        }
    }

    fun setCurrentUser(map: Map<String, Any?>) {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value = userRepository.setCurrentUser(map)
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

    fun getUniversityStudentsWantToMeet() {
        viewModelScope.launch {
            _students.value = Resource.Loading()
            _students.value = userRepository.getUniversityStudentsWantToMeet()
        }
    }

    fun attachToAuthenticationFolder(status: String) {
        viewModelScope.launch {
            _updateStatus.value = Resource.Loading()
            _updateStatus.value = userRepository.attachToAuthenticationFolder(status)
        }
    }


}
