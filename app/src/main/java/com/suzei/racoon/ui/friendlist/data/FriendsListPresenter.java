package com.suzei.racoon.ui.friendlist.data;

import com.google.firebase.database.DatabaseError;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.base.UsersAdapter;

public class FriendsListPresenter implements Contract.Listener<UsersAdapter> {

    private Contract.AdapterView<UsersAdapter> adapterView;
    private FriendsListInteractor friendsListInteractor;

    public FriendsListPresenter(Contract.AdapterView<UsersAdapter> adapterView) {
        this.adapterView = adapterView;
        friendsListInteractor = new FriendsListInteractor(this);
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
    public void onLoadFailed(DatabaseError error) {
        adapterView.hideProgress();
        adapterView.loadFailed();
    }
}
