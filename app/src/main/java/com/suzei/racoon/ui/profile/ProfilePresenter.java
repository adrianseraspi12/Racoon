package com.suzei.racoon.ui.profile;

import com.google.firebase.database.DatabaseError;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.profile.ProfileInteractor;

public class ProfilePresenter implements Contract.Listener<Users> {

    private Contract.DetailsView<Users> profileView;
    private ProfileInteractor profileInteractor;

    public ProfilePresenter(Contract.DetailsView<Users> profileView) {
        this.profileView = profileView;
        profileInteractor = new ProfileInteractor(this);
    }

    public void showUserDetails(String uid) {
        profileView.showProgress();
        profileInteractor.loadDetails(uid);
    }

    public void destroy(String uid) {
        profileInteractor.destroy(uid);
    }

    @Override
    public void onLoadSuccess(Users data) {
        profileView.hideProgress();
        profileView.onLoadSuccess(data);
    }

    @Override
    public void onLoadFailed(DatabaseError error) {
        profileView.hideProgress();
        profileView.onLoadFailed(error);
    }
}
