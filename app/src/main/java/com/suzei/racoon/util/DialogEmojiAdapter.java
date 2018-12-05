package com.suzei.racoon.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Emoji;
import com.suzei.racoon.ui.base.Callback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogEmojiAdapter extends FirebaseRecyclerAdapter<Emoji, DialogEmojiAdapter.ViewHolder> {

    private Callback.RecyclerviewListener<String> mListener;

    public DialogEmojiAdapter(@NonNull FirebaseRecyclerOptions<Emoji> options,
                              Callback.RecyclerviewListener<String> listener) {
        super(options);
        this.mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Emoji model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_dialog_emoji_picker, parent, false);
        return new ViewHolder(view);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_emoji_name)
        TextView nameView;
        @BindView(R.id.item_emoji_image)
        RoundedImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Emoji emoji) {
            String name = emoji.getName();
            String image = emoji.getImage();

            nameView.setText(name);
            Picasso.get().load(image).noFade().fit().centerCrop().into(imageView);

            itemView.setOnClickListener(v -> mListener.onItemClick(image, itemView));
        }
    }

    public interface EmojiAdapterListener {
        void onPickEmoji(String image);
    }
}
