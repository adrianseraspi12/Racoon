package com.suzei.racoon.ui.worldlist;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class WorldInteractor {

    private WorldPresenter worldPresenter;

    WorldInteractor(WorldPresenter worldPresenter) {
        this.worldPresenter = worldPresenter;
    }

    public void performFirebaseDatabaseLoad() {
        DatabaseReference mWorldRef = FirebaseDatabase.getInstance().getReference()
                .child("world_chats");

        mWorldRef.keepSynced(true);

        mWorldRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    HashMap<String, Object> groupMap =
                            (HashMap<String, Object>) dataSnapshot.getValue();
                    ArrayList<String> worldGroupKeys = new ArrayList<>(groupMap.keySet());

                    WorldAdapter adapter = new WorldAdapter(worldGroupKeys);
                    worldPresenter.onLoadSuccess(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

}
