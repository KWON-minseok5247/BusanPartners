package com.kwonminseok.busanpartners.util

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kwonminseok.busanpartners.R

// 앞에 Fragment를 붙이면 다양한 프래그먼트에서 중복입력할 필요 없이 사용이 가능한 듯?
// 그니까 onSendClick을 사용하는 이유는 setupBottomDialog를 여러개 쓸 건데, Dialog에 여러 내용을 띄우거나 로직을 부여해야 한다.
// 그래서 onSendClick은 그 중의 일부이며 Login에서 forgot에서 이메일을 입력 후 send를 누르면 onSendClick(사실상 f라고 봐도 무방)
// 이 실행된다. 그래서 loginfragment에서 아래와 같은 형식으로 나왔다.

//        binding.tvForgotPasswordLogin.setOnClickListener {
//            setupBottomSheetDialog {email ->
//                viewModel.resetPassword(email)
//            }
//        }

// 이걸 봤을 때에 setupBottomSheetDialog는 email을 사용을 했을 때 onSendClick(사실상 f라고 봐도 무방)에 resetPassword를 대입한다고 생각하면 됨.
// 그래서 forgot 버튼을 눌렀을 때 dialog가 실행이 되면서 동시에 send를 클릭하면 resetPassword를 실행할 수 있도록 도와주는 역할인 것 같다.

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password, null)
    dialog.setContentView(view)
    // 위에 dialog에 style넣고 아래 behavior expanded를 함으로써 키보드로 다이얼로그가 가려지지 않는 결과가 만들어짐.
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonSendResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancelResetPassword)

    buttonSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email) // 미지의 함수 -> 다른 뷰모델에 직접 추가해서 써야 하는 듯?
        dialog.dismiss() // dismiss는 종료
    }

    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }

}