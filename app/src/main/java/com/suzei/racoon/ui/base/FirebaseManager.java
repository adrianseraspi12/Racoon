package com.suzei.racoon.ui.base;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManager implements ValueEventListener {

    private FirebaseCallback firebaseCallback;

    public FirebaseManager(FirebaseCallback firebaseCallback) {
        this.firebaseCallback = firebaseCallback;
    }

    public void startSingleEventListener(DatabaseReference db) {
        db.addListenerForSingleValueEvent(this);
    }

    public void startValueEventListener(DatabaseReference db) {
        db.addValueEventListener(this);
    }

    public void stopValueEventListener(DatabaseReference db) {
        db.removeEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        firebaseCallback.onSuccess(dataSnapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public interface FirebaseCallback {

        void onSuccess(DataSnapshot dataSnapshot);

    }
}
