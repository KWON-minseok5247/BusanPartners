package com.kwonminseok.busanpartners.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentLoginBinding
import com.kwonminseok.busanpartners.ui.HomeActivity
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.util.setupBottomSheetDialog
import com.kwonminseok.busanpartners.viewmodel.LoginsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginsViewModel>()

    lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)

        // Google SignIn Options, 로그인하는 인스턴스를 생성하는 과정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Firebase에서 제공하는 웹 클라이언트 ID
            .requestEmail()
            .build()

        // Google SignIn Client, GoogleSignInClient는 Google 로그인 프로세스를 관리하는 주요 클래스
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // 자동 로그인 과정 -> 실제로는 splash 스크린에서 사용해야 할 것!
//        val user = FirebaseAuth.getInstance().currentUser
//        user?.getIdToken(true)?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val idToken = task.result.token
//                Log.d(TAG, "아이디 토큰 = $idToken")
//                val intent =
//                    Intent(requireContext(), HomeActivity::class.java).addFlags(
//                        Intent.FLAG_ACTIVITY_NEW_TASK or
//                                Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    )
//                startActivity(intent)
//            }
//        }


        binding.apply {
            cirLoginButton.setOnClickListener {
                val email = editTextEmail.text.toString().trim()
                val password = editTextPassword.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.forgotPassword.setOnClickListener {
            setupBottomSheetDialog { email ->
                if (!email.isNullOrEmpty()) {
                    viewModel.resetPasswordFun(email)
                } else {
                    Snackbar.make(requireView(), "email is empty", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

//        val signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            .build()
//





        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {

                    }

                    is Resource.Error -> {

                    }

                    else -> Unit
                }
            }
        }
// 12-05 임시 삭제
//        binding.tvForgotPasswordLogin.setOnClickListener {
//            setupBottomSheetDialog {email ->
//                // email이 입력하는 부분임
//                viewModel.resetPassword(email)
//
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.cirLoginButton.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.cirLoginButton.revertAnimation()

                        val intent =
                            Intent(context, HomeActivity::class.java).addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                        startActivity(intent)
                    }

                    is Resource.Error -> {
                        binding.cirLoginButton.revertAnimation()
                        Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT)
                            .show()

                    }

                    else -> {

                    }
                }
            }
        }


        binding.googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }


    }
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // 구글 intent로부터 받아온 로그인 결과
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            //Google 로그인의 결과를 처리하는 메소드. 이 메소드는 성공적으로 로그인이 완료되었는지,
            // 사용자 정보를 가져올 수 있는지 등을 확인하고,
            // 이후의 작업(예: Firebase를 통한 인증, 사용자 정보 화면으로의 이동 등)을 진행
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            // Google Sign In was successful, authenticate with Firebase
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            // ...
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    val user = FirebaseAuth.getInstance().currentUser
//
//                    val userData = User("","", email = user?.email!!, uid = user.uid, name = user.displayName, imagePath = user.photoUrl.toString() ?: "")
//
//                    viewModel.saveUserToDatabase(userData)
//
//                    // 여기서 HomeActivity로 이동하거나 UI 업데이트
//                    val intent =
//                        Intent(context, HomeActivity::class.java).addFlags(
//                            Intent.FLAG_ACTIVITY_NEW_TASK or
//                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        )
//                    startActivity(intent)

                    val currentUser = FirebaseAuth.getInstance().currentUser

                    val usersRef = FirebaseFirestore.getInstance().collection("user")
                    Log.e("usersRef", usersRef.toString())
                    currentUser?.let {user ->
                        val uid = user.uid

                        usersRef.document(uid).get().addOnSuccessListener { documentSnapshot ->
                            if (!documentSnapshot.exists()) {
                                // 사용자 데이터가 존재하지 않는 경우, 새로운 사용자 데이터 저장
                                val userData = User("","", email = user.email!!, uid = user.uid, name = user.displayName, imagePath = user.photoUrl.toString() ?: "")

                                usersRef.document(uid).set(userData)
                                    .addOnSuccessListener {
                                        // 사용자 데이터 저장 성공
                                        val intent =
                                            Intent(context, HomeActivity::class.java).addFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            )
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        // 사용자 데이터 저장 실패
                                        Log.e(TAG, "Error writing document", e)
                                    }
                            } else {
                                // 사용자 데이터가 이미 존재하는 경우, 바로 홈 화면으로 이동
                                val intent =
                                    Intent(context, HomeActivity::class.java).addFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    )
                                startActivity(intent)
                            }
                        }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error accessing the database", e)
                            }
                    }
                    } else {
                    // If sign in fails, display a message to the user.
                    // 실패 처리...
                }

//                    currentUser?.let { user ->
//                        val uid = user.uid
//
//                        // 데이터베이스에서 사용자 UID로 검색
//                        usersRef.child(uid).addListenerForSingleValueEvent(object :
//                            ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                if (!snapshot.exists()) {
//                                    // 사용자 데이터가 존재하지 않는 경우, 새로운 사용자 데이터 저장
//                                    val userData = User("", "", email = user.email!!, uid = uid, name = user.displayName, imagePath = user.photoUrl.toString() ?: "")
//                                    viewModel.saveUserToDatabase(userData)
//                                }
//                                // 데이터가 이미 존재하거나 새로 저장된 후의 처리
//                                val intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                                startActivity(intent)
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                // 데이터베이스 에러 처리
//                            }
//                        })
//                    }
                }
            }


    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "GoogleSignIn"
    }


}
