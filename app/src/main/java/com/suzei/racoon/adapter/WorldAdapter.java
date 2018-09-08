package com.suzei.racoon.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.suzei.racoon.chat.ui.GroupChatActivity;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.util.FirebaseExceptionUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorldAdapter extends RecyclerView.Adapter<WorldAdapter.ViewHolder>{

    private ArrayList<String> mWorldGroupKeys;
    private DatabaseReference mGroupRef;
    private String currentUserId;

    public WorldAdapter(ArrayList<String> groupKeys) {
        this.mWorldGroupKeys = groupKeys;
        currentUserId = FirebaseAuth.getInstance().getUid();
        mGroupRef = FirebaseDatabase.getInstance().getReference().child("groups");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_world_chat,
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

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_world_chat_root_view) FrameLayout rootView;
        @BindView(R.id.item_world_menu) ImageButton menuView;
        @BindView(R.id.item_world_image) RoundedImageView imageView;
        @BindView(R.id.item_world_name) TextView nameView;
        @BindView(R.id.item_world_desc) TextView descView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String key) {
            mGroupRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Groups groups = dataSnapshot.getValue(Groups.class);

                    nameView.setText(groups.getName());
                    descView.setText(groups.getDescription());
                    Picasso.get().load(groups.getImage()).into(imageView);
                    setClickListener(key);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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
                            FirebaseExceptionUtil.databaseError(context, task.getException());
                        }

                    }));

        }
    }

}
