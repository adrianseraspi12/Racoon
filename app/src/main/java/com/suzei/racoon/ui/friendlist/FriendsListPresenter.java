package com.suzei.racoon.ui.friendlist;

import android.content.Context;

import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.base.UsersAdapter;
import com.suzei.racoon.ui.friendlist.FriendsListInteractor;

public class FriendsListPresenter implements Contract.Listener<UsersAdapter> {

    private Contract.AdapterView<UsersAdapter> adapterView;
    private FriendsListInteractor friendsListInteractor;

    FriendsListPresenter(Context context, Contract.AdapterView<UsersAdapter> adapterView) {
        this.adapterView = adapterView;
        friendsListInteractor = new FriendsListInteractor(context, this);
    }

    public void start() {
        adapterView.showProgress();
        friendsListInteractor.performFirebaseDatabaseLoad();
    }

    @Override
    public void onLoadSuccess(UsersAdapter data) {
        adapterView.hideProgress();
        adapterView.setAdapter(data);
    }

    @Override
    public void onLoadFailed(String message) {
        adapterView.hideProgress();
        adapterView.loadFailed();
    }
}
