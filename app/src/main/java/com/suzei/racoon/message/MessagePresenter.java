package com.suzei.racoon.message;

import com.suzei.racoon.adapter.MessagesAdapter;

public class MessagePresenter implements MessageContract.onMessageListener {

    private MessageContract.MessageView messageView;
    private MessageInteractor messageInteractor;

    public MessagePresenter(MessageContract.MessageView messageView) {
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
    public void onLoadFailed() {
        messageView.loadFailed();
    }

}
