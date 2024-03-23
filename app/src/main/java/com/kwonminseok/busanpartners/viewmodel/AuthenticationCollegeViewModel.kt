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
import com.kwonminseok.busanpartners.data.AuthenticationInformation
import com.kwonminseok.busanpartners.data.CheckAuthentication
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
class AuthenticationCollegeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {


    fun saveUserUniversity(university: String) {
        val userRef = firestore.collection("user").document(auth.uid!!)

        userRef.update("college", university)
            .addOnSuccessListener {
                Log.w("대학교", "정상적으로 수정되었습니다.")
            }.addOnFailureListener {
                Log.w("대학교", "실패했습니다. ${it.message}")
            }
    }

    fun attachToAuthenticationFolder() {
        firestore.collection("user").document(auth.uid!!)
            .get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                val checkAuthentication = CheckAuthentication(
                    user!!.uid,
                    user.authentication.studentIdentificationCard,
                    user.authentication.travelerAuthenticationImage
                )
                //TODO 여기 굉장히 이상한데 피곤해서 내일 하기로.
                firestore.collection("user").document(auth.uid!!).set(user.authentication.copy(authenticationStatus = "Loading"))
                    .addOnSuccessListener {
                        Log.w("authenticationStatus = Loading", "정상적으로 수정되었습니다.")

                    }.addOnFailureListener { Log.w("authenticationStatus = Loading 실패", "${it.message}.")
                    }


                firestore.collection("authentication").document(auth.uid!!).set(checkAuthentication)
                    .addOnSuccessListener {
                        Log.w("authentication 폴더에 등록", "정상적으로 수정되었습니다.")

                    }.addOnFailureListener {
                        Log.w("authentication 폴더에 등록 실패", "${it.message}")

                    }


            }.addOnFailureListener {

            }

    }


}
