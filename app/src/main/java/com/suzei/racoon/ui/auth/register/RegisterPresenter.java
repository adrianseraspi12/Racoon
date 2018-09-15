package com.suzei.racoon.ui.auth.register;

import android.content.Context;

import com.suzei.racoon.ui.auth.AuthContract;

public class RegisterPresenter implements AuthContract.onRegisterListener {

    private AuthContract.RegisterView mRegisterView;
    private RegisterInteractor mRegisterInteractor;

    RegisterPresenter(Context context, AuthContract.RegisterView mRegisterView) {
        this.mRegisterView = mRegisterView;
        mRegisterInteractor = new RegisterInteractor(context, this);
    }

    public void validateRegisterCredentials(String name, String email, String password) {
        mRegisterView.showProgress();
        mRegisterInteractor.register(name, email, password);
    }

    @Override
    public void onUsernameError(String message) {
        mRegisterView.hideProgress();
        mRegisterView.setUsernameError(message);
    }

    @Override
    public void onPasswordError(String message) {
        mRegisterView.hideProgress();
        mRegisterView.setPasswordError(message);
    }

    @Override
    public void onDisplayNameError(String message) {
        mRegisterView.hideProgress();
        mRegisterView.setDisplayNameError(message);
    }

    @Override
    public void onSuccess() {
        mRegisterView.hideProgress();
        mRegisterView.onLoginSuccess();
    }

    @Override
    public void onFailure(String message) {
        mRegisterView.hideProgress();
        mRegisterView.onLoginFailure(message);
    }
}
