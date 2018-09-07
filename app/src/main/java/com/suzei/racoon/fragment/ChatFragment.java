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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.AddActivity;
import com.suzei.racoon.activity.AddActivity.Add;
import com.suzei.racoon.activity.MainActivity;
import com.suzei.racoon.adapter.ChatAdapter;
import com.suzei.racoon.callback.ButtonListener;
import com.suzei.racoon.model.Chats;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.suzei.racoon.activity.AddActivity.EXTRA_FRAGMENT_TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements ButtonListener {

    private DatabaseReference mUserChatRef;
    private Unbinder unbinder;

    @BindView(R.id.recyclerview_list) RecyclerView listChatView;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recyclerview_list, container, false);
        initObjects(view);
        setListeners();
        setUpRecyclerView();
        setUpAdapter();
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        mUserChatRef = FirebaseDatabase.getInstance().getReference().child("user_chats")
                .child(currentUserId);
    }

    private void setListeners() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setOnButtonClickListener(this);
        }
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());

        listChatView.setLayoutManager(layoutManager);
        listChatView.addItemDecoration(itemDecoration);
    }

    private void setUpAdapter() {
        FirebaseRecyclerOptions<Chats> options = new FirebaseRecyclerOptions.Builder<Chats>()
                .setQuery(mUserChatRef, Chats.class).setLifecycleOwner(this).build();

        ChatAdapter adapter = new ChatAdapter(options);
        listChatView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onButtonClick() {
        Intent addActIntent = new Intent(getContext(), AddActivity.class);
        addActIntent.putExtra(EXTRA_FRAGMENT_TYPE, Add.SINGLE_CHAT);
        startActivity(addActIntent);
    }
}
