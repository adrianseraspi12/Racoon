package com.suzei.racoon.message;

import com.suzei.racoon.adapter.MessagesAdapter;

public interface MessageContract {

    interface MessageView {

        void setAdapter(MessagesAdapter adapter);

        void loadFailed();

    }

    interface onMessageListener {

        void onLoadSuccess(MessagesAdapter adapter);

        void onLoadFailed();

    }

}
