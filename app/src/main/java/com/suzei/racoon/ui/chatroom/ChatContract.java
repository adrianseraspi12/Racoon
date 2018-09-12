package com.suzei.racoon.ui.chatroom;

public interface ChatContract {

    interface ChatView {

        void sendSuccess();

        void sendFailed();

    }

    interface ChatListener {

        void onSuccess();

        void onSendFailed();

    }

}
