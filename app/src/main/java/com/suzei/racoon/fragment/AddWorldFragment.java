package com.suzei.racoon.fragment;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.R;
import com.suzei.racoon.adapter.SearchAdapter;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.search.SearchContract;
import com.suzei.racoon.ui.search.SearchPresenter;
import com.suzei.racoon.util.EmptyRecyclerView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AddWorldFragment extends Fragment implements SearchContract.SearchView {

    private DatabaseReference mRootRef;

    private SearchPresenter searchPresenter;

    private String currentUserId;

    private ArrayList<Users> searchUserList = new ArrayList<>();

    @BindString(R.string.default_user_image) String defaultImage;
    @BindView(R.id.pick_user_no_result) TextView noResultView;
    @BindView(R.id.pick_user_loading) ProgressBar progressBarView;
    @BindView(R.id.pick_user_back) ImageButton backView;
    @BindView(R.id.pick_user_search_text) EditText searchUserView;
    @BindView(R.id.pick_user_search) ImageButton searchView;
    @BindView(R.id.pick_user_selected_list_layout) RelativeLayout userSelectedLayout;
    @BindView(R.id.pick_user_selected_list) RecyclerView listSelectedUserView;
    @BindView(R.id.pick_user_add) Button addView;
    @BindView(R.id.pick_user_search_list) EmptyRecyclerView listSearchUserView;

    public AddWorldFragment() {
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
        currentUserId = FirebaseAuth.getInstance().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setUpRecyclerViews() {
        listSearchUserView.setLayoutManager(new LinearLayoutManager(getContext()));
        listSelectedUserView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        listSearchUserView.setEmptyView(noResultView);
    }

    private void setUpPresenters() {
        searchPresenter = new SearchPresenter(this);
    }

    @OnClick(R.id.pick_user_back)
    public void onBackClick() {
        getActivity().finish();
    }

    @OnClick(R.id.pick_user_search)
    public void onSearchClick() {
        ArrayList<Users> selectedUserList = new ArrayList<>();
        String query = searchUserView.getText().toString();
        searchPresenter.startSearch(query, selectedUserList);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setSearchAdapter(SearchAdapter searchAdapter) {
        listSearchUserView.setAdapter(searchAdapter);
    }

    @Override
    public void setSearchUserItemClick() {
        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getSearchList(ArrayList<Users> searchList) {
        searchUserList.clear();
        searchUserList.addAll(searchList);
        Timber.i(String.valueOf(searchList));
    }

    @Override
    public void searchFailed() {
        Timber.i("Failed");
    }
}
