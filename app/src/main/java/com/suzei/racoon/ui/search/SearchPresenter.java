package com.suzei.racoon.ui.search;

import com.suzei.racoon.adapter.SearchAdapter;
import com.suzei.racoon.model.Users;

import java.util.ArrayList;

public class SearchPresenter implements SearchContract.SearchListener {

    private SearchContract.SearchView searchView;
    private SearchInteractor searchInteractor;

    public SearchPresenter(SearchContract.SearchView searchView) {
        this.searchView = searchView;
        searchInteractor = new SearchInteractor(this);
        searchInteractor.initSearchAdapter();
    }

    public void startSearch(String query, ArrayList<Users> selectedUsers) {
        searchView.showProgress();
        searchInteractor.performFirebaseDatabaseSearch(query, selectedUsers);
    }

    public void updateList() {
        searchInteractor.updateSearchAdapter();
    }

    @Override
    public void onInitSearchAdapter(SearchAdapter searchAdapter) {
        searchView.setSearchAdapter(searchAdapter);
    }

    @Override
    public void onSearchUser(ArrayList<Users> searchUserList) {
        searchView.getSearchList(searchUserList);
    }

    @Override
    public void onSearchUserItemClick() {
        searchView.setSearchUserItemClick();
    }

    @Override
    public void onSearchFailed() {
        searchView.searchFailed();
    }

}
