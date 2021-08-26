package com.suzei.racoon.ui.auth.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.suzei.racoon.databinding.FragmentForgotPasswordBinding
import com.suzei.racoon.view.DelayedProgressDialog

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : Fragment(), ForgotPasswordContract.View {
    private var dpd: DelayedProgressDialog? = null
    private var presenter: ForgotPasswordContract.Presenter? = null

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    var onResetClick = View.OnClickListener {
        val email = binding.forgotPasswordEmail.text.toString()
        presenter!!.resetPassword(email)
    }

    companion object {
        fun newInstance(): ForgotPasswordFragment {
            return ForgotPasswordFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        dpd = DelayedProgressDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forgotPasswordReset.setOnClickListener(onResetClick)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setPresenter(presenter: ForgotPasswordContract.Presenter) {
        this.presenter = presenter
    }

    override fun onResetPasswordSuccess() {
        Toast.makeText(
            context,
            "We have sent you an email to reset your password, " +
                    "Please follow the instruction",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResetPasswordFailure(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        dpd!!.show(childFragmentManager, "Progress Dialog")
    }

    override fun hideProgress() {
        dpd!!.dismiss()
    }
}