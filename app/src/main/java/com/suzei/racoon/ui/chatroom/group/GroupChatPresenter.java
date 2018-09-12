package com.suzei.racoon.ui.chatroom.group;

import com.suzei.racoon.ui.chatroom.ChatContract;

import java.util.ArrayList;

public class GroupChatPresenter implements ChatContract.ChatListener {

    private ChatContract.ChatView chatView;
    private GroupChatInteractor chatInteractor;

    public GroupChatPresenter(ChatContract.ChatView chatView) {
        this.chatView = chatView;
        chatInteractor = new GroupChatInteractor(this);
    }

    public void sendGroupMessage(String groupId,
                                 String currentUserId,
                                 String messageType,
                                 ArrayList<String> members,
                                 String message) {
        chatInteractor.performFirebaseDatabaseSendGroupMessage(
                groupId,
                currentUserId,
                messageType,
                members,
                message);
    }

    public void onTyping() {
        //TODO if the user is typing, show bubble chat loading
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
