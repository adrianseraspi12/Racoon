package com.suzei.racoon.ui.friend;

import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.friend.FriendInteractor;

public class FriendPresenter implements Contract.Listener<String> {

    private Contract.DetailsView<String> friendView;
    private FriendInteractor friendInteractor;

    public FriendPresenter(Contract.DetailsView<String> friendView) {
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
    public void onLoadSuccess(String data) {
        friendView.onLoadSuccess(data);
    }

    @Override
    public void onLoadFailed(String message) {
        friendView.hideProgress();
        friendView.onLoadSuccess(message);
    }
}
