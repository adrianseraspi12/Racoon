package com.suzei.racoon.ui.auth.forgotpassword.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.suzei.racoon.R
import com.suzei.racoon.databinding.DialogResetEmailSentBinding

class ResetEmailDialog : DialogFragment() {

    private var _binding: DialogResetEmailSentBinding? = null
    private val binding get() = _binding!!
    private val onContinueClick = View.OnClickListener { this.dismiss() }

    companion object {
        const val TAG = "ResetEmailDialog"

        fun newInstance(): ResetEmailDialog {
            return ResetEmailDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogResetEmailSentBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        binding.dialogResetEmailBtnContinue.setOnClickListener(onContinueClick)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}