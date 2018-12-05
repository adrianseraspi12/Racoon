package com.suzei.racoon.ui.auth.forgotpassword;

import com.suzei.racoon.ui.base.Contract;

public interface ForgotPasswordContract {

    interface View extends Contract.ProgressView {

        void setPresenter(Presenter presenter);

        void onResetPasswordSuccess();

        void onResetPasswordFailure(String message);

    }

    interface Presenter {

        void resetPassword(String email);

    }

}
