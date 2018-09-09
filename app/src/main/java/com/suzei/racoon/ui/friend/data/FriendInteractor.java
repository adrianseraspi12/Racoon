package com.suzei.racoon.ui.friend.data;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.ui.base.Contract;

import java.util.HashMap;
import java.util.Map;

public class FriendInteractor {

    private Contract.Listener<String> friendListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mFriendsRef;
    private DatabaseReference mRequestRef;
    private DatabaseReference mNotifCountRef;

    FriendInteractor(Contract.Listener<String> friendListener) {
        this.friendListener = friendListener;
        initObjects();
    }

    private void initObjects() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFriendsRef = mRootRef.child("user_friends");
        mRequestRef = mRootRef.child("request").child("friend");
        mNotifCountRef = mRootRef.child("notification_count");
    }

    public void executeRequest(String currentUserId, String friendId, String currentState) {
        switch (currentState) {

            case "request_sent":
                cancelFriendRequest(currentUserId, friendId);
                break;

            case "friend":
                unfriendUser(currentUserId, friendId);
                break;

            case "not_friend":
                sendFriendRequest(currentUserId, friendId);
                break;

            case "request_received":
                acceptFriendRequest(currentUserId, friendId);
                break;

            default:
                throw new IllegalArgumentException("Invalid current state = " + currentState);

        }
    }

    public void readCurrentState(String currentUserId, String friendId) {
        mRequestRef.child(currentUserId)
                .addValueEventListener(performFirebaseDatabaseReading(currentUserId, friendId));
    }

    public void destroyReading(String currentUserId, String friendId) {
        mRequestRef.child(currentUserId)
                .removeEventListener(performFirebaseDatabaseReading(currentUserId, friendId));
    }

    private void cancelFriendRequest(String currentUserId, String friendId) {
        HashMap<String, Object> cancelFriendReq = new HashMap<>();
        cancelFriendReq.put("request/friend/" + currentUserId + "/" + friendId, null);
        cancelFriendReq.put("request/friend/" + friendId + "/" + currentUserId, null);

        mRootRef.updateChildren(cancelFriendReq, (error, databaseReference) -> {
            if (error == null) {
                friendListener.onLoadSuccess("not_friend");

            } else {
                //TODO Handle Error
            }
        });

    }

    private void unfriendUser(String currentUserId, String friendId) {
        Map<String, Object> unfriendMap = new HashMap<>();
        unfriendMap.put("user_friends/" + currentUserId + "/" + friendId, null);
        unfriendMap.put("user_friends/" + friendId + "/" + currentUserId, null);

        mRootRef.updateChildren(unfriendMap, (error, databaseReference) -> {

            if (error == null) {
                friendListener.onLoadSuccess("not_friend");
            } else {
                //TODO Handle Errors
            }

        });
    }

    private void sendFriendRequest(String currentUserId, String friendId) {
        DatabaseReference notifRef = mRootRef.child("notifications").child(currentUserId).push();
        String key = notifRef.getKey();

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("request/friend/" + currentUserId + "/" + friendId + "/request_type", "sent");
        requestMap.put("request/friend/" + friendId + "/" + currentUserId + "/request_type", "received");
        requestMap.put("notifications/" + currentUserId + "/" + key,
                getNotif(friendId, "friend_request", "sender"));
        requestMap.put("notifications/" + friendId + "/" + key,
                getNotif(currentUserId, "friend_request","receiver"));

        mRootRef.updateChildren(requestMap, (error, databaseReference) -> {
            updateNotifCount(friendId);
            updateNotifCount(currentUserId);
            if (error == null) {
                friendListener.onLoadSuccess("request_sent");
            } else {
                //TODO handle errors
            }
        });
    }

    private void acceptFriendRequest(String currentUserId, String friendId) {
        DatabaseReference notifRef = mRootRef.child("notifications").child(currentUserId).push();
        String key = notifRef.getKey();

        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("user_friends/" + currentUserId + "/" + friendId, true);
        friendMap.put("user_friends/" + friendId + "/" + currentUserId, true);

        friendMap.put("request/friend/" + currentUserId + "/" + friendId, null);
        friendMap.put("request/friend/" + friendId + "/" + currentUserId, null);

        friendMap.put("notifications/" + currentUserId + "/" + key,
                getNotif(friendId, "friend_accepted", "sender"));
        friendMap.put("notifications/" + friendId + "/" + key,
                getNotif(currentUserId, "friend_accepted", "receiver"));

        mRootRef.updateChildren(friendMap, (error, databaseReference) -> {

            updateNotifCount(friendId);
            updateNotifCount(currentUserId);

            if (error == null) {
                friendListener.onLoadSuccess("friend");
            } else {
                //TODO Handle Errors
            }

        });
    }

    private HashMap<String, Object> getNotif(String uid, String type, String role) {
        HashMap<String, Object> notifSendReq = new HashMap<>();
        notifSendReq.put("timestamp", ServerValue.TIMESTAMP);
        notifSendReq.put("type", type);
        notifSendReq.put("role", role);
        notifSendReq.put("uid", uid);
        notifSendReq.put("seen", false);
        return notifSendReq;
    }
    private void updateNotifCount(String uid) {
        mNotifCountRef.child(uid).child("alerts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = dataSnapshot.child("count").getValue(Integer.class);
                        int updatedCount = count + 1;
                        dataSnapshot.getRef().child("count").setValue(updatedCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private ValueEventListener performFirebaseDatabaseReading(String currentUserId,
                                                              String friendId) {

        return new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(friendId)) {

                    //check if the user received/sent the friend req
                    String req_type = dataSnapshot.child(friendId).child("request_type")
                            .getValue(String.class);

                    if (req_type.equals("received")) {
                        friendListener.onLoadSuccess("request_received");
                    } else if (req_type.equals("sent")) {
                        friendListener.onLoadSuccess("request_sent");
                    }
                } else {
                    //Check if the user is friend
                    mFriendsRef.child(currentUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(friendId)) {
                                        friendListener.onLoadSuccess("friend");
                                    } else {
                                        friendListener.onLoadSuccess("not_friend");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }
}
