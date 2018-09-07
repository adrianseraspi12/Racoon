package com.suzei.racoon.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.suzei.racoon.activity.ChatRoomActivity;
import com.suzei.racoon.model.Chats;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.FirebaseExceptionUtil;
import com.suzei.racoon.util.TimeUtil;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends FirebaseRecyclerAdapter<Chats, ChatAdapter.ViewHolder> {

    private DatabaseReference mUserRef;
    private DatabaseReference mGroupRef;

    public ChatAdapter(@NonNull FirebaseRecyclerOptions<Chats> options) {
        super(options);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
        mGroupRef = FirebaseDatabase.getInstance().getReference().child("groups");
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chats model) {
        String uid = getRef(position).getKey();
        holder.bind(model, uid);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_user, parent,
                false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        @BindDrawable(R.drawable.online) Drawable drawableOnline;
        @BindDrawable(R.drawable.offline) Drawable drawableOffline;
        @BindString(R.string.new_conversation) String stringNewConv;
        @BindView(R.id.item_user_image) RoundedImageView imageView;
        @BindView(R.id.item_user_name) TextView nameView;
        @BindView(R.id.item_user_desc) EmojiTextView descView;
        @BindView(R.id.item_user_time) TextView timeView;
        @BindView(R.id.item_user_status) ImageView statusView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        void bind(Chats chats, String uid) {
            String type = chats.getType();
            setTextTypeface(chats.isSeen());
            setTime(chats.getTimestamp());

            switch (type) {
                case "single":
                    showUserData(chats, uid);
                    break;

                case "group":
                    showGroupData(chats, uid);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid chat type = " + type);
            }

        }

        private void setTime(long timestamp) {
            String time = TimeUtil.getTimeAgo(timestamp);
            timeView.setText(time);
        }

        private void setTextTypeface(boolean isSeen) {

            if (!isSeen) {
                descView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                descView.setTypeface(Typeface.DEFAULT);
            }

        }

        private void showUserData(Chats chats, String uid) {
            mUserRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    nameView.setText(users.getName());
                    Picasso.get().load(users.getImage()).into(imageView);

                    if (chats.getLast_message() == null) {
                        descView.setText(stringNewConv);
                    } else {
                        descView.setText(chats.getLast_message());
                    }

                    if (users.isOnline()) {
                        statusView.setImageDrawable(drawableOnline);
                    } else {
                        statusView.setImageDrawable(drawableOffline);
                    }

                    users.setUid(uid);
                    setUserClickListener(users, chats.isSeen());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    FirebaseExceptionUtil.databaseError(itemView.getContext(), error.toException());
                }
            });
        }

        private void showGroupData(Chats chats, String uid) {
            mGroupRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Groups groups = dataSnapshot.getValue(Groups.class);
                    nameView.setText(groups.getName());
                    Picasso.get().load(groups.getImage()).into(imageView);

                    if (chats.getLast_message() == null) {
                        descView.setText(stringNewConv);
                    } else {
                        descView.setText(chats.getLast_message());
                    }

                    groups.setUid(uid);
                    setGroupClickListener(groups, chats.isSeen());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    FirebaseExceptionUtil.databaseError(itemView.getContext(), error.toException());
                }
            });
        }

        private void setGroupClickListener(Groups groups, boolean isSeen) {
            itemView.setOnClickListener(v -> {
                Intent chatActivity = new Intent(context, ChatRoomActivity.class);
                chatActivity.putExtra(ChatRoomActivity.EXTRA_CHAT_TYPE, ChatRoomActivity.ChatType.GROUP_CHAT);
                chatActivity.putExtra(ChatRoomActivity.EXTRA_DETAILS, groups);
                context.startActivity(chatActivity);
            });

        }

        private void setUserClickListener(Users users, boolean isSeen) {
            itemView.setOnClickListener(v -> {
                Intent chatIntent = new Intent(context, ChatRoomActivity.class);
                chatIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_TYPE, ChatRoomActivity.ChatType.SINGLE_CHAT);
                chatIntent.putExtra(ChatRoomActivity.EXTRA_DETAILS, users);
                context.startActivity(chatIntent);
            });
        }

    }

}
