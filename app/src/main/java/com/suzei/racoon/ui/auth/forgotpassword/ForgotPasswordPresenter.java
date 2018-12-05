package com.suzei.racoon.ui.auth.forgotpassword;

import android.text.TextUtils;
import android.util.Patterns;

import com.suzei.racoon.data.auth.AuthRepository;

public class ForgotPasswordPresenter implements ForgotPasswordContract.Presenter {

    private AuthRepository authRepository;
    private ForgotPasswordContract.View mView;

    ForgotPasswordPresenter(AuthRepository authRepository,
                            ForgotPasswordContract.View mView) {
        this.authRepository = authRepository;
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void resetPassword(String email) {

        if (!hasErrors(email)) {
            mView.showProgress();
            authRepository.executeResetPassword(email, new AuthRepository.ResetPasswordListener() {

                @Override
                public void onResetPasswordSuccess() {
                    mView.hideProgress();
                    mView.onResetPasswordSuccess();
                }

                @Override
                public void onResetPasswordFailure(Exception e) {
                    mView.hideProgress();
                    mView.onResetPasswordFailure(e.getMessage());
                }

            });
        }

    }

    private boolean hasErrors(String email) {

        if (TextUtils.isEmpty(email)) {
            mView.onResetPasswordFailure("Email is required");
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mView.onResetPasswordFailure("Email is invalid");
            return true;
        }

        return false;
    }
}
