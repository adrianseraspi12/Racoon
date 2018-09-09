package com.suzei.racoon.ui.messagelist.data;

import com.google.firebase.database.DatabaseError;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.messagelist.adapter.MessagesAdapter;

public class MessagePresenter implements Contract.Listener<MessagesAdapter> {

    private Contract.AdapterView<MessagesAdapter> messageView;
    private MessageInteractor messageInteractor;

    public MessagePresenter(Contract.AdapterView<MessagesAdapter> messageView) {
        this.messageView = messageView;
        messageInteractor = new MessageInteractor(this);
    }

    public void start(String currentUserId, String chatId) {
        messageInteractor.performFirebaseLoad(currentUserId, chatId);
    }

    public void loadMore() {
        messageInteractor.loadMore();
    }

    public void destroy() {
        messageInteractor.destroy();
    }

    @Override
    public void onLoadSuccess(MessagesAdapter adapter) {
        messageView.setAdapter(adapter);
    }

    @Override
    public void onLoadFailed(DatabaseError error) {
        messageView.loadFailed();
    }

}
