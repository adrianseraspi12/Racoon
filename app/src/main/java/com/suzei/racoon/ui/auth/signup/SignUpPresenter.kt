package com.suzei.racoon.ui.auth.signup

import android.text.TextUtils
import com.google.firebase.iid.FirebaseInstanceId
import com.suzei.racoon.data.auth.AuthRepository
import com.suzei.racoon.data.user.UserRepository
import com.suzei.racoon.data.user.UserRepository.OnSaveListener

class SignUpPresenter internal constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val mView: SignUpContract.View
) : SignUpContract.Presenter {
    override fun createAccount(email: String?, password: String?, name: String?) {
        if (!hasError(email, password, name)) {
            mView.showProgress()
            authRepository.executeRegister(email, password, object : AuthRepository.Listener {
                override fun onAuthSuccess(uid: String) {
                    val deviceToken = FirebaseInstanceId.getInstance().token
                    val detailsMap: MutableMap<String?, Any?> = mutableMapOf()
                    detailsMap["age"] = 18
                    detailsMap["bio"] = ""
                    detailsMap["gender"] = "Unknown"
                    detailsMap["image"] = DEFAULT_IMAGE
                    detailsMap["name"] = name
                    detailsMap["online"] = true
                    detailsMap["device_token"] = deviceToken
                    saveUserDetails(uid, detailsMap)
                }

                override fun onAuthFailure(e: Exception) {}
            })
        }
    }

    private fun saveUserDetails(
        uid: String,
        detailsMap: Map<String?, Any?>
    ) {
        userRepository.saveUserDetails(uid, detailsMap, object : OnSaveListener {
            override fun onSuccess() {
                mView.hideProgress()
                mView.onRegisterSuccess()
            }

            override fun onFailure(e: Exception) {
                mView.hideProgress()
                mView.onRegisterFailure("Can't create user details, Please login")
            }
        })
    }

    private fun hasError(email: String?, password: String?, name: String?): Boolean {
        if (TextUtils.isEmpty(email)) {
            mView.onRegisterFailure("Email is required")
            return true
        }
        if (TextUtils.isEmpty(password)) {
            mView.onRegisterFailure("Password is required")
            return true
        }
        if (TextUtils.isEmpty(name)) {
            mView.onRegisterFailure("Display name is required")
            return true
        }
        return false
    }

    companion object {
        private const val DEFAULT_IMAGE =
            "https://firebasestorage.googleapis.com/v0/b/racoon-89ee8.appspot.com/o/emoji%2Fhappy_512px.png?alt=media&amp;token=11c342c5-1896-4e04-a822-c76536cf5d1c"
    }

    init {
        mView.setPresenter(this)
    }
}