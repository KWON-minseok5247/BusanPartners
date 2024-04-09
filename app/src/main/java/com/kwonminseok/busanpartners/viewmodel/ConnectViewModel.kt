package com.kwonminseok.busanpartners.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private val TAG = "ConnectViewModel"
@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<MutableList<User>>>(Resource.Unspecified())
    val user = _user.asStateFlow()


    init {
        getStudents()
    }


 private fun getStudents() {

        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("user")
        .whereEqualTo("authentication.collegeStudent", true)
        .whereEqualTo("wantToMeet", true)
        .get()
        .addOnSuccessListener { documents ->
            val userList =documents.toObjects(User::class.java)
            viewModelScope.launch {
                _user.emit(Resource.Success(userList))
            }
        }
        .addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }


}

}