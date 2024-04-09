package com.kwonminseok.busanpartners.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.repository.FirebaseUserRepository
import com.kwonminseok.busanpartners.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = "UserAccountViewModel"
@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: FirebaseUserRepository) : ViewModel() {
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user: StateFlow<Resource<User>> = _user

    private val _updateStatus = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val updateStatus: StateFlow<Resource<Boolean>> = _updateStatus

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            _user.value = Resource.Loading()
            _user.value = userRepository.getCurrentUser()
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
}
