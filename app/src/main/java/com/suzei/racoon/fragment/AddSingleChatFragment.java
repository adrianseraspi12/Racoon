package com.suzei.racoon.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.ChatRoomActivity;
import com.suzei.racoon.activity.ChatRoomActivity.ChatType;
import com.suzei.racoon.adapter.SearchAdapter;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.EmptyRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AddSingleChatFragment extends Fragment {

    private DatabaseReference mUserRef;
    private SearchAdapter mSearchAdapter;

    private String currentUserId;
    private ArrayList<Users> searchUserList = new ArrayList<>();

    @BindView(R.id.pick_user_no_result) TextView noResultView;
    @BindView(R.id.pick_user_loading) ProgressBar progressBarView;
    @BindView(R.id.pick_user_back) ImageButton backView;
    @BindView(R.id.pick_user_search_text) EditText searchUserView;
    @BindView(R.id.pick_user_search) ImageButton searchView;
    @BindView(R.id.pick_user_add) Button addView;
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
        setUpAdapter();
        return view;
    }

    private void initObjects(View view) {
        ButterKnife.bind(this, view);
        currentUserId = FirebaseAuth.getInstance().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    private void setUpRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        listSearchUserView.setLayoutManager(layoutManager);
        listSearchUserView.setItemAnimator(new DefaultItemAnimator());
        listSearchUserView.setEmptyView(noResultView);
    }

    private void setUpAdapter() {
        mSearchAdapter = new SearchAdapter(searchUserList, (users, itemView) -> {
            Intent chatRoomIntent = new Intent(getContext(), ChatRoomActivity.class);
            chatRoomIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_TYPE, ChatType.SINGLE_CHAT);
            chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_DETAILS, users);
            startActivity(chatRoomIntent);
            getActivity().finish();
        });

        listSearchUserView.setAdapter(mSearchAdapter);
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
        if (TextUtils.isEmpty(query)) {
            progressBarView.setVisibility(View.GONE);
            searchView.setEnabled(true);
            return;
        }
        showUsers(query);
    }

    private void showUsers(String query) {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchUserList.clear();
                Timber.i("onSearchClick: query=%s", query);

                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String uid = snap.getKey();
                    Timber.i("onDataChange: uid=%s", uid);

                    if (!uid.equals(currentUserId)) {
                        Users users = snap.getValue(Users.class);
                        users.setUid(uid);
                        String name = users.getName();

                        if (!TextUtils.isEmpty(name) &&
                                name.toLowerCase().contains(query.toLowerCase())) {
                            searchUserList.add(users);
                        }
                    }
                }

                searchView.setEnabled(true);
                progressBarView.setVisibility(View.GONE);
                mSearchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}