package com.suzei.racoon.ui.auth.login;

import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.suzei.racoon.data.auth.AuthRepository;
import com.suzei.racoon.data.user.UserRepository;
import com.suzei.racoon.ui.auth.AuthContract;

import java.util.HashMap;
import java.util.Map;

public class LoginPresenter implements LoginContract.Presenter, AuthRepository.Listener {

    private LoginContract.View mLoginView;
    private AuthRepository authRepository;
    private UserRepository userRepository;

    LoginPresenter(AuthRepository authRepository,
                   UserRepository userRepository,
                   LoginContract.View mLoginView) {
        this.mLoginView = mLoginView;
        this.authRepository = authRepository;
        this.userRepository = userRepository;

        mLoginView.setPresenter(this);
    }

    @Override
    public void loginUser(String email, String password) {
        if (!hasError(email, password)) {
            mLoginView.showProgress();
            authRepository.executeLogin(email, password, this);
        }
    }

    private boolean hasError(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            mLoginView.onLoginFailure("Email is required");
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            mLoginView.onLoginFailure("Password is required");
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mLoginView.onLoginFailure("Email is invalid");
            return true;
        }

        return false;
    }

    @Override
    public void onAuthSuccess(String uid) {
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("device_token", deviceToken);
        userRepository.saveUserDetails(uid, userMap, new UserRepository.OnSaveListener() {

            @Override
            public void onSuccess() {
                mLoginView.hideProgress();
                mLoginView.onLoginSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                mLoginView.hideProgress();
                FirebaseAuth.getInstance().signOut();
                mLoginView.onLoginFailure("Can't connect to server, Please login again");
            }

        });
    }

    @Override
    public void onAuthFailure(Exception e) {
        mLoginView.onLoginFailure(e.getMessage());
    }
}
