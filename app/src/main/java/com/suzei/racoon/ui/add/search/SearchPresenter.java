package com.suzei.racoon.ui.add.search;

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
        searchInteractor.performFirebaseDatabaseSearch(query, selectedUsers, null);
    }

    public void startSearchExcludeOldList(String query,
                                          ArrayList<Users> selectedUsers,
                                          ArrayList<String> oldList) {
        searchView.showProgress();
        searchInteractor.performFirebaseDatabaseSearch(query, selectedUsers, oldList);
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
        searchView.hideProgress();
        searchView.searchSuccess();
    }

    @Override
    public void onSearchFailed() {
        searchView.searchFailed();
    }

}
