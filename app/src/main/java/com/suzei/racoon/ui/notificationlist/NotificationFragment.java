package com.suzei.racoon.ui.notificationlist;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suzei.racoon.R;
import com.suzei.racoon.ui.base.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements
        Contract.AdapterView<NotificationAdapter> {

    private Unbinder unbinder;
    private NotificationPresenter notificationPresenter;

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
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        notificationPresenter = new NotificationPresenter(this);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());

        listNotifsView.setLayoutManager(layoutManager);
        listNotifsView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onStart() {
        super.onStart();
        notificationPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        notificationPresenter.destroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setAdapter(NotificationAdapter adapter) {
        listNotifsView.setAdapter(adapter);
    }

    @Override
    public void loadFailed() {

    }
}
