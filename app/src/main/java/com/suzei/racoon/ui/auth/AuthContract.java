package com.suzei.racoon.ui.auth;

import com.suzei.racoon.ui.base.Contract;

public interface AuthContract {

    interface LoginView extends Contract.ProgressView {

        void setUsernameError(String message);

        void setPasswordError(String message);

        void onLoginSuccess();

        void onLoginFailure(String message);

    }

    interface RegisterView extends LoginView {

        void setDisplayNameError(String message);

    }

    interface LoginPresenter {

        void loginUser(String email, String password);

    }

    interface onAuthListener {

        void onUsernameError(String message);

        void onPasswordError(String message);

        void onSuccess();

        void onFailure(String message);

    }

    interface onRegisterListener extends onAuthListener {

        void onDisplayNameError(String message);

    }

}
