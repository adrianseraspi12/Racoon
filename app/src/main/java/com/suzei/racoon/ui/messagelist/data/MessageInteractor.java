package com.suzei.racoon.ui.messagelist.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.messagelist.adapter.MessagesAdapter;
import com.suzei.racoon.model.Messages;

public class MessageInteractor {

    private Contract.Listener<MessagesAdapter> messageListener;
    private MessagesAdapter adapter;

    MessageInteractor(Contract.Listener<MessagesAdapter> messageListener) {
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
            messageListener.onLoadFailed(null);
        }

    }

    public void destroy() {

        if (adapter != null) {
            adapter.cleanup();
        } else {
            messageListener.onLoadFailed(null);
        }

    }

}
