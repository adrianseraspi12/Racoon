package com.suzei.racoon.ui.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Users;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Callback.RecyclerviewListener<Users> mListener;
    private ArrayList<String> userKeys;

    public UsersAdapter(ArrayList<String> friendKeys,
                        Callback.RecyclerviewListener<Users> listener) {
        this.userKeys = friendKeys;
        this.mListener = listener;
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

    class ViewHolder extends RecyclerView.ViewHolder implements FirebaseManager.FirebaseCallback {

        private DatabaseReference mUserRef;
        private FirebaseManager firebaseManager;

        @BindString(R.string.person_is_private) String personIsPrivate;
        @BindView(R.id.item_user_image) RoundedImageView imageView;
        @BindView(R.id.item_user_name) TextView nameView;
        @BindView(R.id.item_user_desc) EmojiTextView descView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            firebaseManager = new FirebaseManager(this);
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
        }

        void bind(String key) {
            firebaseManager.startSingleEventListener(mUserRef.child(key));
        }

        @Override
        public void onSuccess(DataSnapshot dataSnapshot) {

            if (dataSnapshot.hasChildren()) {
                Users users = dataSnapshot.getValue(Users.class);
                nameView.setText(users.getName());
                Picasso.get().load(users.getImage()).fit().centerCrop().into(imageView);
                users.setUid(dataSnapshot.getKey());

                if (users.getBio().equals("")) {
                    descView.setText(personIsPrivate);
                } else {
                    descView.setText(users.getBio());
                }

                if (mListener != null) {
                    itemView.setOnClickListener(v -> mListener.onItemClick(users,
                            itemView));
                }
            }
        }
    }

}
