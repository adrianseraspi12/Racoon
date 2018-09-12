package com.suzei.racoon.ui.chatroom.group;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.suzei.racoon.ui.chatroom.ChatContract;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatInteractor {

    private ChatContract.ChatListener chatListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserChatRef;

    GroupChatInteractor(ChatContract.ChatListener chatListener) {
        this.chatListener = chatListener;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserChatRef = mRootRef.child("user_chats");
    }

    public void performFirebaseDatabaseSendGroupMessage(String groupId,
                                                        String currentUserId,
                                                        String messageType,
                                                        ArrayList<String> members,
                                                        String message) {
        String messageId = mRootRef.push().getKey();
        HashMap<String, Object> messageDetailsMap = getMessageDetailsMap(
                currentUserId,
                message,
                messageType,
                members);

        HashMap<String, Object> saveMessageMap = new HashMap<>();
        for (int i = 0; i < members.size(); i++) {
            String member = members.get(i);
            saveMessageMap.put("messages" + "/" + member + "/" + groupId + "/" + messageId,
                    messageDetailsMap);
        }

        mRootRef.updateChildren(saveMessageMap, (databaseError, databaseReference) -> {

            if (databaseError == null) {
                updateChatDB(groupId, currentUserId, messageType, members, message);
                chatListener.onSuccess();
            } else {
                chatListener.onSendFailed();
            }

        });
    }

    private void updateChatDB(String groupId,
                              String currentUserId,
                              String messageType,
                              ArrayList<String> members,
                              String message) {

        HashMap<String, Object> chatDetailMap = new HashMap<>();
        chatDetailMap.put("timestamp", ServerValue.TIMESTAMP);
        chatDetailMap.put("last_message", getMessage(messageType, message));
        chatDetailMap.put("type", "group");

        HashMap<String, Object> chatMap = new HashMap<>();
        for (int i = 0; i < members.size(); i++) {
            String member = members.get(i);

            if (member.equals(currentUserId)) {
                chatDetailMap.put("seen", true);
            } else {
                chatDetailMap.put("seen", false);
            }

            chatMap.put(member + "/" + groupId, chatDetailMap);
        }

        mUserChatRef.updateChildren(chatMap);
    }

    private String getMessage(String type, String message) {
        switch (type) {

            case "text":
                return message;

            case "image":
                return "Sent an image";
            default:
                throw new IllegalArgumentException("Invalid type=" + type);
        }
    }

    private HashMap<String, Object> getMessageDetailsMap(String currentUserId,
                                                         String message,
                                                         String type,
                                                         ArrayList<String> members) {
        HashMap<String, Object> seenMap = new HashMap<>();
        for (int i = 0; i < members.size(); i++) {
            String member = members.get(i);

            if (member.equals(currentUserId)) {
                seenMap.put(member, true);
            } else {
                seenMap.put(member, false);
            }

        }

        HashMap<String, Object> messageDetailsMap = new HashMap<>();
        messageDetailsMap.put("message", message);
        messageDetailsMap.put("type", type);
        messageDetailsMap.put("from", currentUserId);
        messageDetailsMap.put("timestamp", ServerValue.TIMESTAMP);
        messageDetailsMap.put("seen", seenMap);

        return messageDetailsMap;
    }


}
