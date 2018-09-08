package com.suzei.racoon.chat;

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
