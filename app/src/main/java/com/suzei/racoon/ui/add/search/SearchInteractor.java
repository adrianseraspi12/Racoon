package com.suzei.racoon.ui.add.search;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.model.Users;

import java.util.ArrayList;

import timber.log.Timber;

public class SearchInteractor {

    private SearchContract.SearchListener searchListener;

    private SearchAdapter mSearchAdapter;
    private ArrayList<Users> searchUserList = new ArrayList<>();

    SearchInteractor(SearchContract.SearchListener searchListener) {
        this.searchListener = searchListener;
    }

    public void initSearchAdapter() {
        mSearchAdapter = new SearchAdapter(searchUserList, (users, itemView) ->
                searchListener.onSearchUserItemClick(users));
        searchListener.onInitSearchAdapter(mSearchAdapter);
    }

    public void performFirebaseDatabaseSearch(String query,
                                              ArrayList<Users> selectedUsers,
                                              ArrayList<String> oldList) {

        String currentUserId = FirebaseAuth.getInstance().getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchUserList.clear();

                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String uid = snap.getKey();
                    Timber.i("Key= %s", uid);

                    if (
                            !uid.equals(currentUserId) &&
                            !hasSelectedKey(uid, selectedUsers) &&
                            !isInOldList(uid, oldList)) {

                        Users users = snap.getValue(Users.class);
                        users.setUid(uid);

                        String name = users.getName();

                        if (isEqual(name, query)) {
                            searchUserList.add(users);
                        }

                    }
                }

                searchListener.onSearchSuccess();
                mSearchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void removeUser(Users users) {
        searchUserList.remove(users);
        mSearchAdapter.notifyDataSetChanged();
    }

    public void addUser(Users users) {
        searchUserList.add(users);
        mSearchAdapter.notifyDataSetChanged();
    }

    private boolean isEqual(String name, String query) {
        return !TextUtils.isEmpty(name) && name.toLowerCase().contains(query.toLowerCase());
    }

    private boolean hasSelectedKey(String uid, ArrayList<Users> selectedUserList) {
        for (int i = 0; i < selectedUserList.size(); i++) {
            Users users = selectedUserList.get(i);
            if (uid.equals(users.getUid())) {
                return true;
            }
        }
        return false;
    }

    private boolean isInOldList(String uid, ArrayList<String> oldList) {
        if (oldList == null) {
            return false;
        }

        for (int i = 0; i < oldList.size(); i++) {
            String oldUser = oldList.get(i);

            if (uid.equals(oldUser)) {
                return true;
            }

        }
        return false;
    }


}
