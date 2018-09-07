package com.suzei.racoon.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.R;

import com.suzei.racoon.activity.AddActivity;
import com.suzei.racoon.activity.AddActivity.Add;
import com.suzei.racoon.activity.MainActivity;
import com.suzei.racoon.adapter.WorldAdapter;
import com.suzei.racoon.callback.ButtonListener;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.util.GridAutofitLayoutManager;

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
public class WorldFragment extends Fragment implements ButtonListener {

    private DatabaseReference mWorldRef;
    private Unbinder unbinder;

    @BindView(R.id.world_chat_list) RecyclerView listWorldChat;

    public WorldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_world, container, false);
        initObjects(view);
        setListeners();
        setUpRecyclerView();
        setUpRecyclerAdapter();
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        mWorldRef = FirebaseDatabase.getInstance().getReference().child("world_chats");
    }

    private void setListeners() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setOnButtonClickListener(this);
        }
    }

    private void setUpRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        listWorldChat.setLayoutManager(manager);
        listWorldChat.setNestedScrollingEnabled(false);
    }

    private void setUpRecyclerAdapter() {
        mWorldRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    HashMap<String, Object> groupMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    ArrayList<String> worldGroupKeys = new ArrayList<>(groupMap.keySet());

                    WorldAdapter adapter = new WorldAdapter(worldGroupKeys);
                    listWorldChat.setAdapter(adapter);
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
        Timber.i("DestroyView called");
        unbinder.unbind();
    }

    @Override
    public void onButtonClick() {
        Intent addActIntent = new Intent(getContext(), AddActivity.class);
        addActIntent.putExtra(EXTRA_FRAGMENT_TYPE, Add.WORLD);
        startActivity(addActIntent);
    }
}
