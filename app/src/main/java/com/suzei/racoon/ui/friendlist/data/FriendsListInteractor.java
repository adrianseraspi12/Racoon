package com.suzei.racoon.ui.friendlist.data;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.base.UsersAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsListInteractor {

    private Contract.Listener<UsersAdapter> listener;

    public FriendsListInteractor(Contract.Listener<UsersAdapter> listener) {
        this.listener = listener;
    }

    public void performFirebaseDatabaseLoad() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        DatabaseReference mFriendsRef = FirebaseDatabase.getInstance().getReference()
                .child("user_friends")
                .child(currentUserId);

        mFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    HashMap<String, Object> friendKeys =
                            (HashMap<String, Object>) dataSnapshot.getValue();

                    ArrayList<String> mFriendsList = new ArrayList<>(friendKeys.keySet());

                    UsersAdapter adapter = new UsersAdapter(mFriendsList, (users, itemView) -> {
//                        Intent chatIntent = new Intent(getContext(), ChatRoomActivity.class);
//                        chatIntent.putExtra(ChatRoomActivity.EXTRA_DETAILS, users);
//                        chatIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_TYPE,
//                                ChatRoomActivity.ChatType.SINGLE_CHAT);
//                        startActivity(chatIntent);
                    });

                    listener.onLoadSuccess(adapter);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
