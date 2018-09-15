package com.suzei.racoon.ui.add;

import android.content.Context;

import com.suzei.racoon.model.Users;

import java.util.ArrayList;

public class CreateGroupPresenter implements CreateGroupContract.CreateGroupListener {

    private CreateGroupContract.CreateGroupView crudView;
    private CreateGroupInteractor crudInteractor;

    public CreateGroupPresenter(Context context, CreateGroupContract.CreateGroupView crudView) {
        this.crudView = crudView;
        crudInteractor = new CreateGroupInteractor(context, this);

    }

    public void create(ArrayList<Users> members) {
        crudView.showProgress();
        crudInteractor.createGroup(members);
    }

    @Override
    public void onCreateSuccess(String id) {
        crudView.hideProgress();
        crudView.createSuccess(id);
    }

    @Override
    public void onCreateFailed() {
        crudView.hideProgress();
        crudView.createFailed();
    }

}
