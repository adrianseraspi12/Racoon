package com.suzei.racoon.ui.add;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suzei.racoon.R;
import com.suzei.racoon.ui.add.search.SearchAdapter;
import com.suzei.racoon.ui.add.search.SelectedAdapter;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.chatroom.group.GroupChatActivity;
import com.suzei.racoon.ui.add.search.SearchContract;
import com.suzei.racoon.ui.add.search.SearchPresenter;
import com.suzei.racoon.ui.add.search.SelectPresenter;
import com.suzei.racoon.ui.add.search.SelectUserContract;
import com.suzei.racoon.util.view.EmptyRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGroupChatFragment extends Fragment implements
        SearchContract.SearchView,
        SelectUserContract.SelectUserView,
        CreateGroupContract.CreateGroupView {

    private SearchPresenter searchPresenter;
    private SelectPresenter selectPresenter;
    private CreateGroupPresenter createGroupPresenter;

    private ArrayList<Users> selectedUserList = new ArrayList<>();

    @BindView(R.id.pick_user_no_result) TextView noResultView;
    @BindView(R.id.pick_user_loading) ProgressBar progressBarView;
    @BindView(R.id.pick_user_back) ImageButton backView;
    @BindView(R.id.pick_user_search_text) EditText searchUserView;
    @BindView(R.id.pick_user_search) ImageButton searchView;
    @BindView(R.id.pick_user_selected_list_layout) RelativeLayout userSelectedLayout;
    @BindView(R.id.pick_user_selected_list) RecyclerView listSelectedUserView;
    @BindView(R.id.pick_user_add) Button addView;
    @BindView(R.id.pick_user_search_list) EmptyRecyclerView listSearchUserView;

    public AddGroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pick_user, container, false);
        initObjects(view);
        setUpRecyclerViews();
        setUpPresenters();
        return view;
    }

    private void initObjects(View view) {
        ButterKnife.bind(this, view);
    }

    private void setUpRecyclerViews() {
        listSearchUserView.setLayoutManager(new LinearLayoutManager(getContext()));
        listSelectedUserView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        listSearchUserView.setEmptyView(noResultView);
    }

    private void setUpPresenters() {
        searchPresenter = new SearchPresenter(this);
        selectPresenter = new SelectPresenter(this);
        createGroupPresenter = new CreateGroupPresenter(getContext(), this);
    }

    @OnClick(R.id.pick_user_back)
    public void onBackClick() {
        getActivity().finish();
    }

    @OnClick(R.id.pick_user_search)
    public void onSearchClick() {
        searchView.setEnabled(false);
        progressBarView.setVisibility(View.VISIBLE);
        String query = searchUserView.getText().toString().trim();
        searchPresenter.startSearch(query, selectedUserList);
    }

    @OnClick(R.id.pick_user_add)
    public void onAddClick() {
        createGroupPresenter.create(selectedUserList);
    }

    @Override
    public void createSuccess(String id) {
        Intent chatRoomIntent = new Intent(getContext(), GroupChatActivity.class);
        chatRoomIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        chatRoomIntent.putExtra(GroupChatActivity.EXTRA_GROUP_ID, id);
        startActivity(chatRoomIntent);
        getActivity().finish();
    }

    @Override
    public void createFailed() {

    }

    @Override
    public void setSearchAdapter(SearchAdapter searchAdapter) {
        listSearchUserView.setAdapter(searchAdapter);
    }

    @Override
    public void setSearchUserItemClick(Users users) {
        selectedUserList.add(users);
        selectPresenter.addSelectedUser(users);
        searchPresenter.removeFromSearch(users);

        listSelectedUserView.scrollToPosition(selectPresenter.getItemCount() - 1);
        userSelectedLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void searchSuccess() {
        searchView.setEnabled(true);
        progressBarView.setVisibility(View.GONE);
    }

    @Override
    public void searchFailed() {
        searchView.setEnabled(true);
        progressBarView.setVisibility(View.GONE);
        Timber.i("Failed");
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setSelectUserAdapter(SelectedAdapter selectUserAdapter) {
        listSelectedUserView.setAdapter(selectUserAdapter);
    }

    @Override
    public void setSelectUserItemClick(Users users) {
        selectedUserList.remove(users);
        selectPresenter.removeSelectedUser(users);
        searchPresenter.addFromSearch(users);

        if (selectedUserList.size() == 0) {
            userSelectedLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void selectUserFailed() {
        Timber.i("Failed");
    }
}
