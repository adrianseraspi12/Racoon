package com.suzei.racoon.ui.auth.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suzei.racoon.R
import com.suzei.racoon.data.auth.AuthRepositoryImpl
import com.suzei.racoon.data.user.UserRepositoryImpl

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginFragment = LoginFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, loginFragment)
            .commit()

        LoginPresenter(
            AuthRepositoryImpl(),
            UserRepositoryImpl(),
            loginFragment
        )
    }
}