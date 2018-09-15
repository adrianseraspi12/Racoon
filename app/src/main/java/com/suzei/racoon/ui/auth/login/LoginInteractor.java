package com.suzei.racoon.ui.auth.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.suzei.racoon.ui.auth.AuthContract;
import com.suzei.racoon.util.ErrorHandler;

public class LoginInteractor {

    private Activity activity;
    private AuthContract.onAuthListener mOnLoginListener;
    private FirebaseAuth mAuth;

    LoginInteractor(Activity activity, AuthContract.onAuthListener mOnLoginListener) {
        this.mOnLoginListener = mOnLoginListener;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    public void login (String email, String password) {
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

            performFirebaseLogin(email, password);

        }, 2000);
    }

    private void performFirebaseLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        saveEmailToSharedPref(email);
                        updateDeviceToken(uid);
                    } else {
                        FirebaseAuthException errorCode = (FirebaseAuthException)task.getException();
                        String message = ErrorHandler.authError(errorCode.getErrorCode());
                        mOnLoginListener.onFailure(message);
                    }
                });
    }

    private void saveEmailToSharedPref(String email) {
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    private void updateDeviceToken(String uid) {
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).child("device_token").setValue(deviceToken)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        mOnLoginListener.onSuccess();
                    } else {
                        mOnLoginListener.onFailure(task.getException().getMessage());
                    }

                });
    }
}
