package com.suzei.racoon.ui.notificationlist;

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
import com.google.firebase.database.Query;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Notifications;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private Unbinder unbinder;

    private DatabaseReference mNotifsRef;

    @BindView(R.id.recyclerview_list) RecyclerView listNotifsView;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_list, container, false);
        initObjects(view);
        setUpRecyclerView();
        setUpAdapter();
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        mNotifsRef = FirebaseDatabase.getInstance().getReference().child("notifications")
                .child(currentUserId);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());

        listNotifsView.setLayoutManager(layoutManager);
        listNotifsView.addItemDecoration(itemDecoration);
    }

    private void setUpAdapter() {
        Query query = mNotifsRef.orderByChild("timestamp");
        FirebaseRecyclerOptions<Notifications> options = new FirebaseRecyclerOptions
                .Builder<Notifications>().setQuery(query, Notifications.class)
                .setLifecycleOwner(this).build();

        NotificationAdapter adapter = new NotificationAdapter(options);
        listNotifsView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
