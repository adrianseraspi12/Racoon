package com.suzei.racoon.auth.login;

import android.app.Activity;
import android.content.Context;

import com.suzei.racoon.auth.AuthContract;

public class LoginPresenter implements AuthContract.onLoginListener {

    private AuthContract.LoginView mLoginView;
    private LoginInteractor mLoginInteractor;

    public LoginPresenter(AuthContract.LoginView mLoginView) {
        this.mLoginView = mLoginView;
        this.mLoginInteractor = new LoginInteractor(this);
    }

    public void validateLoginCredentials(Activity activity, String email, String password) {
        mLoginView.showProgress();
        mLoginInteractor.login(activity, email, password);
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
    public void onFailure(Exception e) {
        mLoginView.hideProgress();
        mLoginView.onLoginFailure(e);
    }
}
