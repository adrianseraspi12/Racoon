package com.suzei.racoon.group;

import com.suzei.racoon.model.Groups;

public class GroupDetailsPresenter implements GroupDetailsContract.GroupDetailsListener {

    private GroupDetailsContract.GroupDetailsView groupView;
    private GroupDetailsInteractor groupDetailsInteractor;

    public GroupDetailsPresenter(GroupDetailsContract.GroupDetailsView groupView) {
        this.groupView = groupView;
        groupDetailsInteractor = new GroupDetailsInteractor(this);
    }

    public void showGroupDetails(String groupId) {
        groupView.showProgress();
        groupDetailsInteractor.startFirebaseDatabaseLoad(groupId);
    }

    public void destroy(String groupId) {
        groupDetailsInteractor.destroy(groupId);
    }

    @Override
    public void onLoadSuccess(Groups groups) {
        groupView.hideProgress();
        groupView.onLoadSuccess(groups);
    }

    @Override
    public void onLoadFailed() {
        groupView.hideProgress();
        groupView.onLoadFailed();
    }
}
