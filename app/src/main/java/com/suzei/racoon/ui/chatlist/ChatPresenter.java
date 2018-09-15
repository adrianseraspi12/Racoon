package com.suzei.racoon.ui.chatlist;

import com.google.firebase.database.DatabaseError;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.chatlist.ChatAdapter;
import com.suzei.racoon.ui.chatlist.ChatInteractor;

public class ChatPresenter implements Contract.Listener<ChatAdapter> {

    private Contract.AdapterView<ChatAdapter> adapterView;
    private ChatInteractor chatInteractor;

    public ChatPresenter(Contract.AdapterView<ChatAdapter> adapterView) {
        this.adapterView = adapterView;
        chatInteractor = new ChatInteractor(this);
    }

    public void start() {
        chatInteractor.start();
    }

    public void destroy() {
        chatInteractor.destroy();
    }

    @Override
    public void onLoadSuccess(ChatAdapter data) {
        adapterView.setAdapter(data);
    }

    @Override
    public void onLoadFailed(String message) {

    }
}
