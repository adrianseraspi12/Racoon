package com.suzei.racoon.ui.auth.login;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suzei.racoon.R;
import com.suzei.racoon.data.auth.AuthRepositoryImpl;
import com.suzei.racoon.data.user.UserRepositoryImpl;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginFragment loginFragment = LoginFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_container, loginFragment)
                .commit();

        new LoginPresenter(
                new AuthRepositoryImpl(),
                new UserRepositoryImpl(),
                loginFragment);
    }
}
