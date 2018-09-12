package com.suzei.racoon.ui.chatroom.single;

import com.suzei.racoon.ui.chatroom.ChatContract;

public class SingleChatPresenter implements ChatContract.ChatListener {

    private ChatContract.ChatView chatView;
    private SingleChatInteractor singleChatInteractor;

    public SingleChatPresenter(ChatContract.ChatView chatView) {
        this.chatView = chatView;
        singleChatInteractor = new SingleChatInteractor(this);
    }

    public void sendMessage(String chatId,
                            String messageType,
                            String message) {

        singleChatInteractor.performFirebaseDatabaseSendSingleMesage(
                chatId,
                messageType,
                message);

    }

    @Override
    public void onSuccess() {
        chatView.sendSuccess();
    }

    @Override
    public void onSendFailed() {
        chatView.sendFailed();
    }

}
