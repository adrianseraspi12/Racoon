package com.suzei.racoon.auth;

public interface AuthContract {

    interface LoginView {

        void showProgress();

        void hideProgress();

        void setUsernameError(String message);

        void setPasswordError(String message);

        void onLoginSuccess();

        void onLoginFailure(Exception e);

    }

    interface RegisterView {

        void showProgress();

        void hideProgress();

        void setUsernameError(String message);

        void setPasswordError(String message);

        void setDisplayNameError(String message);

        void onLoginSuccess();

        void onLoginFailure(Exception e);

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
