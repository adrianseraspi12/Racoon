package com.suzei.racoon.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.ChatRoomActivity;
import com.suzei.racoon.activity.MembersActivity;
import com.suzei.racoon.friend.ui.FriendActivity;
import com.suzei.racoon.adapter.UsersAdapter;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.FirebaseExceptionUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewMembersFragment extends Fragment {

    private Unbinder unbinder;

    private UsersAdapter adminAdapter;
    private UsersAdapter membersAdapter;


    private DatabaseReference mGroupsRef;
    private ValueEventListener eventListener;

    private String currentUserId;
    private String mId;

    private ArrayList<String> adminsList = new ArrayList<>();
    private ArrayList<String> membersList = new ArrayList<>();

    @BindView(R.id.view_member_back) ImageButton backView;
    @BindView(R.id.view_member_admin_list) RecyclerView listAdminView;
    @BindView(R.id.view_members_list) RecyclerView listMembersView;

    public ViewMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_members, container, false);
        initMembersArgs();
        initObjects(view);
        setUpRecyclerViews();
        setListeners();
        setUpAdapter();
        return view;
    }

    private void initMembersArgs() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            mId = bundle.getString(MembersActivity.EXTRA_ID);
        }

        Timber.i("ID = %s", mId);
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        currentUserId = FirebaseAuth.getInstance().getUid();
        mGroupsRef = FirebaseDatabase.getInstance().getReference().child("groups")
                .child(mId);
    }

    private void setUpRecyclerViews() {
        listAdminView.setNestedScrollingEnabled(false);
        listMembersView.setNestedScrollingEnabled(false);
        listAdminView.setLayoutManager(new LinearLayoutManager(getContext()));
        listMembersView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setListeners() {
        eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Groups groups = dataSnapshot.getValue(Groups.class);

                adminsList.clear();
                membersList.clear();

                adminsList.addAll(groups.getAdmin().keySet());
                membersList.addAll(groups.getMembers().keySet());

                adminAdapter.notifyDataSetChanged();
                membersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FirebaseExceptionUtil.databaseError(getContext(), databaseError.toException());
            }

        };
    }

    private void setUpAdapter() {
        adminAdapter = new UsersAdapter(adminsList, (users, itemView) -> {
            String uid = users.getUid();

            if (uid.equals(currentUserId)) {
                //dont show popupmenu if uid==currentUserId
                return;
            }

            PopupMenu popup = getPopupMenu(users, itemView);
            popup.getMenu().getItem(3).setVisible(false);

            if (!adminsList.contains(currentUserId)) {
                // hide 'remove a admin' and 'remove from group'
                popup.getMenu().getItem(0).setVisible(false);
                popup.getMenu().getItem(1).setVisible(false);
            }

            popup.show();
        });

        membersAdapter = new UsersAdapter(membersList, (users, itemView) -> {
            String uid = users.getUid();

            if (uid.equals(currentUserId)) {
                //dont show popupmenu if uid==currentUserId
                return;
            }

            PopupMenu popup = getPopupMenu(users, itemView);
            popup.getMenu().getItem(0).setVisible(false);

            if (!adminsList.contains(currentUserId)) {
                // hide 'remove a admin' and 'remove from group'
                popup.getMenu().getItem(1).setVisible(false);
                popup.getMenu().getItem(3).setVisible(false);
            }

            popup.show();
        });

        listAdminView.setAdapter(adminAdapter);
        listMembersView.setAdapter(membersAdapter);
    }

    private PopupMenu getPopupMenu(Users users, View itemView) {
        PopupMenu popupMenu = new PopupMenu(getContext(), itemView, Gravity.BOTTOM|Gravity.END);
        popupMenu.inflate(R.menu.popup_members);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch (id) {

                case R.id.popup_send_message:
                    Intent chatIntent = new Intent(getContext(), ChatRoomActivity.class);
                    chatIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_TYPE,
                            ChatRoomActivity.ChatType.SINGLE_CHAT);
                    chatIntent.putExtra(ChatRoomActivity.EXTRA_DETAILS, users);
                    startActivity(chatIntent);
                    return true;

                case R.id.popup_visit_profile:
                    Intent profileIntent = new Intent(getContext(), FriendActivity.class);
                    profileIntent.putExtra(FriendActivity.EXTRA_PROFILE_DETAILS, users);
                    startActivity(profileIntent);
                    return true;

                case R.id.popup_remove_admin:
                    mGroupsRef.child("admin").child(users.getUid()).removeValue();
                    Toast.makeText(getContext(), users.getName() + " has been removed as admin",
                            Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.popup_set_as_admin:
                    mGroupsRef.child("admin").child(users.getUid()).setValue(true);
                    Toast.makeText(getContext(), users.getName() + "becomes an Admin",
                            Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.popup_remove_group:
                    mGroupsRef.child("members").child(users.getUid()).removeValue();
                    Toast.makeText(getContext(), users.getName() + " has been removed",
                            Toast.LENGTH_SHORT).show();
                    return true;

                default:
                    throw new IllegalArgumentException("Invalid Id= " + id);
            }
        });
        return popupMenu;
    }

    @OnClick(R.id.view_member_back)
    public void backClick() {
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGroupsRef.addValueEventListener(eventListener);
    }

    @Override
    public void onStop() {
        mGroupsRef.removeEventListener(eventListener);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
