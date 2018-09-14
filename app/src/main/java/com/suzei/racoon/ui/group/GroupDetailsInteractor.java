package com.suzei.racoon.ui.group;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.base.FirebaseManager;

public class GroupDetailsInteractor implements FirebaseManager.FirebaseCallback {

    private Contract.Listener<Groups> groupListener;

    private FirebaseManager firebaseManager;
    private DatabaseReference mGroupsRef;

    GroupDetailsInteractor(Contract.Listener<Groups> groupListener) {
        this.groupListener = groupListener;
        mGroupsRef = FirebaseDatabase.getInstance().getReference().child("groups");
        firebaseManager = new FirebaseManager(this);
    }

    public void startFirebaseDatabaseLoad(String groupId) {
        firebaseManager.startValueEventListener(mGroupsRef.child(groupId));
    }

    public void destroy(String groupId) {
        firebaseManager.stopValueEventListener(mGroupsRef.child(groupId));
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        Groups groups = dataSnapshot.getValue(Groups.class);
        groupListener.onLoadSuccess(groups);
    }
}
