package com.suzei.racoon.group;

import com.suzei.racoon.model.Groups;

public interface GroupDetailsContract {

    interface GroupDetailsView {

        void showProgress();

        void hideProgress();

        void onLoadSuccess(Groups groups);

        void onLoadFailed();

    }

    interface GroupDetailsListener {

        void onLoadSuccess(Groups groups);

        void onLoadFailed();

    }

}
