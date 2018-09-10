package com.suzei.racoon.ui.friendlist;


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

import com.suzei.racoon.R;
import com.suzei.racoon.activity.AddActivity;
import com.suzei.racoon.activity.AddActivity.Add;
import com.suzei.racoon.ui.base.Callback;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.base.MainActivity;
import com.suzei.racoon.ui.base.UsersAdapter;
import com.suzei.racoon.ui.friendlist.data.FriendsListPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.suzei.racoon.activity.AddActivity.EXTRA_FRAGMENT_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements
        Callback.ButtonView,
        Contract.AdapterView<UsersAdapter>{

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
        setUpFriendsListPresenter();
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
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

    private void setUpFriendsListPresenter() {
        FriendsListPresenter presenter = new FriendsListPresenter(this);
        presenter.start();
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


    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setAdapter(UsersAdapter adapter) {
        listFriendsView.setAdapter(adapter);
    }

    @Override
    public void loadFailed() {

    }

}
