package com.suzei.racoon.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.AddActivity;
import com.suzei.racoon.activity.AddActivity.Add;
import com.suzei.racoon.activity.ChatRoomActivity;
import com.suzei.racoon.activity.MainActivity;
import com.suzei.racoon.adapter.UsersAdapter;
import com.suzei.racoon.callback.ButtonListener;
import com.suzei.racoon.callback.UsersListener;
import com.suzei.racoon.model.Users;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.suzei.racoon.activity.AddActivity.EXTRA_FRAGMENT_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements ButtonListener {

    private DatabaseReference mFriendsRef;
    private Unbinder unbinder;


    @BindView(R.id.recyclerview_list) RecyclerView listFriendsView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recyclerview_list, container, false);
        initObjects(view);
        setListeners();
        setUpRecyclerview();
        setUpAdapter();
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("user_friends")
                .child(currentUserId);
        mFriendsRef.keepSynced(true);
    }

    private void setListeners() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setOnButtonClickListener(this);
        }
    }

    private void setUpRecyclerview() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());

        listFriendsView.setLayoutManager(layoutManager);
        listFriendsView.addItemDecoration(itemDecoration);
    }

    private void setUpAdapter() {
        mFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    HashMap<String, Object> friendKeys =
                            (HashMap<String, Object>) dataSnapshot.getValue();

                    ArrayList<String> mFriendsList = new ArrayList<>(friendKeys.keySet());

                        UsersAdapter adapter = new UsersAdapter(mFriendsList, (users, itemView) -> {
                        Intent chatIntent = new Intent(getContext(), ChatRoomActivity.class);
                        chatIntent.putExtra(ChatRoomActivity.EXTRA_DETAILS, users);
                        chatIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_TYPE,
                                ChatRoomActivity.ChatType.SINGLE_CHAT);
                        startActivity(chatIntent);
                    });

                        listFriendsView.setAdapter(adapter);
                        Timber.i(String.valueOf(mFriendsList));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onButtonClick() {
        Intent addActIntent = new Intent(getContext(), AddActivity.class);
        addActIntent.putExtra(EXTRA_FRAGMENT_TYPE, Add.FRIENDS);
        startActivity(addActIntent);
    }
}
