package com.suzei.racoon.ui.search;

import com.suzei.racoon.model.Users;

public class SelectPresenter implements SelectUserContract.SelectUserListener {

    private SelectUserContract.SelectUserView selectUserView;
    private SelectInteractor selectInteractor;

    public SelectPresenter(SelectUserContract.SelectUserView selectUserView) {
        this.selectUserView = selectUserView;
        selectInteractor = new SelectInteractor(this);
        selectInteractor.initSelectAdapter();
    }

    public int getItemCount() {
        return selectInteractor.getItemCount();
    }

    public void addSelectedUser(Users users) {
        selectInteractor.addSelectedUser(users);
    }

    public void removeSelectedUser(Users users) {
        selectInteractor.removeSelectedUser(users);
    }

    @Override
    public void onInitSelectedAdapter(SelectedAdapter searchAdapter) {
        selectUserView.setSelectUserAdapter(searchAdapter);
    }

    @Override
    public void onSelectUserItemClick(Users users) {
        selectUserView.setSelectUserItemClick(users);
    }

    @Override
    public void onSelectFailed() {

    }

}
