package com.suzei.racoon.ui.auth.forgotpassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.suzei.racoon.R;
import com.suzei.racoon.data.auth.AuthRepositoryImpl;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ForgotPasswordFragment forgotPasswordFragment = ForgotPasswordFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.forgot_password_container, forgotPasswordFragment)
                .commit();

        new ForgotPasswordPresenter(
                new AuthRepositoryImpl(),
                forgotPasswordFragment);

    }
}
