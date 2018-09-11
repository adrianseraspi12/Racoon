package com.suzei.racoon.ui.search;

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

    public void addFromSearch(Users users) {
        searchInteractor.addUser(users);
    }

    public void removeFromSearch(Users users) {
        searchInteractor.removeUser(users);
    }

    @Override
    public void onInitSearchAdapter(SearchAdapter searchAdapter) {
        searchView.setSearchAdapter(searchAdapter);
    }

    @Override
    public void onSearchUserItemClick(Users users) {
        searchView.setSearchUserItemClick(users);
    }

    @Override
    public void onSearchSuccess() {
        searchView.searchSuccess();
    }

    @Override
    public void onSearchFailed() {
        searchView.searchFailed();
    }

}
