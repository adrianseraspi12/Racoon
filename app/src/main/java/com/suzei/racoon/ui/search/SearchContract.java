package com.suzei.racoon.ui.search;

import com.suzei.racoon.adapter.SearchAdapter;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.base.Contract;

import java.util.ArrayList;

public interface SearchContract {

    interface SearchView extends Contract.ProgressView {

        void setSearchAdapter(SearchAdapter searchAdapter);

        void setSearchUserItemClick(Users users);

        void searchFailed();

    }

    interface SearchListener {

        void onInitSearchAdapter(SearchAdapter searchAdapter);

        void onSearchUserItemClick(Users users);

        void onSearchFailed();

    }

}
