package com.suzei.racoon.util;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseError;
import com.suzei.racoon.R;

public class ErrorHandler {

    public static void databaseError(Context context, Exception e) {
        DatabaseError exception = DatabaseError.fromException(e);

        switch (exception.getCode()) {
            case DatabaseError.DISCONNECTED:
                Toast.makeText(context, context.getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
                break;
            case DatabaseError.NETWORK_ERROR:
                Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void authError(Context context, Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) {
            Toast.makeText(context, context.getString(R.string.weak_password), Toast.LENGTH_SHORT).show();
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(context, context.getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(context, context.getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
