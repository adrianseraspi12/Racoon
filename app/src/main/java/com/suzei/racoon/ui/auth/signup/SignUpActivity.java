package com.suzei.racoon.ui.auth.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.suzei.racoon.R;
import com.suzei.racoon.data.auth.AuthRepositoryImpl;
import com.suzei.racoon.data.user.UserRepositoryImpl;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SignUpFragment signUpFragment = SignUpFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sign_up_container, signUpFragment)
                .commit();

        new SignUpPresenter(
                new AuthRepositoryImpl(),
                new UserRepositoryImpl(),
                signUpFragment);
    }
}
