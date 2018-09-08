package com.suzei.racoon.friend.data;

public interface FriendContract {

    interface FriendView {

        void getCurrentState(String currentState);

        void onFailure();

    }

    interface onFriendListener {

        void onSuccess(String currentState);

        void onFailure();

    }

}
