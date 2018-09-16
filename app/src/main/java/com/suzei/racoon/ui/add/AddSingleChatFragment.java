package com.suzei.racoon.ui.add;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import android.widget.TextView;

import com.suzei.racoon.R;
import com.suzei.racoon.ui.add.search.SearchAdapter;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.chatroom.single.SingleChatActivity;
import com.suzei.racoon.ui.add.search.SearchContract;
import com.suzei.racoon.ui.add.search.SearchPresenter;
import com.suzei.racoon.view.EmptyRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddSingleChatFragment extends Fragment implements SearchContract.SearchView {

    private SearchPresenter searchPresenter;

    @BindView(R.id.pick_user_no_result) TextView noResultView;
    @BindView(R.id.pick_user_loading) ProgressBar progressBarView;
    @BindView(R.id.pick_user_back) ImageButton backView;
    @BindView(R.id.pick_user_search_text) EditText searchUserView;
    @BindView(R.id.pick_user_search) ImageButton searchView;
    @BindView(R.id.pick_user_search_list) EmptyRecyclerView listSearchUserView;

    public AddSingleChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pick_user, container, false);
        initObjects(view);
        setUpRecyclerViews();
        return view;
    }

    private void initObjects(View view) {
        ButterKnife.bind(this, view);
        searchPresenter = new SearchPresenter(this);
    }

    private void setUpRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        listSearchUserView.setLayoutManager(layoutManager);
        listSearchUserView.setItemAnimator(new DefaultItemAnimator());
        listSearchUserView.setEmptyView(noResultView);
    }

    @OnClick(R.id.pick_user_back)
    public void onBackClick() {
        getActivity().finish();
    }

    @OnClick(R.id.pick_user_search)
    public void onSearchClick() {
        ArrayList<Users> users = new ArrayList<>();

        String query = searchUserView.getText().toString().trim();
        searchPresenter.startSearch(query, users);
    }

    @Override
    public void setSearchAdapter(SearchAdapter searchAdapter) {
        listSearchUserView.setAdapter(searchAdapter);
    }

    @Override
    public void setSearchUserItemClick(Users users) {
        Intent chatRoomIntent = new Intent(getContext(), SingleChatActivity.class);
        chatRoomIntent.putExtra(SingleChatActivity.EXTRA_ID, users.getUid());
        startActivity(chatRoomIntent);
        getActivity().finish();
    }

    @Override
    public void searchSuccess() {
    }

    @Override
    public void searchFailed() {

    }

    @Override
    public void showProgress() {
        searchView.setEnabled(false);
        progressBarView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        searchView.setEnabled(true);
        progressBarView.setVisibility(View.GONE);
    }
}