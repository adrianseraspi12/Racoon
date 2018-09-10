package com.suzei.racoon.ui.auth;

import com.suzei.racoon.ui.base.Contract;

public interface AuthContract {

    interface LoginView extends Contract.ProgressView {

        void setUsernameError(String message);

        void setPasswordError(String message);

        void onLoginSuccess();

        void onLoginFailure(Exception e);

    }

    interface RegisterView extends LoginView {

        void setDisplayNameError(String message);

    }

    interface onLoginListener {

        void onUsernameError(String message);

        void onPasswordError(String message);

        void onSuccess();

        void onFailure(Exception e);

    }

    interface onRegisterListener {

        void onUsernameError(String message);

        void onPasswordError(String message);

        void onDisplayNameError(String message);

        void onSuccess();

        void onFailure(Exception e);
    }

}
