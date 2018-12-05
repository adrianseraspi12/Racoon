package com.suzei.racoon.ui.notificationlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.base.FirebaseManager;
import com.suzei.racoon.ui.friend.FriendActivity;
import com.suzei.racoon.model.Notifications;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.ErrorHandler;
import com.suzei.racoon.util.TimeRefractor;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NotificationAdapter extends FirebaseRecyclerAdapter<Notifications, NotificationAdapter.ViewHolder> {

    private DatabaseReference mNotifCount;
    private DatabaseReference mNotifRef;

    NotificationAdapter(@NonNull FirebaseRecyclerOptions<Notifications> options) {
        super(options);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        mNotifCount = rootRef.child("notification_count").child(currentUserId);
        mNotifRef = rootRef.child("notifications").child(currentUserId);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Notifications model) {
        String notifId = getRef(position).getKey();
        holder.bind(model, notifId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_notifications,
                parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private DatabaseReference mUsersRef;
        private FirebaseManager firebaseManager;

        @BindColor(android.R.color.white) int whiteColor;

        @BindView(R.id.item_notif_image) RoundedImageView imageView;
        @BindView(R.id.item_notif_type) TextView typeView;
        @BindView(R.id.item_notif_desc) TextView descView;
        @BindView(R.id.item_notif_timestamp) TextView timestampView;
        @BindView(R.id.item_notif_rootview) ConstraintLayout rootView;

        @BindString(R.string.sent_friend_request) String sentFriendReqStr;
        @BindString(R.string.receive_friend_request) String receiveFriendReqStr;
        @BindString(R.string.accepted_friend_request) String acceptedFriendReqStr;
        @BindString(R.string.friend_request_accepted) String friendReqAcceptedStr;
        @BindString(R.string.friend_request) String friendRequestStr;
        @BindString(R.string.friend_accepted) String friendAcceptedStr;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            mUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        }

        void bind(Notifications notif, String notifId) {
            String time = TimeRefractor.getTimeAgo(notif.getTimestamp());
            setDetails(notif.getType(), notif.getRole());
            setImageAndClickListener(notifId, notif);
            setTextTypeface(notif.isSeen());
            timestampView.setText(time);

            Timber.i("id = %s",  notif.getUid());
            Timber.i("uid_type= %s", notif.getUid_type());
        }

        private void setTextTypeface(boolean isSeen) {
            if (!isSeen) {
                typeView.setTypeface(Typeface.DEFAULT_BOLD);
                descView.setTypeface(Typeface.DEFAULT_BOLD);
                timestampView.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }

        private void setDetails(String type, String role) {
            switch (type) {
                case "friend_request":
                    typeView.setText(friendRequestStr);
                    if (role.equals("sender")) {
                        descView.setText(sentFriendReqStr);
                    } else {
                        descView.setText(receiveFriendReqStr);
                    }
                    break;

                case "friend_accepted":
                    typeView.setText(friendAcceptedStr);
                    if (role.equals("sender")) {
                        descView.setText(acceptedFriendReqStr);
                    } else {
                        descView.setText(friendReqAcceptedStr);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Invalid type = " + type);
            }
        }

        private void setImageAndClickListener(String notifId, Notifications notif) {
            String notifUidType = notif.getUid_type();
            if (notifUidType.equals("single")) {
                firebaseManager = new FirebaseManager(dataSnapshot -> {

                    if (dataSnapshot.hasChildren()) {

                        Users users = dataSnapshot.getValue(Users.class);
                        users.setUid(notif.getUid());
                        Picasso.get().load(users.getImage()).fit().centerCrop().into(imageView);

                        itemView.setOnClickListener(v -> {

                            if (!notif.isSeen()) {
                                decrementCount(notifId);
                            }
                            Intent profileIntent = new Intent(context, FriendActivity.class);
                            profileIntent.putExtra(FriendActivity.EXTRA_PROFILE_DETAILS, users);
                            context.startActivity(profileIntent);

                        });
                    }

                });

                firebaseManager.startSingleEventListener(mUsersRef.child(notif.getUid()));
            }

        }

        private void decrementCount(String notifId) {
            mNotifCount.child("alerts").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {
                        int count = dataSnapshot.child("count").getValue(Integer.class);
                        int updatedCount = count - 1;
                        mNotifCount.child("alerts").child("count").setValue(updatedCount);
                    }

                    mNotifRef.child(notifId).child("seen").setValue(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ErrorHandler.databaseError(context, databaseError.toException());
                }
            });
        }

    }

}
