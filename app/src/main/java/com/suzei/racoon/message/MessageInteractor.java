package com.suzei.racoon.message;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.adapter.MessagesAdapter;
import com.suzei.racoon.model.Messages;

public class MessageInteractor {

    private MessageContract.onMessageListener messageListener;
    private MessagesAdapter adapter;

    MessageInteractor(MessageContract.onMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void performFirebaseLoad(String currentUserId, String chatId) {
        DatabaseReference mMessageRef = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(currentUserId).child(chatId);

        adapter = new MessagesAdapter(Messages.class, mMessageRef, 10);

        messageListener.onLoadSuccess(adapter);

    }

    public void loadMore() {

        if (adapter != null && adapter.getItemCount() != 0) {
            adapter.more();
        } else {
            messageListener.onLoadFailed();
        }

    }

    public void destroy() {

        if (adapter != null) {
            adapter.cleanup();
        }

    }

}
