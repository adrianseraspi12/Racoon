package com.suzei.racoon.ui.add;

import com.suzei.racoon.ui.base.Contract;

public interface CreateGroupContract {

    interface CreateGroupView extends Contract.ProgressView {

        void createSuccess(String id);

        void createFailed();

    }

    interface CreateGroupListener {

        void onCreateSuccess(String id);

        void onCreateFailed();

    }

}
