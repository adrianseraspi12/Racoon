package com.suzei.racoon.ui.chatlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.base.FirebaseManager;
import com.suzei.racoon.ui.chatroom.group.GroupChatActivity;
import com.suzei.racoon.ui.chatroom.single.SingleChatActivity;
import com.suzei.racoon.model.Chats;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.lib.TimeRefractor;
import com.vanniktech.emoji.EmojiTextView;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends FirebaseRecyclerAdapter<Chats, ChatAdapter.ViewHolder> {

    private DatabaseReference mUserRef;
    private DatabaseReference mGroupRef;

    ChatAdapter(@NonNull FirebaseRecyclerOptions<Chats> options) {
        super(options);
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
        private FirebaseManager firebaseManager;

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
                    setUserClickListener(uid);
                    break;

                case "group":
                    showGroupData(chats, uid);
                    setGroupClickListener(uid);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid chat type = " + type);
            }

        }

        private void setTime(long timestamp) {
            String time = TimeRefractor.getTimeAgo(timestamp);
            timeView.setText(time);
        }

        private void setTextTypeface(boolean isSeen) {

            if (!isSeen) {
                descView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                descView.setTypeface(Typeface.DEFAULT);
            }

        }

        private void showGroupData(Chats chats, String uid) {
            firebaseManager = new FirebaseManager(dataSnapshot -> {

                Groups groups = dataSnapshot.getValue(Groups.class);
                nameView.setText(groups.getName());
                Picasso.get().load(groups.getImage()).fit().centerCrop().into(imageView);

                if (chats.getLast_message() == null) {
                    descView.setText(stringNewConv);
                } else {
                    descView.setText(chats.getLast_message());
                }

            });

            firebaseManager.startSingleEventListener(mGroupRef.child(uid));
        }

        private void showUserData(Chats chats, String uid) {
            firebaseManager = new FirebaseManager(dataSnapshot -> {

                Users users = dataSnapshot.getValue(Users.class);
                nameView.setText(users.getName());
                Picasso.get().load(users.getImage()).fit().centerCrop().into(imageView);

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

            });

            firebaseManager.startSingleEventListener(mUserRef.child(uid));
        }

        private void setGroupClickListener(String id) {
            itemView.setOnClickListener(v -> {
                Intent chatActivity = new Intent(context, GroupChatActivity.class);
                chatActivity.putExtra(GroupChatActivity.EXTRA_GROUP_ID, id);
                context.startActivity(chatActivity);
            });
        }

        private void setUserClickListener(String id) {
            itemView.setOnClickListener(v -> {
                Intent chatIntent = new Intent(context, SingleChatActivity.class);
                chatIntent.putExtra(SingleChatActivity.EXTRA_ID, id);
                context.startActivity(chatIntent);
            });
        }

    }

}
