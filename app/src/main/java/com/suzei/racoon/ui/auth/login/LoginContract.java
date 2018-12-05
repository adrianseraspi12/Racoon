package com.suzei.racoon.ui.auth.login;

import com.suzei.racoon.ui.base.Contract;

public interface LoginContract {

    interface View extends Contract.ProgressView {

        void setPresenter(Presenter presenter);

        void onLoginSuccess();

        void onLoginFailure(String message);

    }

    interface Presenter {

        void loginUser(String email, String password);

    }

}
