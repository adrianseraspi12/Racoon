package com.suzei.racoon.ui.group;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.ui.base.Contract;

public class GroupDetailsInteractor {

    private Contract.Listener<Groups> groupListener;

    private DatabaseReference mGroupsRef;

    GroupDetailsInteractor(Contract.Listener<Groups> groupListener) {
        this.groupListener = groupListener;
        mGroupsRef = FirebaseDatabase.getInstance().getReference();
    }

    public void startFirebaseDatabaseLoad(String groupId) {
        mGroupsRef
                .child("groups").child(groupId)
                .addValueEventListener(groupDetailsValueEventListener());
    }

    public void destroy(String groupId) {
        mGroupsRef
                .child("groups").child(groupId)
                .removeEventListener(groupDetailsValueEventListener());
    }

    private ValueEventListener groupDetailsValueEventListener() {

        return new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Groups groups = dataSnapshot.getValue(Groups.class);
                groupListener.onLoadSuccess(groups);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

}
