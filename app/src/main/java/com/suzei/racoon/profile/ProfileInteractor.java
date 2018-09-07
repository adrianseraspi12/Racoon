package com.suzei.racoon.profile;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.model.Users;

public class ProfileInteractor {

    private ProfileContract.onProfileListener profileListener;

    ProfileInteractor(ProfileContract.onProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    public void loadDetails(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid)
                .addValueEventListener(eventListener());
    }

    public void destroy(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid)
                .removeEventListener(eventListener());
    }

    private ValueEventListener eventListener() {
        return new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Users users = dataSnapshot.getValue(Users.class);
                        profileListener.onSuccess(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        profileListener.onFailure();
                    }
                };
    }
}
