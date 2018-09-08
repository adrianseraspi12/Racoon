package com.suzei.racoon.profile.data;

import com.suzei.racoon.model.Users;

public interface ProfileContract {

    interface ProfileView {

        void showProgress();

        void hideProgress();

        void onLoadSuccess(Users users);

        void onLoadingFailure();

    }

    interface onProfileListener {

        void onSuccess(Users users);

        void onFailure();

    }

}
