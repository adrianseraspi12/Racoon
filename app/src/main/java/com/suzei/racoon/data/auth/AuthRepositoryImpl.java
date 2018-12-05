package com.suzei.racoon.data.auth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class AuthRepositoryImpl implements AuthRepository {

    private FirebaseAuth mAuth;

    public AuthRepositoryImpl() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void executeLogin(String email, String password, Listener listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                listener.onAuthSuccess(uid);
            }
            else {
                listener.onAuthFailure(task.getException());
            }

        });
    }

    @Override
    public void executeRegister(String email, String password, Listener listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                listener.onAuthSuccess(uid);
            }
            else {
                listener.onAuthFailure(task.getException());
            }

        });
    }

    @Override
    public void executeResetPassword(String email, ResetPasswordListener listener) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                listener.onResetPasswordSuccess();
            }
            else {
                listener.onResetPasswordFailure(task.getException());
            }

        });
    }

}
