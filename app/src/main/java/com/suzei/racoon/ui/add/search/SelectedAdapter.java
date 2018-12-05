package com.suzei.racoon.ui.add.search;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.base.Callback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.ViewHolder> {

    private Callback.RecyclerviewListener<Users> mListener;
    private List<Users> selectedList;

    public SelectedAdapter(List<Users> selectedList, Callback.RecyclerviewListener<Users> listener) {
        this.selectedList = selectedList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_user_image,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = selectedList.get(position);
        holder.bind(users);
    }

    @Override
    public int getItemCount() {
        return selectedList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_user_image) RoundedImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Users users) {
            String image = users.getImage();
            Picasso.get().load(image).fit().centerCrop().into(imageView);
            itemView.setOnClickListener(v -> mListener.onItemClick(users, itemView));
        }
    }
}
