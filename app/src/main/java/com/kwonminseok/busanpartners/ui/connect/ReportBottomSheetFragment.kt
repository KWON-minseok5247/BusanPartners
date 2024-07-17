package com.kwonminseok.busanpartners.ui.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.kwonminseok.busanpartners.R
import com.kwonminseok.busanpartners.databinding.FragmentDemoSheetBinding

class ReportBottomSheetFragment : SuperBottomSheetFragment() {

    private var _binding: FragmentDemoSheetBinding? = null
    private val binding get() = _binding!!

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
                val selectedReason = view.findViewById<RadioButton>(selectedReasonId).text.toString()
                Toast.makeText(context, "신고 사유: $selectedReason", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context, "신고 사유를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    override fun getPeekHeight(): Int {
//        return requireContext().resources.displayMetrics.heightPixels * 2 / 3
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
