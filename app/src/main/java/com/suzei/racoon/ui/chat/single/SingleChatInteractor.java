package com.suzei.racoon.ui.chat.single;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.suzei.racoon.ui.chat.ChatContract;

import java.util.HashMap;

public class SingleChatInteractor {

    private ChatContract.ChatListener chatListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mUsersChatRef;

    SingleChatInteractor(ChatContract.ChatListener chatListener) {
        this.chatListener = chatListener;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersChatRef = mRootRef.child("user_chats");
    }

    public void performFirebaseDatabaseSendSingleMesage(String chatId,
                                                        String currentUserId,
                                                        String messageType,
                                                        String message) {
        String messageId = mRootRef.child("messages").child(currentUserId).push().getKey();

        HashMap<String, Object> messageMap =
                getMessageDetails(chatId, currentUserId, messageType, message);


        HashMap<String, Object> saveMessageMap = new HashMap<>();
        saveMessageMap.put("messages/" + currentUserId + "/" + chatId + "/" + messageId, messageMap);
        saveMessageMap.put("messages/" + chatId + "/" + currentUserId + "/" + messageId, messageMap);

        mRootRef.updateChildren(saveMessageMap, (databaseError, databaseReference) -> {

            if (databaseError == null) {
                updateChatDB(chatId, currentUserId, messageType, message);
                chatListener.onSuccess();
            } else {
                chatListener.onSendFailed();
            }

        });
    }

    private void updateChatDB(String chatId,
                              String currentUserId,
                              String messageType,
                              String message) {

        HashMap<String, Object> chatDetailMap = new HashMap<>();
        chatDetailMap.put("timestamp", ServerValue.TIMESTAMP);
        chatDetailMap.put("last_message", getMessage(messageType, message));
        chatDetailMap.put("type", "single");

        HashMap<String, Object> chatMap = new HashMap<>();

        chatDetailMap.put("seen", false);
        chatMap.put(chatId + "/" + currentUserId, chatDetailMap);

        chatDetailMap.put("seen", true);
        chatMap.put(currentUserId + "/" + chatId, chatDetailMap);

        mUsersChatRef.updateChildren(chatMap);
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

    private HashMap<String, Object> getMessageDetails(String chatId,
                                                      String currentUserId,
                                                      String messageType,
                                                      String message) {
        HashMap<String, Object> seenMap = new HashMap<>();
        seenMap.put(currentUserId, true);
        seenMap.put(chatId, false);

        HashMap<String, Object> messageDetailsMap = new HashMap<>();
        messageDetailsMap.put("message", message);
        messageDetailsMap.put("type", messageType);
        messageDetailsMap.put("from", currentUserId);
        messageDetailsMap.put("timestamp", ServerValue.TIMESTAMP);
        messageDetailsMap.put("seen", seenMap);

        return messageDetailsMap;
    }

}
