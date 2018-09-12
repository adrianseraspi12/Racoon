package com.suzei.racoon.ui.worldlist;


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

import com.suzei.racoon.R;

import com.suzei.racoon.ui.add.AddActivity;
import com.suzei.racoon.ui.add.AddActivity.Add;
import com.suzei.racoon.ui.base.Callback;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.base.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.suzei.racoon.ui.add.AddActivity.EXTRA_FRAGMENT_TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorldFragment extends Fragment implements
        Callback.ButtonView,
        Contract.AdapterView<WorldAdapter>{

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
        setUpWorldPresenter();
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

    private void setUpRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        listWorldChat.setLayoutManager(manager);
        listWorldChat.setNestedScrollingEnabled(false);
    }

    private void setUpWorldPresenter() {
        WorldPresenter worldPresenter = new WorldPresenter(this);
        worldPresenter.start();
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

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setAdapter(WorldAdapter adapter) {
        listWorldChat.setAdapter(adapter);
    }

    @Override
    public void loadFailed() {
        Timber.i("Failed to load");
    }

}
