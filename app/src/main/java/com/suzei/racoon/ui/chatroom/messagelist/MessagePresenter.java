package com.suzei.racoon.ui.chatroom.messagelist;

import com.suzei.racoon.ui.base.Contract;

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
    public void onLoadFailed(String message) {
        messageView.loadFailed();
    }

}
