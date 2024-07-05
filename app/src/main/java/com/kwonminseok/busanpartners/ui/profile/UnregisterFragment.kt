package com.kwonminseok.busanpartners.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alertview.lib.AlertView
import com.alertview.lib.OnItemClickListener
import com.barnea.dialoger.Dialoger
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kwonminseok.busanpartners.BuildConfig
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.application.BusanPartners.Companion.chatClient
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentProfileBinding
import com.kwonminseok.busanpartners.databinding.FragmentUnregisterBinding
import com.kwonminseok.busanpartners.extensions.toEntity
import com.kwonminseok.busanpartners.extensions.toUser
import com.kwonminseok.busanpartners.ui.login.LoginRegisterActivity
import com.kwonminseok.busanpartners.util.Constants
import com.kwonminseok.busanpartners.util.Resource
import com.kwonminseok.busanpartners.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Filters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter


private val TAG = "ProfileFragment"

@AndroidEntryPoint
class UnregisterFragment : Fragment() {

    private var _binding: FragmentUnregisterBinding? = null
    private val binding get() = _binding!!


    //    private val viewModel by viewModels<ProfileViewModel>()
    private val viewModel: UserViewModel by viewModels()
    lateinit var user: User
    private val uid = BusanPartners.preferences.getString("uid", "")
    private lateinit var sharedPreferences: SharedPreferences


    // 여기서 해야 할 거는 일단 room을 통해서 데이터를 가져오기 ->
    // 만약 room에 데이터가 없다면 네트워크로부터 데이터를 가져오기 -> 가져온 데이터를 insert하기
    // 만약 room의 데이터가 있으면 일단 그 데이터로 표시한다 -> 근데 네트워크로부터 가져온 데이터랑
    // 다르다면 네트워크 데이터로 최신화한다. -> 그리고 데이터를 insert한다.
    // 만약 room 데이터와 네트워크로부터 가져온 데이터가 동일하다면 굳이 업데이트는 필요 없음.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUnregisterBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        viewModel.getUserStateFlowData(uid).observe(viewLifecycleOwner) { userEntity ->
            // userEntity가 null이 아닐 때 UI 업데이트
            if (userEntity != null) {
                user = userEntity.toUser()
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radio_other) {
                binding.unregisterReason.visibility = View.VISIBLE
            } else {
                binding.unregisterReason.visibility = View.GONE
            }
        }

        binding.continueButton.setOnClickListener {
            AlertView.Builder()
                .setContext(requireActivity())
                .setStyle(AlertView.Style.Alert)
                .setTitle(getString(R.string.save_alert_title))
                .setMessage(getString(R.string.delete_account_message))
                .setDestructive(getString(R.string.confirmation))
                .setOthers(arrayOf(getString(R.string.cancel)))
                .setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(o: Any?, position: Int) {
                        if (position == 0) { // 확인 버튼 위치 확인
                            val selectedReason = when (binding.radioGroup.checkedRadioButtonId) {
                                R.id.radio_insufficient_info -> "이용이 불편합니다."
                                R.id.radio_concerns_data -> "개인 정보 등의 보안이 걱정됩니다."
                                R.id.radio_not_satisfied -> "매칭 시스템에 불만이 있습니다."
                                R.id.radio_too_many_emails -> "상대방으로부터 불편함을 겪었습니다."
                                R.id.radio_use_too_hard -> "사용하기에 너무 복잡합니다."
                                R.id.radio_other -> "기타(직접 입력)"
                                else -> ""
                            }

                            if (selectedReason.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.choose_delete_reason),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }

                            val detail = if (selectedReason == "기타(직접 입력)") {
                                binding.unregisterReason.text.toString()
                            } else {
                                null
                            }

                            // Reason 설정 후 계정 삭제 작업을 진행합니다.
                            CoroutineScope(Dispatchers.Main).launch {
                                viewModel.setDeleteReason(selectedReason, detail)
                                viewModel.updateStatus.collect { resource ->
                                    when (resource) {
                                        is Resource.Loading -> {
                                            // 로딩 인디케이터 표시
                                            Log.e("로딩", "로딩")
                                        }

                                        is Resource.Success -> {
                                            // 로딩 인디케이터 숨기기
                                            Log.e("성공", "성공")
                                            // 성공 메시지 표시 또는 성공 후 작업
                                            deleteAccount()
                                        }

                                        is Resource.Error -> {
                                            // 로딩 인디케이터 숨기기
                                            // 에러 메시지 표시
                                            Toast.makeText(
                                                requireContext(),
                                                "${resource.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        else -> Unit // Resource.Unspecified 처리
                                    }
                                }
                            }
                        } else {
                            (o as AlertView).dismiss() // 다른 버튼 클릭 시 AlertView 닫기
                        }
                    }

                })
                .build()
                .setCancelable(true)
                .show()

        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }



        // 선택된 이유를 처리하는 로직 추가
        // 예: Firebase에 저장


//        binding.continueButton.setOnClickListener {
//            AlertDialog.Builder(requireContext())
//                .setTitle("계정이 탈퇴됩니다.")
//                .setMessage("정말로 계정을 삭제하시겠습니까?")
//                .setPositiveButton("Yes") { dialog, _ ->
//                    dialog.dismiss()
//                    BusanPartners.preferences.setString("uid", "")
//                    BusanPartners.preferences.setString(Constants.TOKEN, "")
//
//                    val editor = sharedPreferences.edit()
//                    editor.clear()
//                    editor.apply()
//
//
//                    val firebaseUser = FirebaseAuth.getInstance().currentUser
//                    val googleSignInClient = GoogleSignIn.getClient(
//                        requireActivity(),
//                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                            .requestIdToken(getString(R.string.default_web_client_id))
//                            .build()
//                    )
//
//                    googleSignInClient.silentSignIn().addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val googleSignInAccount = task.result
//                            val idToken = googleSignInAccount?.idToken
//
//                            if (idToken != null) {
//                                val credential = GoogleAuthProvider.getCredential(idToken, null)
//
//                                firebaseUser?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
//                                    if (reauthTask.isSuccessful) {
//                                        CoroutineScope(Dispatchers.IO).launch {
//                                            try {
//                                                // 비동기 작업을 병렬로 실행
//                                                val deleteUserFromChannels = async {
//                                                    val filter = Filters.eq("members", user.uid)
//                                                    val queryChannelsRequest = QueryChannelsRequest(filter, 0, 10)
//                                                    val channelResult = chatClient.queryChannels(queryChannelsRequest).execute()
//
//                                                    if (channelResult.isSuccess) {
//                                                        val channels = channelResult.getOrNull()
//                                                        if (channels != null) {
//                                                            for (channel in channels) {
//                                                                chatClient.removeMembers("messaging", channel.cid, listOf(uid)).execute()
//                                                            }
//                                                        }
//                                                    }
//                                                }
//
//                                                val deleteUserFromRoom = async {
//                                                    viewModel.deleteUser(user.toEntity())
//                                                }
//
//                                                val deleteUserFromFirebase = async {
//                                                    viewModel.deleteCurrentUser()
//                                                }
//
//                                                // 모든 작업이 완료될 때까지 기다림
//                                                awaitAll(deleteUserFromChannels, deleteUserFromRoom, deleteUserFromFirebase)
//
//                                                withContext(Dispatchers.Main) {
//                                                    // Firebase 삭제 결과 처리
//                                                    val result = deleteUserFromFirebase.await()
//                                                    if (result is Resource.Success) {
//                                                        googleSignInClient.revokeAccess().addOnCompleteListener {
//                                                            if (it.isSuccessful) {
//                                                                Toast.makeText(requireContext(), "계정이 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                                                                val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
//                                                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                                                startActivity(intent)
//                                                            } else {
//                                                                Log.e("Google Sign-In access revoke", "Failed")
//                                                                Toast.makeText(requireContext(), "Google Sign-In access revoke failed.", Toast.LENGTH_SHORT).show()
//                                                            }
//                                                        }
//                                                    } else {
//                                                        Log.e("계정 삭제에 실패했습니다", "Failure: ${result.message}")
//                                                        Toast.makeText(requireContext(), "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                                                    }
//                                                }
//                                            } catch (e: Exception) {
//                                                Log.e("Delete Account", "Exception: ${e.message}")
//                                                withContext(Dispatchers.Main) {
//                                                    Toast.makeText(requireContext(), "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        Log.e("Reauthentication", "Failed: ${reauthTask.exception?.message}")
//                                        Toast.makeText(requireContext(), "재인증에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            } else {
//                                Log.e("Google Sign-In", "ID Token is null")
//                                Toast.makeText(requireContext(), "구글 로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            Log.e("Google Sign-In", "Silent sign-in failed: ${task.exception?.message}")
//                            Toast.makeText(requireContext(), "구글 로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                .setNegativeButton("No") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun deleteAccount() {

        val dialog = Dialoger(requireContext(), Dialoger.TYPE_LOADING)
            .setTitle(getString(R.string.deleting_account))
            .setDescription(getString(R.string.please_wait_for_delete))
            .setProgressBarColor(R.color.jakarta)
            .show()

// Dismiss the loading dialog after 5 seconds
        BusanPartners.preferences.setString("uid", "")
        BusanPartners.preferences.setString(Constants.TOKEN, "")

        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()


        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val googleSignInClient = GoogleSignIn.getClient(
            requireActivity(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.DEFAULT_WEB_CLIENT_ID)
                .build()
        )

        googleSignInClient.silentSignIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val googleSignInAccount = task.result
                val idToken = googleSignInAccount?.idToken

                if (idToken != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)

                    firebaseUser?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    // 비동기 작업을 병렬로 실행
                                    val deleteUserFromChannels = async {
                                        val filter = Filters.eq("members", user.uid)
                                        val queryChannelsRequest =
                                            QueryChannelsRequest(filter, 0, 10)
                                        val channelResult =
                                            chatClient.queryChannels(queryChannelsRequest).execute()

                                        if (channelResult.isSuccess) {
                                            val channels = channelResult.getOrNull()
                                            if (channels != null) {
                                                for (channel in channels) {
                                                    chatClient.removeMembers(
                                                        "messaging",
                                                        channel.cid,
                                                        listOf(uid)
                                                    ).execute()
                                                }
                                            }
                                        }
                                    }

                                    val deleteUserFromRoom = async {
                                        viewModel.deleteUser(user.toEntity())
                                    }

                                    val deleteUserFromFirebase = async {
                                        viewModel.deleteCurrentUser()
                                    }

                                    // 모든 작업이 완료될 때까지 기다림
                                    awaitAll(
                                        deleteUserFromChannels,
                                        deleteUserFromRoom,
                                        deleteUserFromFirebase
                                    )

                                    withContext(Dispatchers.Main) {
                                        // Firebase 삭제 결과 처리
                                        val result = deleteUserFromFirebase.await()
                                        dialog.dismiss() // 로딩 다이얼로그 닫기
                                        if (result is Resource.Success) {
                                            googleSignInClient.revokeAccess()
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        Dialoger(requireContext(), Dialoger.TYPE_MESSAGE)
                                                            .setTitle(getString(R.string.account_deleted))
                                                            .setDescription(getString(R.string.returning_to_main))
                                                            .setButtonText(getString(R.string.confirmation))
                                                            .setButtonOnClickListener {
                                                                val intent = Intent(
                                                                    requireContext(),
                                                                    LoginRegisterActivity::class.java
                                                                )
                                                                intent.flags =
                                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                startActivity(intent)
                                                            }
                                                            .show()

                                                        Toast.makeText(
                                                            requireContext(),
                                                            getString(R.string.account_deleted),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Log.e(
                                                            "Google Sign-In access revoke",
                                                            "Failed"
                                                        )
                                                        Toast.makeText(
                                                            requireContext(),
                                                            getString(R.string.account_deletion_failed),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        } else {
                                            Log.e("계정 삭제에 실패했습니다", "Failure: ${result.message}")
                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.account_deletion_failed),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                                } catch (e: Exception) {
                                    Log.e("Delete Account", "Exception: ${e.message}")
                                    withContext(Dispatchers.Main) {
                                        dialog.dismiss() // 로딩 다이얼로그 닫기
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.account_deletion_failed),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }
                        } else {
                            Log.e("Reauthentication", "Failed: ${reauthTask.exception?.message}")
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.reauth_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss() // 로딩 다이얼로그 닫기

                        }
                    }
                } else {
                    Log.e("Google Sign-In", "ID Token is null")
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.google_login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss() // 로딩 다이얼로그 닫기

                }
            } else {
                Log.e("Google Sign-In", "Silent sign-in failed: ${task.exception?.message}")
                Toast.makeText(
                    requireContext(),
                    getString(R.string.google_login_failed),
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss() // 로딩 다이얼로그 닫기

            }
        }
    }


}