package com.suzei.racoon.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.suzei.racoon.R
import com.suzei.racoon.databinding.FragmentLoginBinding
import com.suzei.racoon.ui.auth.forgotpassword.ForgotPasswordActivity
import com.suzei.racoon.ui.auth.signup.SignUpActivity
import com.suzei.racoon.ui.base.MainActivity
import com.suzei.racoon.view.DelayedProgressDialog

class LoginFragment : Fragment(), LoginContract.View {
    private var presenter: LoginContract.Presenter? = null
    private var dpd: DelayedProgressDialog? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    var onLoginButtonClick = View.OnClickListener {
        val email = binding.loginEmailInput.text.toString()
        val password = binding.loginPasswordInput.text.toString()
        this.presenter!!.loginUser(email, password)
    }

    var onSignUpButtonClick = View.OnClickListener {
        startActivity(Intent(context, SignUpActivity::class.java))
    }

    var onForgotPasswordClick = View.OnClickListener {
        startActivity(Intent(context, ForgotPasswordActivity::class.java))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        dpd = DelayedProgressDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        setupButtonClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupButtonClick() {
        binding.loginButton.setOnClickListener(onLoginButtonClick)
        binding.registerButton.setOnClickListener(onSignUpButtonClick)
        binding.forgotPasswordButton.setOnClickListener(onForgotPasswordClick)
    }

    override fun setPresenter(presenter: LoginContract.Presenter?) {
        this.presenter = presenter
    }

    override fun onLoginSuccess() {
        startActivity(Intent(context, MainActivity::class.java))
    }

    override fun onLoginFailure(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        dpd!!.show(fragmentManager!!, "Progress Dialog")
    }

    override fun hideProgress() {
        dpd!!.dismiss()
    }
}