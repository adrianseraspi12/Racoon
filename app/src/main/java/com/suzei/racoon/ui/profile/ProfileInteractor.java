package com.suzei.racoon.ui.profile;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.base.Contract;

public class ProfileInteractor {

    private Contract.Listener<Users> profileListener;

    ProfileInteractor(Contract.Listener<Users> profileListener) {
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
                        profileListener.onLoadSuccess(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        profileListener.onLoadFailed(databaseError);
                    }
                };
    }
}
