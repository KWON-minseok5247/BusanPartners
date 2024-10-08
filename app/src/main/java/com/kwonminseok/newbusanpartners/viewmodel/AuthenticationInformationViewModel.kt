package com.kwonminseok.newbusanpartners.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.newbusanpartners.data.CheckAuthentication
import com.kwonminseok.newbusanpartners.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationInformationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {


    fun saveUserUniversity(university: String, email: String) {
        val userRef = firestore.collection("user").document(auth.uid!!)

        val userUpdates = mapOf(
            "college" to university,
            "universityEmail" to email
        )
        userRef.update(userUpdates)
            .addOnSuccessListener {
                Log.w("대학교", "정상적으로 수정되었습니다.")
            }.addOnFailureListener {
                Log.w("대학교", "실패했습니다. ${it.message}")
            }
    }

    fun attachToAuthenticationFolder(status: String) {
        firestore.collection("user").document(auth.uid!!)
            .get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                val checkAuthentication = CheckAuthentication(
                    user!!.uid,
                    user.authentication.studentIdentificationCard,
                    user.authentication.travelerAuthenticationImage,
                    user.universityEmail,
                    user.college
                )

                firestore.collection("user").document(auth.uid!!).update("authentication.authenticationStatus", "loading")
                    .addOnSuccessListener {
                        Log.w("authenticationStatus = Loading", "정상적으로 수정되었습니다.")

                    }.addOnFailureListener { Log.w("authenticationStatus = Loading 실패", "${it.message}.")

                    }
                // 대학생일 때
                if (status == "student") {
                    firestore.collection("universityAuthentication").document(auth.uid!!).set(checkAuthentication)
                        .addOnSuccessListener {
                            Log.w("authentication 폴더에 등록", "정상적으로 수정되었습니다.")

                        }.addOnFailureListener {
                            Log.w("authentication 폴더에 등록 실패", "${it.message}")

                        }
                } else { // 관광객일 때
                    firestore.collection("travelerAuthentication").document(auth.uid!!).set(checkAuthentication)
                        .addOnSuccessListener {
                            Log.w("authentication 폴더에 등록", "정상적으로 수정되었습니다.")

                        }.addOnFailureListener {
                            Log.w("authentication 폴더에 등록 실패", "${it.message}")

                        }
                }





            }.addOnFailureListener {
                Log.w("DataRef 획득 실패", "${it.message}")

            }

    }

    fun attachToTravelerAuthenticationFolder() {
        firestore.collection("user").document(auth.uid!!)
            .get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                val checkAuthentication = CheckAuthentication(
                    user!!.uid,
                    user.authentication.studentIdentificationCard,
                    user.authentication.travelerAuthenticationImage,
                    user.universityEmail,
                    user.college
                )

                firestore.collection("user").document(auth.uid!!).update("authentication.authenticationStatus", "loading")
                    .addOnSuccessListener {
                        Log.w("authenticationStatus = Loading", "정상적으로 수정되었습니다.")

                    }.addOnFailureListener { Log.w("authenticationStatus = Loading 실패", "${it.message}.")
                    }





            }.addOnFailureListener {

            }

    }


}
