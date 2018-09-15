package com.suzei.racoon.ui.auth.login;

import android.app.Activity;
import android.content.Context;

import com.suzei.racoon.ui.auth.AuthContract;

public class LoginPresenter implements AuthContract.onAuthListener {

    private AuthContract.LoginView mLoginView;
    private LoginInteractor mLoginInteractor;

    LoginPresenter(Activity activity, AuthContract.LoginView mLoginView) {
        this.mLoginView = mLoginView;
        this.mLoginInteractor = new LoginInteractor(activity, this);
    }

    public void validateLoginCredentials(String email, String password) {
        mLoginView.showProgress();
        mLoginInteractor.login(email, password);
    }

    @Override
    public void onUsernameError(String message) {
        mLoginView.hideProgress();
        mLoginView.setUsernameError(message);
    }

    @Override
    public void onPasswordError(String message) {
        mLoginView.hideProgress();
        mLoginView.setPasswordError(message);
    }

    @Override
    public void onSuccess() {
        mLoginView.hideProgress();
        mLoginView.onLoginSuccess();
    }

    @Override
    public void onFailure(String message) {
        mLoginView.hideProgress();
        mLoginView.onLoginFailure(message);
    }
}
