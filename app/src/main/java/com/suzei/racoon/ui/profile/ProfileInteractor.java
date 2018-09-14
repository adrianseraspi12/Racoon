package com.suzei.racoon.ui.profile;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.base.FirebaseManager;

public class ProfileInteractor implements FirebaseManager.FirebaseCallback {

    private Contract.Listener<Users> profileListener;
    private FirebaseManager firebaseManager;
    private DatabaseReference usersRef;

    ProfileInteractor(Contract.Listener<Users> profileListener) {
        this.profileListener = profileListener;
        firebaseManager = new FirebaseManager(this);
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void loadDetails(String uid) {
        firebaseManager.startValueEventListener(usersRef.child(uid));
    }

    public void destroy(String uid) {
        firebaseManager.stopValueEventListener(usersRef.child(uid));
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        Users users = dataSnapshot.getValue(Users.class);
        profileListener.onLoadSuccess(users);
    }
}
