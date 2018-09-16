package com.suzei.racoon.ui.group;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.add.search.SearchAdapter;
import com.suzei.racoon.ui.add.search.SearchContract;
import com.suzei.racoon.ui.add.search.SearchPresenter;
import com.suzei.racoon.ui.add.search.SelectPresenter;
import com.suzei.racoon.ui.add.search.SelectUserContract;
import com.suzei.racoon.ui.add.search.SelectedAdapter;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMembersFragment extends Fragment implements
        SearchContract.SearchView,
        SelectUserContract.SelectUserView {

    private Unbinder unbinder;

    private DatabaseReference mGroupRef;
    private SearchPresenter searchPresenter;
    private SelectPresenter selectPresenter;

    private String mId;
    private ArrayList<Users> selectedUserList = new ArrayList<>();
    private ArrayList<String> oldMembersList = new ArrayList<>();

    @BindView(R.id.pick_user_no_result) TextView noResultView;
    @BindView(R.id.pick_user_loading) ProgressBar progressBarView;
    @BindView(R.id.pick_user_search_text) EditText searchUserView;
    @BindView(R.id.pick_user_search) ImageButton searchView;
    @BindView(R.id.pick_user_selected_list_layout) RelativeLayout userSelectedLayout;
    @BindView(R.id.pick_user_selected_list) RecyclerView listSelectedUserView;
    @BindView(R.id.pick_user_search_list) EmptyRecyclerView listSearchUserView;

    public AddMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pick_user, container, false);
        initGroupArgs();
        initObjects(view);
        setUpRecyclerViews();
        return view;
    }

    private void initGroupArgs() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            mId = bundle.getString(MembersActivity.EXTRA_ID);
            oldMembersList = bundle.getStringArrayList(MembersActivity.EXTRA_MEMBERS);
        }

        Timber.i("Id = %s", mId);
        Timber.i(String.valueOf(oldMembersList));
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        searchPresenter = new SearchPresenter(this);
        selectPresenter = new SelectPresenter(this);
        mGroupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(mId);
    }

    private void setUpRecyclerViews() {
        listSearchUserView.setLayoutManager(new LinearLayoutManager(getContext()));
        listSelectedUserView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        listSearchUserView.setEmptyView(noResultView);
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
        searchPresenter.startSearchExcludeOldList(query, selectedUserList, oldMembersList);
    }

    @OnClick(R.id.pick_user_add)
    public void onAddClick() {
        HashMap<String, Object> memberMap = new HashMap<>();
        for (int i = 0; i < selectedUserList.size(); i++) {
            Users users = selectedUserList.get(i);
            String uid = users.getUid();
            memberMap.put(uid, true);
        }

        mGroupRef.child("members").updateChildren(memberMap).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Members Added", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

    }
}
