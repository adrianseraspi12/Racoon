package com.suzei.racoon.ui.auth.forgotpassword

import android.text.TextUtils
import android.util.Patterns
import com.suzei.racoon.data.auth.AuthRepository
import com.suzei.racoon.data.auth.AuthRepository.ResetPasswordListener

class ForgotPasswordPresenter internal constructor(
    private val authRepository: AuthRepository,
    private val mView: ForgotPasswordContract.View
) : ForgotPasswordContract.Presenter {
    override fun resetPassword(email: String) {

        if (!hasErrors(email)) {
            mView.showProgress()

            authRepository.executeResetPassword(email, object : ResetPasswordListener {
                override fun onResetPasswordSuccess() {
                    mView.hideProgress()
                    mView.onResetPasswordSuccess()
                }

                override fun onResetPasswordFailure(e: Exception) {
                    mView.hideProgress()
                    mView.onResetPasswordFailure(
                        e.message ?: "Something went wrong. Please try again."
                    )
                }
            })
        }
    }

    private fun hasErrors(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            mView.onResetPasswordFailure("Email is required")
            return true
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mView.onResetPasswordFailure("Email is invalid")
            return true
        }
        return false
    }

    init {
        mView.setPresenter(this)
    }
}