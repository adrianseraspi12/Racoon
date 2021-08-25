package com.suzei.racoon.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.suzei.racoon.databinding.FragmentSignUpBinding
import com.suzei.racoon.ui.base.MainActivity
import com.suzei.racoon.view.DelayedProgressDialog

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment(), SignUpContract.View {
    private var presenter: SignUpContract.Presenter? = null
    private var dpd: DelayedProgressDialog? = null

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): SignUpFragment {
            return SignUpFragment()
        }
    }

    var onChatNowClick = View.OnClickListener {
        val email = binding.signupEmail.text.toString()
        val password = binding.signupPassword.text.toString()
        val displayName = binding.signupName.text.toString()
        presenter!!.createAccount(email, password, displayName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        dpd = DelayedProgressDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupChatNow.setOnClickListener(onChatNowClick)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setPresenter(presenter: SignUpContract.Presenter?) {
        this.presenter = presenter
    }

    override fun onRegisterSuccess() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onRegisterFailure(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        dpd!!.show(childFragmentManager, "Progress Dialog")
    }

    override fun hideProgress() {
        dpd!!.dismiss()
    }
}