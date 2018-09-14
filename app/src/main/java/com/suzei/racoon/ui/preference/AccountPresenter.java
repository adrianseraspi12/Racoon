package com.suzei.racoon.ui.preference;

import android.content.Context;

import com.suzei.racoon.ui.auth.AuthContract;

public class AccountPresenter implements AccountContract.AccountListener {

    private AccountContract.AccountView accountView;
    private AccountInteractor accountInteractor;

    AccountPresenter(Context context, AccountContract.AccountView  accountView) {
        this.accountView = accountView;
        accountInteractor = new AccountInteractor(context, this);
    }

    public void changeEmail(String email, String password, String newEmail) {
        accountView.showProgress();
        accountInteractor.changeEmail(email, password, newEmail);
    }

    public void resetPassword(String email, String oldPassword, String newPassword) {
        accountView.showProgress();
        accountInteractor.resetPassword(email, oldPassword, newPassword);
    }

    @Override
    public void onUsernameError(String message) {
        accountView.hideProgress();
        accountView.setUsernameError(message);
    }

    @Override
    public void onPasswordError(String message) {
        accountView.hideProgress();
        accountView.setPasswordError(message);
    }

    @Override
    public void onSuccess() {
        accountView.hideProgress();
        accountView.onLoginSuccess();
    }

    @Override
    public void onFailure(Exception e) {
        accountView.hideProgress();
        accountView.onLoginFailure(e);
    }

    @Override
    public void onAccountChangeError(String message) {
        accountView.hideProgress();
        accountView.setAccountChangeError(message);
    }
}
