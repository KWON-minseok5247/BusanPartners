package com.kwonminseok.newbusanpartners.ui.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alertview.lib.AlertView
import com.alertview.lib.OnItemClickListener
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.newbusanpartners.R
import com.kwonminseok.newbusanpartners.application.BusanPartners
import com.kwonminseok.newbusanpartners.data.Report
import com.kwonminseok.newbusanpartners.data.User
import com.kwonminseok.newbusanpartners.databinding.FragmentDemoSheetBinding
import com.kwonminseok.newbusanpartners.databinding.FragmentMessageReportSheetBinding
import com.kwonminseok.newbusanpartners.ui.login.SplashActivity
import com.kwonminseok.newbusanpartners.viewmodel.UserViewModel

private val TAG = "MessageReportBottomSheetFragment"

class MessageReportBottomSheetFragment : SuperBottomSheetFragment() {


    private var _binding: FragmentMessageReportSheetBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null

    private lateinit var channelId: String
    private lateinit var channelType: String
    private var otherPerson: String? = null
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            channelId =
                it.getString("channelId") ?: throw IllegalArgumentException("Channel ID not found")
            channelType = it.getString("channelType")
                ?: throw IllegalArgumentException("Channel type not found")
            otherPerson = it.getString("otherPerson") // null일 수 있으므로 nullable로 처리
        }

//        arguments?.let {
//            user = it.getParcelable("selectedUser")
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMessageReportSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.reportButton.setOnClickListener {
//
////            AlertView.Builder()
////                .setContext(requireActivity())
////                .setStyle(AlertView.Style.Alert)
////                .setTitle(getString(R.string.block_alert_title))
////                .setMessage(getString(R.string.block_alert_message))
////                .setDestructive(getString(R.string.confirmation))
////                .setOthers(arrayOf(getString(R.string.cancel)))
////                .setOnItemClickListener(object : OnItemClickListener {
////                    override fun onItemClick(o: Any?, position: Int) {
////                        if (position == 0) { // 확인 버튼 위치 확인
////
////                        } else {
////                            (o as AlertView).dismiss() // 다른 버튼 클릭 시 AlertView 닫기
////                        }
////                    }
////
////                })
////                .build()
////                .setCancelable(true)
////                .show()
//
//
//
//
//
//            val selectedReasonId = binding.reasonRadioGroup.checkedRadioButtonId
//            if (selectedReasonId != -1) {
//                val selectedReason = binding.root.findViewById<RadioButton>(selectedReasonId).text.toString()
//                user?.let {
//                    submitReport(it.uid, selectedReason, it.introduction?.ko, it.chipGroup?.ko.toString())
//
//                }
//                setFragmentResult("blockUserRequest", bundleOf(
//                    "reportedUserId" to user?.uid,
//                    "userUniversity" to user?.college
//                )
//                )
//                dismiss()
//
////                val b = Bundle().apply {
////                    putString("studentUid", user!!.uid)
////                }
//                BusanPartners.chatClient.channel("${channelType}:${channelId}")
//                    .hide(true).enqueue { hideResult ->
//                        if (hideResult.isSuccess) {
//                            if (otherPerson != null) {
//                                val banList = SplashActivity.currentUser?.banList?.toMutableList() ?: mutableListOf()
//                                if (!banList.contains(otherPerson)) {
//                                    banList.add(otherPerson!!)
//                                }
//
//                                SplashActivity.currentUser = SplashActivity.currentUser?.copy(blockList = banList)
//                                if (isAdded) {
//                                    userViewModel.updateUser(SplashActivity.currentUser!!)
//                                    userViewModel.setCurrentUser(
//                                        mapOf(
//                                            "banList" to banList
//                                        )
//                                    )
//                                }
//
//
//                                Toast.makeText(requireContext(), getString(R.string.block_success), Toast.LENGTH_SHORT).show()
//
//                            } else {
//                                Log.e("BanUser", "No valid user found to ban.")
//                            }
//
//
//                        } else {
//                            Log.e(
//                                "채널 숨기기 실패",
//                                hideResult.errorOrNull()?.message ?: "알 수 없는 오류"
//                            )
//                        }
//                    }
//
////                findNavController().navigate(R.id.action_messageReportBottomSheetFragment_to_messageFragment)
////                findNavController().navigate(R.id.action_reportBottomSheetFragment_to_connectFragment)
//            } else {
//                Toast.makeText(
//                    context,
//                    getString(R.string.select_report_reason),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }

        binding.reportButton.setOnClickListener {
            val selectedReasonId = binding.reasonRadioGroup.checkedRadioButtonId
            if (selectedReasonId != -1) {
                val selectedReason =
                    binding.root.findViewById<RadioButton>(selectedReasonId).text.toString()
                user?.let {
                    submitReport(
                        it.uid,
                        selectedReason,
                        it.introduction?.ko,
                        it.chipGroup?.ko.toString()
                    )
                }

                setFragmentResult(
                    "blockUserRequest", bundleOf(
                        "reportedUserId" to user?.uid,
                        "userUniversity" to user?.college
                    )
                )

                BusanPartners.chatClient.channel("${channelType}:${channelId}")
                    .hide(true).enqueue { hideResult ->
                        if (hideResult.isSuccess) {
                            if (otherPerson != null) {
                                val banList = SplashActivity.currentUser?.banList?.toMutableList()
                                    ?: mutableListOf()
                                if (!banList.contains(otherPerson)) {
                                    banList.add(otherPerson!!)
                                }

                                SplashActivity.currentUser =
                                    SplashActivity.currentUser?.copy(blockList = banList)

                                // Fragment가 아직 Activity에 연결되어 있는지 확인
                                userViewModel.updateUser(SplashActivity.currentUser!!)
                                userViewModel.setCurrentUser(
                                    mapOf(
                                        "banList" to banList
                                    )
                                )
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.block_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismiss()

                            } else {
                                Log.e("BanUser", "No valid user found to ban.")
                                BusanPartners.chatClient.channel("${channelType}:${channelId}")
                                    .delete().enqueue()

                                    Toast.makeText(
                                    requireContext(),
                                    getString(R.string.block_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismiss()
                            }

                        } else {
                            Log.e(
                                "채널 숨기기 실패",
                                hideResult.errorOrNull()?.message ?: "알 수 없는 오류"
                            )
                        }
                    }

            } else {
                Toast.makeText(
                    context,
                    getString(R.string.select_report_reason),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun getPeekHeight(): Int {
        return 1300 // Adjust this value as needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun animateStatusBar(): Boolean {
        return false
    }

    fun submitReport(reportedUserId: String, reason: String, details: String?, chipGroup: String?) {
        val db = FirebaseFirestore.getInstance()
        val reportsCollection = db.collection("reports")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val report = Report(
                reportedBy = currentUser.uid,
                reportedUser = reportedUserId,
                reason = reason,
                details = details,
                chipGroup = chipGroup,
                timestamp = Timestamp.now()
            )

            reportsCollection.add(report)
                .addOnSuccessListener { documentReference ->
                    Log.e(
                        TAG,
                        "Report submitted with ID: ${documentReference.id}"
                    )
                    // 여기서 차단하는 기능 + selectedUniverSity로 이동하는 기능
                    if (isAdded) {
                        Log.e("isAdded", "yes")
                        findNavController().popBackStack()
                    }
                    Log.e("isAdded", "no")


                }
                .addOnFailureListener { e ->
                    Log.e(
                        TAG,
                        "Error submitting report",
                        e
                    )
                }
        }
    }

}