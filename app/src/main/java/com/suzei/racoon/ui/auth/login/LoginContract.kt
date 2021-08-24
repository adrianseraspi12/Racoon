package com.suzei.racoon.ui.auth.login

import com.suzei.racoon.ui.base.Contract.ProgressView

interface LoginContract {
    interface View : ProgressView {
        fun setPresenter(presenter: Presenter?)
        fun onLoginSuccess()
        fun onLoginFailure(message: String?)
    }

    interface Presenter {
        fun loginUser(email: String?, password: String?)
    }
}