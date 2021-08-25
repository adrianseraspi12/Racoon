package com.suzei.racoon.ui.auth.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.suzei.racoon.R
import com.suzei.racoon.data.auth.AuthRepositoryImpl
import com.suzei.racoon.data.user.UserRepositoryImpl

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val toolbar = findViewById<Toolbar>(R.id.signup_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val signUpFragment = SignUpFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.sign_up_container, signUpFragment)
            .commit()

        SignUpPresenter(
            AuthRepositoryImpl(),
            UserRepositoryImpl(),
            signUpFragment
        )
    }
}