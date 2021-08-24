package com.suzei.racoon.ui.auth.login

import android.text.TextUtils
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.suzei.racoon.data.auth.AuthRepository
import com.suzei.racoon.data.user.UserRepository
import com.suzei.racoon.data.user.UserRepository.OnSaveListener
import java.util.*

class LoginPresenter internal constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val mLoginView: LoginContract.View
) : LoginContract.Presenter, AuthRepository.Listener {
    override fun loginUser(email: String?, password: String?) {
        if (!hasError(email, password)) {
            mLoginView.showProgress()
            authRepository.executeLogin(email, password, this)
        }
    }

    private fun hasError(email: String?, password: String?): Boolean {
        if (TextUtils.isEmpty(email)) {
            mLoginView.onLoginFailure("Email is required")
            return true
        }
        if (TextUtils.isEmpty(password)) {
            mLoginView.onLoginFailure("Password is required")
            return true
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mLoginView.onLoginFailure("Email is invalid")
            return true
        }
        return false
    }

    override fun onAuthSuccess(uid: String) {
        val deviceToken = FirebaseInstanceId.getInstance().token
        val userMap: MutableMap<String, Any?> = HashMap()
        userMap["device_token"] = deviceToken
        userRepository.saveUserDetails(uid, userMap, object : OnSaveListener {
            override fun onSuccess() {
                mLoginView.hideProgress()
                mLoginView.onLoginSuccess()
            }

            override fun onFailure(e: Exception) {
                mLoginView.hideProgress()
                FirebaseAuth.getInstance().signOut()
                mLoginView.onLoginFailure("Can't connect to server, Please login again")
            }
        })
    }

    override fun onAuthFailure(e: Exception) {
        mLoginView.onLoginFailure(e.message)
    }

    init {
        mLoginView.setPresenter(this)
    }
}