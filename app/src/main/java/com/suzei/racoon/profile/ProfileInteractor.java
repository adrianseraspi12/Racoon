package com.suzei.racoon.profile;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.auth.AuthContract;
import com.suzei.racoon.model.Users;

public class ProfileInteractor {

    private ProfileContract.onProfileListener profileListener;

    public ProfileInteractor(ProfileContract.onProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    public void loadDetails(String uid) {
        new Handler().postDelayed(() -> {

            if (TextUtils.isEmpty(uid)) {
                return;
            }

            performFirebaseDatabaseLoad(uid);
        }, 2000);
    }

    private void performFirebaseDatabaseLoad(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Users users = dataSnapshot.getValue(Users.class);
                        profileListener.onSuccess(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        profileListener.onFailure();
                    }
                });
    }
}
