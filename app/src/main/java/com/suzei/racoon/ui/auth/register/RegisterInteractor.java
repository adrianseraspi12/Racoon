package com.suzei.racoon.ui.auth.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.auth.AuthContract;
import com.suzei.racoon.util.ErrorHandler;

import java.util.HashMap;

public class RegisterInteractor {

    private Context context;
    private AuthContract.onRegisterListener mRegisterListener;

    RegisterInteractor(Context context, AuthContract.onRegisterListener mRegisterListener) {
        this.mRegisterListener = mRegisterListener;
        this.context = context;
    }

    public void register(String displayName, String email, String password) {

        new Handler().postDelayed(() -> {

            if (TextUtils.isEmpty(email)) {
                mRegisterListener.onUsernameError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mRegisterListener.onUsernameError("Use a valid Email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mRegisterListener.onPasswordError("Password is required");
                return;
            }

            if (password.length() < 5) {
                mRegisterListener.onPasswordError("Password is weak, Minimum of 6 characters");
                return;
            }

            if (TextUtils.isEmpty(displayName)) {
                mRegisterListener.onDisplayNameError("Name is required");
                return;
            }

            performFirebaseRegister(displayName, email, password);

        }, 2000);

    }

    private void performFirebaseRegister(String name, String email, String password) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        saveEmailToSharedPref(email);
                        createDefaultUserDetails(uid, name);
                    } else {
                        FirebaseAuthException errorCode = (FirebaseAuthException) task.getException();
                        String message = ErrorHandler.authError(errorCode.getErrorCode());
                        mRegisterListener.onFailure(message);
                    }

                });
    }

    private void saveEmailToSharedPref(String email) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    private void createDefaultUserDetails(String uid, String name) {
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        String defaultImage = context.getResources().getString(R.string.default_user_image);

        HashMap<String, Object> detailsMap = new HashMap<>();
        detailsMap.put("age", 18);
        detailsMap.put("bio", "");
        detailsMap.put("gender", "Unknown");
        detailsMap.put("image", defaultImage);
        detailsMap.put("name", name);
        detailsMap.put("online", true);
        detailsMap.put("device_token", deviceToken);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).setValue(detailsMap)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        mRegisterListener.onSuccess();
                    }

                });
    }
}
