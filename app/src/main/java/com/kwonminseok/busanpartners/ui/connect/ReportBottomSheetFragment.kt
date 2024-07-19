package com.kwonminseok.busanpartners.ui.connect

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.application.BusanPartners
import com.kwonminseok.busanpartners.data.Report
import com.kwonminseok.busanpartners.data.User
import com.kwonminseok.busanpartners.databinding.FragmentDemoSheetBinding
import com.kwonminseok.busanpartners.ui.login.SplashActivity
import com.kwonminseok.busanpartners.viewmodel.UserViewModel

private val TAG ="ReportBottomSheetFragment"
class ReportBottomSheetFragment : SuperBottomSheetFragment() {


    private var _binding: FragmentDemoSheetBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable("selectedUser")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentDemoSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reportButton.setOnClickListener {
            val selectedReasonId = binding.reasonRadioGroup.checkedRadioButtonId
            if (selectedReasonId != -1) {
                val selectedReason = binding.root.findViewById<RadioButton>(selectedReasonId).text.toString()
                user?.let {
                    submitReport(it.uid, selectedReason, it.introduction?.ko, it.chipGroup?.ko.toString())

                }
                setFragmentResult("blockUserRequest", bundleOf("reportedUserId" to user?.uid,
                    "userUniversity" to user?.college))
                dismiss()

//                val b = Bundle().apply {
//                    putString("studentUid", user!!.uid)
//                }

                findNavController().navigate(R.id.action_reportBottomSheetFragment_to_selectedUniversityStudentListFragment)
//                findNavController().navigate(R.id.action_reportBottomSheetFragment_to_connectFragment)
            } else {
                Toast.makeText(context, "신고 사유를 선택해주세요.", Toast.LENGTH_SHORT).show()
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
                timestamp = com.google.firebase.Timestamp.now()
            )

            reportsCollection.add(report)
                .addOnSuccessListener { documentReference ->
                    Log.e(TAG, "Report submitted with ID: ${documentReference.id}")
                    // 여기서 차단하는 기능 + selectedUniverSity로 이동하는 기능
                    if (isAdded) {
                        Log.e("isAdded", "yes")
                        findNavController().popBackStack()
                    }
                    Log.e("isAdded", "no")


                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error submitting report", e)
                }
        }
    }

}