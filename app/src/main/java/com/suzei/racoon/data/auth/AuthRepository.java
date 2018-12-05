package com.suzei.racoon.data.auth;

public interface AuthRepository {

    interface Listener {

        void onAuthSuccess(String uid);

        void onAuthFailure(Exception e);

    }

    interface ResetPasswordListener {

        void onResetPasswordSuccess();

        void onResetPasswordFailure(Exception e);

    }

    void executeLogin(String email, String password, Listener listener);

    void executeRegister(String email, String password, Listener listener);

    void executeResetPassword(String email, ResetPasswordListener listener);

}
