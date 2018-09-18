package com.suzei.racoon.ui.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.util.ErrorHandler;

public class AccountInteractor {

    private static final int CHANGE_EMAIL = 0;
    private static final int RESET_PASSWORD = 1;
    private static final int DELETE_ACCOUNT = 2;

    private AccountContract.AccountListener authListener;
    private Context context;
    private FirebaseUser currentUser;

    AccountInteractor(Context context, AccountContract.AccountListener authListener) {
        this.authListener = authListener;
        this.context = context;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void changeEmail(String email, String password, String newEmail) {
        new Handler().postDelayed(() -> {
            if (hasError(email, password) && isNewEmailHasError(newEmail)) {
                return;
            }

            reauthenticate(email, password, newEmail, CHANGE_EMAIL);
        }, 2000);
    }

    public void resetPassword(String email, String oldPassword, String newPassword) {
        new Handler().postDelayed(() -> {
            if (hasError(email, oldPassword) && isNewPasswordHasError(newPassword)) {
                return;
            }

            reauthenticate(email, oldPassword, newPassword, RESET_PASSWORD);
        }, 2000);
    }

    public void deleteAccount(String email, String password) {
        new Handler().postDelayed(() -> {
            if (hasError(email, password)) {
                return;
            }

            reauthenticate(email, password, "", DELETE_ACCOUNT);
        }, 2000);
    }

    private boolean isNewEmailHasError(String newEmail) {
        if (TextUtils.isEmpty(newEmail)) {
            authListener.onAccountChangeError("Email is required");
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            authListener.onAccountChangeError("Use a valid Email");
            return true;
        }

        return false;
    }

    private boolean isNewPasswordHasError(String newPassword) {
        if (TextUtils.isEmpty(newPassword)) {
            authListener.onAccountChangeError("Password is required");
            return true;
        }

        if (newPassword.length() < 5) {
            authListener.onAccountChangeError("Password is weak, Minimum of 6 characters");
            return true;
        }

        return false;
    }

    private boolean hasError(String email, String password) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("auth", Context.MODE_PRIVATE);

        String currentEmail = sharedPreferences.getString("email", "");
        if (!currentEmail.equals(email)) {
            authListener.onUsernameError("Use a valid Email");
            return true;
        }

        if (TextUtils.isEmpty(email)) {
            authListener.onUsernameError("Email is required");
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            authListener.onUsernameError("Use a valid Email");
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            authListener.onPasswordError("Password is required");
            return true;
        }

        return false;
    }

    private void reauthenticate(String email, String password, String strValue, int type) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
        currentUser.reauthenticate(authCredential).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                switch (type) {
                    case CHANGE_EMAIL:
                        updateEmail(strValue);
                        break;
                    case RESET_PASSWORD:
                        updatePassword(strValue);
                        break;
                    case DELETE_ACCOUNT:
                        performDeletion();
                        break;
                }

            } else {
                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                String message = ErrorHandler.authError(e.getErrorCode());
                authListener.onFailure(message);
            }

        });
    }

    private void updateEmail(String email) {
        currentUser.updateEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                authListener.onSuccess();
            }
        });
    }

    private void updatePassword(String password) {
        currentUser.updatePassword(password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                authListener.onSuccess();
            }
        });
    }

    private void performDeletion() {
        currentUser.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                authListener.onSuccess();
                deleteAllData();
            }
        });
    }

    private void deleteAllData() {
        String currentUserId = currentUser.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("messages").child(currentUserId).removeValue();
        rootRef.child("notification_count").child(currentUserId).removeValue();
        rootRef.child("notifications").child(currentUserId).removeValue();
        rootRef.child("user_chats").child(currentUserId).removeValue();
        rootRef.child("user_friends").child(currentUserId).removeValue();
        rootRef.child("users").child(currentUserId).removeValue();
    }
}
