package com.suzei.racoon.ui.add.search;

import com.suzei.racoon.model.Users;

public interface SelectUserContract {

    interface SelectUserView {

        void setSelectUserAdapter(SelectedAdapter selectUserAdapter);

        void setSelectUserItemClick(Users users);

        void selectUserFailed();

    }

    interface SelectUserListener {

        void onInitSelectedAdapter(SelectedAdapter searchAdapter);

        void onSelectUserItemClick(Users users);

        void onSelectFailed();

    }

}
