package com.suzei.racoon.auth.login;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.suzei.racoon.auth.AuthContract;

public class LoginInteractor {

    private AuthContract.onLoginListener mOnLoginListener;

    LoginInteractor(AuthContract.onLoginListener mOnLoginListener) {
        this.mOnLoginListener = mOnLoginListener;
    }

    public void login (Activity activity, String email, String password) {
        new Handler().postDelayed(() -> {

            if (TextUtils.isEmpty(email)) {
                mOnLoginListener.onUsernameError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mOnLoginListener.onUsernameError("Use a valid Email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mOnLoginListener.onPasswordError("Password is required");
                return;
            }

            performFirebaseLogin(activity, email, password);

        }, 2000);
    }

    private void performFirebaseLogin(Activity activity, String email, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {

                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        updateDeviceToken(uid);
                    } else {
                        mOnLoginListener.onFailure(task.getException());
                    }

                });
    }

    private void updateDeviceToken(String uid) {
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).child("device_token").setValue(deviceToken)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        mOnLoginListener.onSuccess();
                    } else {
                        mOnLoginListener.onFailure(task.getException());
                    }

                });
    }
}
