package com.suzei.racoon.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.suzei.racoon.adapter.SelectedAdapter;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.EmptyRecyclerView;
import com.suzei.racoon.util.FirebaseExceptionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AddWorldFragment extends Fragment {

    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mGroupRef;

    private SearchAdapter mSearchAdapter;
    private SelectedAdapter mSelectedAdapter;

    private String currentUserId;

    private ArrayList<Users> searchUserList = new ArrayList<>();
    private ArrayList<Users> selectedUserList = new ArrayList<>();

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
        setUpAdapter();
        return view;
    }

    private void initObjects(View view) {
        ButterKnife.bind(this, view);
        currentUserId = FirebaseAuth.getInstance().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("users");
        mGroupRef = mRootRef.child("groups");
    }

    private void setUpRecyclerViews() {
        listSearchUserView.setLayoutManager(new LinearLayoutManager(getContext()));
        listSelectedUserView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        listSearchUserView.setEmptyView(noResultView);
    }

    private void setUpAdapter() {
        mSearchAdapter = new SearchAdapter(searchUserList, (users, itemView) -> {
            selectedUserList.add(users);
            mSelectedAdapter.notifyDataSetChanged();
            mSearchAdapter.notifyDataSetChanged();
            listSelectedUserView.scrollToPosition(mSelectedAdapter.getItemCount() -1);
            userSelectedLayout.setVisibility(View.VISIBLE);
        });

        mSelectedAdapter = new SelectedAdapter(selectedUserList, (users, itemView) -> {
            selectedUserList.remove(users);
            mSelectedAdapter.notifyDataSetChanged();
            mSearchAdapter.notifyDataSetChanged();

            if (selectedUserList.size() == 0) {
                userSelectedLayout.setVisibility(View.GONE);
            }
        });

        listSearchUserView.setAdapter(mSearchAdapter);
        listSelectedUserView.setAdapter(mSelectedAdapter);
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
        showSearchUsers(query);
    }

    @OnClick(R.id.pick_user_add)
    public void onAddClick() {
        String groupId = mGroupRef.push().getKey();

        HashMap<String, Object> groupMap = new HashMap<>();
        groupMap.put("name", getDefaultName());
        groupMap.put("admin/" + currentUserId, true);
        groupMap.put("description", "Newly created group");
        groupMap.put("image", defaultImage);
        groupMap.put("members", getMembers());

        mGroupRef.child(groupId).updateChildren(groupMap).addOnSuccessListener(aVoid -> {
            mRootRef.child("world_chats").child(groupId).setValue(true);

            Intent chatRoomIntent = new Intent(getContext(), ChatRoomActivity.class);
            chatRoomIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_DETAILS, getGroupDetails(groupId));
            chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_TYPE, ChatType.GROUP_CHAT);
            startActivity(chatRoomIntent);
            getActivity().finish();

        });
    }

    private String getDefaultName() {
        Random random = new Random(System.currentTimeMillis());
        int randonNumber = 10000 + random.nextInt(20000);
        return "Group" + randonNumber;
    }

    private HashMap<String, Object> getMembers() {
        HashMap<String, Object> membersMap = new HashMap<>();
        for (int i = 0; i < selectedUserList.size(); i++) {
            Users users = selectedUserList.get(i);
            membersMap.put(users.getUid(), true);
        }
        membersMap.put(currentUserId, true);
        return membersMap;
    }

    private Groups getGroupDetails(String uid) {
        HashMap<String, Object> admins = new HashMap<>();
        admins.put(currentUserId, true);

        Groups groups = new Groups();
        groups.setUid(uid);
        groups.setName(getDefaultName());
        groups.setImage(defaultImage);
        groups.setDescription("Newly created group");
        groups.setAdmin(admins);
        groups.setMembers(getMembers());
        return groups;
    }

    private void showSearchUsers(String query) {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchUserList.clear();

                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String uid = snap.getKey();
                    Timber.i("UID = %s", uid);

                    if (!uid.equals(currentUserId) && !hasSelectedKey(uid)) {
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

                Timber.i("Search Item count = %s", mSearchAdapter.getItemCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Exception exception = databaseError.toException();
                FirebaseExceptionUtil.databaseError(getContext(), exception);
            }
        });
    }

    private boolean hasSelectedKey(String uid) {
        for (int i = 0; i < selectedUserList.size(); i++) {
            Users users = selectedUserList.get(i);
            if (uid.equals(users.getUid())) {
                searchView.setEnabled(true);
                progressBarView.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }
}
