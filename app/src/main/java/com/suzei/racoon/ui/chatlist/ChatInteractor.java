package com.suzei.racoon.ui.chatlist;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.model.Chats;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.chatlist.ChatAdapter;

class ChatInteractor {

    private Contract.Listener<ChatAdapter> chatListener;

    private ChatAdapter chatAdapter;

    ChatInteractor(Contract.Listener<ChatAdapter> chatListener) {
        this.chatListener = chatListener;
        initLoad();
    }

    private void initLoad() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        DatabaseReference mUserChatRef = FirebaseDatabase.getInstance().getReference()
                .child("user_chats")
                .child(currentUserId);

        mUserChatRef.keepSynced(true);

        FirebaseRecyclerOptions<Chats> options = new FirebaseRecyclerOptions.Builder<Chats>()
                .setQuery(mUserChatRef, Chats.class)
                .build();

        chatAdapter = new ChatAdapter(options);
        chatListener.onLoadSuccess(chatAdapter);
    }

    public void start() {
        chatAdapter.startListening();
    }

    public void destroy() {
        chatAdapter.stopListening();
    }

}
