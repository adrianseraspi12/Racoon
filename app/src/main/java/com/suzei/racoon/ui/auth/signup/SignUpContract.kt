package com.suzei.racoon.ui.auth.signup

import com.suzei.racoon.ui.base.Contract.ProgressView

interface SignUpContract {
    interface View : ProgressView {
        fun setPresenter(presenter: Presenter?)
        fun onRegisterSuccess()
        fun onRegisterFailure(message: String?)
    }

    interface Presenter {
        fun createAccount(email: String?, password: String?, name: String?)
    }
}