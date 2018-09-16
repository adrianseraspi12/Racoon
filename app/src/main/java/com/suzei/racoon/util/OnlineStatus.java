package com.suzei.racoon.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class OnlineStatus {

    public static void set(boolean isOnline) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("online")
                    .setValue(isOnline);
        }

    }

}
