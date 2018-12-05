package com.suzei.racoon.ui.worldlist;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.base.FirebaseManager;
import com.suzei.racoon.ui.chatroom.group.GroupChatActivity;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.util.ErrorHandler;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorldAdapter extends RecyclerView.Adapter<WorldAdapter.ViewHolder>{

    private ArrayList<String> mWorldGroupKeys;

    WorldAdapter(ArrayList<String> groupKeys) {
        this.mWorldGroupKeys = groupKeys;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_user,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = mWorldGroupKeys.get(position);
        holder.bind(key);
    }

    @Override
    public int getItemCount() {
        return mWorldGroupKeys.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements FirebaseManager.FirebaseCallback {

        private FirebaseManager firebaseManager;
        private DatabaseReference mGroupRef;
        private String currentUserId;

        @BindView(R.id.item_user_image) RoundedImageView imageView;
        @BindView(R.id.item_user_name) TextView nameView;
        @BindView(R.id.item_user_desc) EmojiTextView descView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mGroupRef = FirebaseDatabase.getInstance().getReference().child("groups");
            firebaseManager = new FirebaseManager(this);
            currentUserId = FirebaseAuth.getInstance().getUid();
        }

        void bind(String key) {
            firebaseManager.startSingleEventListener(mGroupRef.child(key));
            setClickListener(key);
        }

        private void setClickListener(String id) {
            Context context = itemView.getContext();

            itemView.setOnClickListener(v ->
                    mGroupRef.child(id).child("members").child(currentUserId)
                            .setValue(true).addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Intent chatActivity = new Intent(context, GroupChatActivity.class);
                            chatActivity.putExtra(GroupChatActivity.EXTRA_GROUP_ID, id);
                            context.startActivity(chatActivity);
                        } else {
                            ErrorHandler.databaseError(context, task.getException());
                        }

                    }));

        }

        @Override
        public void onSuccess(DataSnapshot dataSnapshot) {
            Groups groups = dataSnapshot.getValue(Groups.class);

            nameView.setText(groups.getName());
            descView.setText(groups.getDescription());
            Picasso.get().load(groups.getImage()).fit().centerCrop().into(imageView);
        }
    }

}
