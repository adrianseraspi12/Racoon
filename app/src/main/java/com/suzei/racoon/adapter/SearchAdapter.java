package com.suzei.racoon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.base.Callback;
import com.vanniktech.emoji.EmojiTextView;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private Callback.RecyclerviewListener<Users> mListener;
    private List<Users> userList;

    public SearchAdapter(List<Users> userList, Callback.RecyclerviewListener<Users> listener) {
        this.userList = userList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_user, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = userList.get(position);
        holder.bind(users);
    }

    @Override
    public int getItemCount() {
        return userList.size();
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

        void bind(Users users) {
            String name = users.getName();
            String desc = users.getBio();
            String image = users.getImage();

            nameView.setText(name);
            Picasso.get().load(image).into(imageView);
            if (desc.equals("")) {
                descView.setText(personIsPrivate);
            } else {
                descView.setText(desc);
            }

            itemView.setOnClickListener(v -> {
                removeAt(getAdapterPosition());
                mListener.onItemClick(users, itemView);
            });
        }

        private void removeAt(int position) {
            if (userList.size() != 0) {
                userList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, userList.size());
            }
        }
    }
}
