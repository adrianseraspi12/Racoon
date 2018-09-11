package com.suzei.racoon.ui.search;

import com.suzei.racoon.adapter.SelectedAdapter;
import com.suzei.racoon.model.Users;

import java.util.ArrayList;

public class SelectInteractor {

    private SelectUserContract.SelectUserListener selectUserListener;

    private SelectedAdapter selectedAdapter;

    private ArrayList<Users> selectedUserList = new ArrayList<>();

    SelectInteractor(SelectUserContract.SelectUserListener selectUserListener) {
        this.selectUserListener = selectUserListener;
    }

    public void initSelectAdapter() {
        selectedAdapter = new SelectedAdapter(selectedUserList,
                (data, view) -> selectUserListener.onSelectUserItemClick(data));
        selectUserListener.onInitSelectedAdapter(selectedAdapter);
    }

    public void addSelectedUser(Users users) {
        selectedUserList.add(users);
        selectedAdapter.notifyDataSetChanged();
    }

    public void removeSelectedUser(Users users) {
        selectedUserList.remove(users);
        selectedAdapter.notifyDataSetChanged();
    }

    public int getItemCount() {
        return selectedAdapter.getItemCount();
    }

}
