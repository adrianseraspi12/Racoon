package com.suzei.racoon.ui.add.search;

import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.base.Contract;

public interface SearchContract {

    interface SearchView extends Contract.ProgressView {

        void setSearchAdapter(SearchAdapter searchAdapter);

        void setSearchUserItemClick(Users users);

        void searchSuccess();

        void searchFailed();

    }

    interface SearchListener {

        void onInitSearchAdapter(SearchAdapter searchAdapter);

        void onSearchUserItemClick(Users users);

        void onSearchSuccess();

        void onSearchFailed();

    }

}
