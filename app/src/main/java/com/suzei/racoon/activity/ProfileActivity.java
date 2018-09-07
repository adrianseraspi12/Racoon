package com.suzei.racoon.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.FirebaseExceptionUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class ProfileActivity extends AppCompatActivity {

    public static final String EXTRA_PROFILE_DETAILS = "profile_details";

    private DatabaseReference mRootRef;
    private DatabaseReference mFriendsRef;
    private DatabaseReference mRequestRef;
    private DatabaseReference mNotifCountRef;
    private ValueEventListener mRequestListener;

    private String currentUserId;
    private String mId;
    private String mCurrentState = "not_friend";

    private Users users;

    @BindDrawable(R.drawable.gender_male) Drawable drawableMale;
    @BindDrawable(R.drawable.gender_female) Drawable drawableFemale;
    @BindDrawable(R.drawable.gender_unknown) Drawable drawableUnknown;
    @BindString(R.string.add_friend) String stringAddFriend;
    @BindString(R.string.accept_friend) String stringAcceptFriend;
    @BindString(R.string.cancel_request) String stringCancelReq;
    @BindString(R.string.unfriend) String stringUnfriend;
    @BindView(R.id.profile_back) ImageButton backView;
    @BindView(R.id.profile_image) RoundedImageView imageView;
    @BindView(R.id.profile_name) TextView nameView;
    @BindView(R.id.profile_description) TextView bioView;
    @BindView(R.id.profile_age) TextView ageView;
    @BindView(R.id.profile_gender) TextView genderView;
    @BindView(R.id.profile_add_friend) Button addFriendView;
    @BindView(R.id.profile_message) Button messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        initBundleUserDetails();
        initObjects();
        showUserDetails();
        setUpFriendReqBtn();

    }

    private void initBundleUserDetails() {
        users = getIntent().getParcelableExtra(EXTRA_PROFILE_DETAILS);
        mId = users.getUid();
        Timber.i("ID = %s", mId);
    }

    private void initObjects() {
        ButterKnife.bind(this);
        currentUserId = FirebaseAuth.getInstance().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFriendsRef = mRootRef.child("user_friends");
        mRequestRef = mRootRef.child("request").child("friend");
        mNotifCountRef = mRootRef.child("notification_count");
    }

    private void showUserDetails() {
        nameView.setText(users.getName());
        bioView.setText(users.getBio());
        genderView.setText(users.getGender());
        ageView.setText(String.valueOf(users.getAge()));
        Picasso.get().load(users.getImage()).into(imageView);

        switch (users.getGender()) {
            case "Male":
                genderView.setCompoundDrawablesWithIntrinsicBounds(drawableMale, null,
                        null, null);
                break;

            case "Female":
                genderView.setCompoundDrawablesWithIntrinsicBounds(drawableFemale, null,
                        null, null);
                break;

            case "Unknown":
                genderView.setCompoundDrawablesWithIntrinsicBounds(drawableUnknown, null,
                        null, null);
                break;
        }
    }

    private void setUpFriendReqBtn() {
        mRequestListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mId)) {
                    //check if the user received/sent the friend req
                    String req_type = dataSnapshot.child(mId).child("request_type")
                            .getValue(String.class);

                    if (req_type.equals("received")) {
                        mCurrentState = "request_received";
                        addFriendView.setText(stringAcceptFriend);
                    } else if (req_type.equals("sent")) {
                        mCurrentState = "request_sent";
                        addFriendView.setText(stringCancelReq);
                    }
                } else {
                    //Check if the user is friend
                    mFriendsRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(mId)) {
                                mCurrentState = "friend";
                                addFriendView.setText(stringUnfriend);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            FirebaseExceptionUtil.databaseError(ProfileActivity.this,
                                    error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseExceptionUtil.databaseError(ProfileActivity.this,
                        error.toException());
            }
        };

    }

    @OnClick(R.id.profile_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.profile_message)
    public void onMessageClick() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ChatRoomActivity.EXTRA_DETAILS, users);
        bundle.putInt(ChatRoomActivity.EXTRA_CHAT_TYPE, ChatRoomActivity.ChatType.SINGLE_CHAT);

        Intent chatIntent = new Intent(ProfileActivity.this, ChatRoomActivity.class);
        chatIntent.putExtras(bundle);
        startActivity(chatIntent);
    }

    @OnClick(R.id.profile_add_friend)
    public void onAddFriendClick() {
        addFriendView.setEnabled(false);

        switch (mCurrentState) {

            case "request_sent":
                cancelFriendRequest();
                break;

            case "friend":
                unfriendUser();
                break;

            case "not_friend":
                sendFriendRequest();
                break;

            case "request_received":
                acceptFriendRequest();
                break;

            default:
                throw new IllegalArgumentException("Invalid current state = " + mCurrentState);
        }
    }

    private void cancelFriendRequest() {
        HashMap<String, Object> cancelFriendReq = new HashMap<>();
        cancelFriendReq.put("request/friend/" + currentUserId + "/" + mId, null);
        cancelFriendReq.put("request/friend/" + mId + "/" + currentUserId, null);

        mRootRef.updateChildren(cancelFriendReq, (error, databaseReference) -> {

            if (error == null) {
                addFriendView.setEnabled(true);
                mCurrentState = "not_friend";
                addFriendView.setText(stringAddFriend);
            } else {
                FirebaseExceptionUtil.databaseError(ProfileActivity.this,
                        error.toException());
            }

        });
    }

    private void unfriendUser() {
        Map<String, Object> unfriendMap = new HashMap<>();
        unfriendMap.put("user_friends/" + currentUserId + "/" + mId, null);
        unfriendMap.put("user_friends/" + mId + "/" + currentUserId, null);

        mRootRef.updateChildren(unfriendMap, (error, databaseReference) -> {

            if (error == null) {

                mCurrentState = "not_friend";
                addFriendView.setText(stringAddFriend);
            } else {
                FirebaseExceptionUtil.databaseError(ProfileActivity.this,
                        error.toException());
            }

            addFriendView.setEnabled(true);

        });
    }

    private void sendFriendRequest() {
        DatabaseReference notifRef = mRootRef.child("notifications").child(currentUserId).push();
        String key = notifRef.getKey();

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("request/friend/" + currentUserId + "/" + mId + "/request_type", "sent");
        requestMap.put("request/friend/" + mId + "/" + currentUserId + "/request_type", "received");

        requestMap.put("notifications/" + currentUserId + "/" + key,
                getNotif(mId, "friend_request", "sender"));
        requestMap.put("notifications/" + mId + "/" + key,
                getNotif(currentUserId, "friend_request","receiver"));

        mRootRef.updateChildren(requestMap, (error, databaseReference) -> {

            updateNotifCount(mId);
            updateNotifCount(currentUserId);

            if (error == null) {
                mCurrentState = "request_sent";
                addFriendView.setText(stringCancelReq);
            } else {
                FirebaseExceptionUtil.databaseError(ProfileActivity.this,
                        error.toException());
            }
            addFriendView.setEnabled(true);

        });
    }

    private void acceptFriendRequest() {
        DatabaseReference notifRef = mRootRef.child("notifications").child(currentUserId).push();
        String key = notifRef.getKey();

        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("user_friends/" + currentUserId + "/" + mId, true);
        friendMap.put("user_friends/" + mId + "/" + currentUserId, true);

        friendMap.put("request/friend/" + currentUserId + "/" + mId, null);
        friendMap.put("request/friend/" + mId + "/" + currentUserId, null);

        friendMap.put("notifications/" + currentUserId + "/" + key, getNotif(mId,
                "friend_accepted", "sender"));
        friendMap.put("notifications/" + mId + "/" + key, getNotif(currentUserId,
                "friend_accepted", "receiver"));

        mRootRef.updateChildren(friendMap, (error, databaseReference) -> {

            updateNotifCount(mId);
            updateNotifCount(currentUserId);

            if (error == null) {
                mCurrentState = "friend";
                addFriendView.setText(stringUnfriend);
            } else {
                FirebaseExceptionUtil.databaseError(ProfileActivity.this,
                        error.toException());
            }

            addFriendView.setEnabled(true);

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
        mNotifCountRef.child(uid).child("alerts").addListenerForSingleValueEvent(new ValueEventListener() {

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

    @Override
    protected void onStart() {
        super.onStart();
        mRequestRef.child(currentUserId).addValueEventListener(mRequestListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRequestRef.child(currentUserId).removeEventListener(mRequestListener);
    }
}
