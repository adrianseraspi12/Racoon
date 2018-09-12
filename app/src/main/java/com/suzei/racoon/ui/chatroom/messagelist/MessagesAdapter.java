package com.suzei.racoon.ui.chatroom.messagelist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Messages;
import com.suzei.racoon.util.pagination.InfiniteFirebaseRecyclerAdapter;
import com.vanniktech.emoji.EmojiTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MessagesAdapter extends InfiniteFirebaseRecyclerAdapter<Messages, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENDER = 0;
    private static final int VIEW_TYPE_RECEIVER = 1;

    /**
     * @param modelClass   Firebase will marshall the data at a location into an instance of a class that you provide
     * @param ref          The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                     combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param itemsPerPage
     */
    public MessagesAdapter(Class<Messages> modelClass, Query ref, int itemsPerPage) {
        super(modelClass, ref, itemsPerPage);
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = getItem(position);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        String sender = messages.getFrom();

        if (sender.equals(currentUserId)) {
            return VIEW_TYPE_SENDER;
        } else {
            return VIEW_TYPE_RECEIVER;
        }

    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @NonNull Messages model, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {

            case VIEW_TYPE_SENDER:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                senderViewHolder.bind(model);
                break;

            case VIEW_TYPE_RECEIVER:
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                receiverViewHolder.bind(model);
                break;

            default:
                throw new IllegalArgumentException("Invalid viewtype= " + viewType);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {

            case VIEW_TYPE_SENDER:
                View senderView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_item_sender_message, parent, false);

                return new SenderViewHolder(senderView);

            case VIEW_TYPE_RECEIVER:
                View receiverView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_item_receiver_message, parent, false);

                return new ReceiverViewHolder(receiverView);

            default:
                throw new IllegalArgumentException("Invalid Viewtype = " + viewType);
        }

    }

    class SenderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_sender_text_message) EmojiTextView messageView;
        @BindView(R.id.item_sender_picture_message) RoundedImageView imageView;

        SenderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Messages message) {
            String type = message.getType();
            switch (type) {

                case "text":
                    messageView.setText(message.getMessage());
                    imageView.setVisibility(View.GONE);
                    break;

                case "image":
                    Picasso.get().load(message.getMessage()).fit().centerCrop().into(imageView);
                    messageView.setVisibility(View.GONE);
                    Timber.i(message.getMessage());
                    break;

            }

            Timber.i("Sender= %s", message.getFrom());
            Timber.i("Time= %s", message.getTimestamp());
            Timber.i("Message= %s", message.getMessage());

        }

    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_receiver_image) RoundedImageView imageView;
        @BindView(R.id.item_receiver_picture_message) RoundedImageView messageImageView;
        @BindView(R.id.item_receiver_text_message) EmojiTextView messageView;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Messages message) {
            String type = message.getType();
            switch (type) {

                case "text":
                    messageView.setText(message.getMessage());
                    messageImageView.setVisibility(View.GONE);
                    break;

                case "image":
                    Picasso.get().load(message.getMessage()).fit().centerCrop().into(messageImageView);
                    messageView.setVisibility(View.GONE);
                    break;

            }

            Timber.i("Sender= %s", message.getFrom());
            Timber.i("Time= %s", message.getTimestamp());
            Timber.i("Message= %s", message.getMessage());

        }

    }

}
