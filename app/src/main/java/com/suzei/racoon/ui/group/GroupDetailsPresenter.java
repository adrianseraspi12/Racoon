package com.suzei.racoon.ui.group;

import com.suzei.racoon.model.Groups;
import com.suzei.racoon.ui.base.Contract;

public class GroupDetailsPresenter implements Contract.Listener<Groups> {

    private Contract.DetailsView<Groups> groupView;
    private GroupDetailsInteractor groupDetailsInteractor;

    public GroupDetailsPresenter(Contract.DetailsView<Groups> groupView) {
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
    public void onLoadFailed(String message) {
        groupView.hideProgress();
        groupView.onLoadFailed(message);
    }

}
