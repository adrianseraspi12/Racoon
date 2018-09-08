package com.suzei.racoon.friend.data;

import com.suzei.racoon.friend.data.FriendContract;
import com.suzei.racoon.friend.data.FriendInteractor;

public class FriendPresenter implements FriendContract.onFriendListener {

    private FriendContract.FriendView friendView;
    private FriendInteractor friendInteractor;

    public FriendPresenter(FriendContract.FriendView friendView) {
        this.friendView = friendView;
        friendInteractor = new FriendInteractor(this);
    }

    public void sendRequest(String currentUserId, String friendId, String currentState) {
        friendInteractor.executeRequest(currentUserId, friendId, currentState);
    }

    public void readCurrentState(String currentUserId, String friendId) {
        friendInteractor.readCurrentState(currentUserId, friendId);
    }

    public void destroy(String currentUserId, String friendId) {
        friendInteractor.destroyReading(currentUserId, friendId);
    }

    @Override
    public void onSuccess(String currentState) {
        friendView.getCurrentState(currentState);
    }

    @Override
    public void onFailure() {
        //TODO show fail to load layout
    }
}
