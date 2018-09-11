package com.suzei.racoon.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.MembersActivity;
import com.suzei.racoon.ui.search.SearchAdapter;
import com.suzei.racoon.ui.search.SelectedAdapter;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.EmptyRecyclerView;
import com.suzei.racoon.util.FirebaseExceptionUtil;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMembersFragment extends Fragment {

    private Unbinder unbinder;

    private DatabaseReference mUserRef;
    private DatabaseReference mGroupRef;

    private SearchAdapter mSearchAdapter;
    private SelectedAdapter mSelectedAdapter;

    private String currentUserId;

    private String mId;
    private ArrayList<Users> searchUserList = new ArrayList<>();
    private ArrayList<Users> selectedUserList = new ArrayList<>();
    private ArrayList<String> oldMembersList = new ArrayList<>();

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
        setUpAdapter();
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
        currentUserId = FirebaseAuth.getInstance().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
        mGroupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(mId);
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
        mGroupRef.child("members").updateChildren(getMembersMap()).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Members Added", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

        });
    }

    private HashMap<String, Object> getMembersMap() {
        HashMap<String, Object> memberMap = new HashMap<>();
        for (int i = 0; i < selectedUserList.size(); i++) {
            Users users = selectedUserList.get(i);
            String uid = users.getUid();
            memberMap.put(uid, true);
        }

        return memberMap;
    }

    private void showSearchUsers(String query) {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchUserList.clear();

                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String uid = snap.getKey();

                    if (!uid.equals(currentUserId) && !hasSelectedKey(uid)
                            && !isOldMember(uid)) {
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

    private boolean isOldMember(String uid) {
        for (int i = 0; i < oldMembersList.size(); i++) {
            String oldMember = oldMembersList.get(i);
            if (uid.equals(oldMember)) {
                searchView.setEnabled(true);
                progressBarView.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
