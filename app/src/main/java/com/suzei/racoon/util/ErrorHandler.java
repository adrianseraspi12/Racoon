package com.suzei.racoon.util;

import android.content.Context;
import android.widget.Toast;

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

    public static String authError(String errorCode) {
        switch (errorCode) {

            case "ERROR_INVALID_EMAIL":
                return "Email is invalid";

            case "ERROR_WRONG_PASSWORD":
                return "Wrong Password";

            case "ERROR_EMAIL_ALREADY_IN_USE":
                return "Email is already exist, Please use other email address";

            default:
                return "Email or password is incorrect";
        }
    }

}
