package com.suzei.racoon.ui.auth.forgotpassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.suzei.racoon.R
import com.suzei.racoon.data.auth.AuthRepositoryImpl

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val toolbar = findViewById<Toolbar>(R.id.forgot_password_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val forgotPasswordFragment = ForgotPasswordFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.forgot_password_container, forgotPasswordFragment)
            .commit()

        ForgotPasswordPresenter(
            AuthRepositoryImpl(),
            forgotPasswordFragment
        )
    }
}