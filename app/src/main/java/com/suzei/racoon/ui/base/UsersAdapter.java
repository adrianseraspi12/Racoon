package com.suzei.racoon.ui.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.callback.UsersListener;
import com.suzei.racoon.model.Users;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private UsersListener mListener;
    private DatabaseReference mUserRef;
    private ArrayList<String> userKeys;

    public UsersAdapter(ArrayList<String> friendKeys, UsersListener listener) {
        this.userKeys = friendKeys;
        this.mListener = listener;
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
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
        String key = userKeys.get(position);
        holder.bind(key);
    }

    @Override
    public int getItemCount() {
        return userKeys.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindString(R.string.person_is_private) String personIsPrivate;
        @BindView(R.id.item_user_image) RoundedImageView imageView;
        @BindView(R.id.item_user_name) TextView nameView;
        @BindView(R.id.item_user_desc) EmojiTextView descView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String key) {
            mUserRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    nameView.setText(users.getName());
                    Picasso.get().load(users.getImage()).into(imageView);
                    users.setUid(key);

                    if (users.getBio().equals("")) {
                        descView.setText(personIsPrivate);
                    } else {
                        descView.setText(users.getBio());
                    }

                    if (mListener != null) {
                        itemView.setOnClickListener(v -> mListener.onItemClickListener(users,
                                itemView));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
