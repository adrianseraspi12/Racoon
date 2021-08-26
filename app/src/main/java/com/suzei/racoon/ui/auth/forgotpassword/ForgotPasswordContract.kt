package com.suzei.racoon.ui.auth.forgotpassword

import com.suzei.racoon.ui.base.Contract.ProgressView

interface ForgotPasswordContract {

    interface View : ProgressView {
        fun setPresenter(presenter: Presenter)
        fun onResetPasswordSuccess()
        fun onResetPasswordFailure(message: String)
    }

    interface Presenter {
        fun resetPassword(email: String)
    }
}