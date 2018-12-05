package com.suzei.racoon.data.user;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class UserRepositoryImpl implements UserRepository {

    private DatabaseReference userDatabase;

    public UserRepositoryImpl() {
        this.userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @Override
    public void saveUserDetails(String uid,
                                Map<String, Object> userMap,
                                OnSaveListener listener) {

        userDatabase.child(uid).updateChildren(userMap).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                listener.onSuccess();
            }
            else {
                listener.onFailure(task.getException());
            }

        });

    }
}
