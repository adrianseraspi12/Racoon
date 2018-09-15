package com.suzei.racoon.ui.friendlist;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.base.FirebaseManager;
import com.suzei.racoon.ui.base.UsersAdapter;
import com.suzei.racoon.ui.friend.FriendActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsListInteractor implements FirebaseManager.FirebaseCallback {

    private Contract.Listener<UsersAdapter> listener;
    private FirebaseManager firebaseManager;
    private DatabaseReference mFriendsRef;
    private Context context;

    FriendsListInteractor(Context context, Contract.Listener<UsersAdapter> listener) {
        this.listener = listener;
        this.context = context;
        String currentUserId = FirebaseAuth.getInstance().getUid();
        mFriendsRef = FirebaseDatabase.getInstance().getReference()
                .child("user_friends").child(currentUserId);
        firebaseManager = new FirebaseManager(this);
    }

    public void performFirebaseDatabaseLoad() {
        firebaseManager.startSingleEventListener(mFriendsRef);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            HashMap<String, Object> friendKeys =
                    (HashMap<String, Object>) dataSnapshot.getValue();

            ArrayList<String> mFriendsList = new ArrayList<>(friendKeys.keySet());

            UsersAdapter adapter = new UsersAdapter(mFriendsList, (users, itemView) -> {
                Intent intent = new Intent(context, FriendActivity.class);
                intent.putExtra(FriendActivity.EXTRA_PROFILE_DETAILS, users);
                context.startActivity(intent);
            });

            listener.onLoadSuccess(adapter);
        }
    }
}
