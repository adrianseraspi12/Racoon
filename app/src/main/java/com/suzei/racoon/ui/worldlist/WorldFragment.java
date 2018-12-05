package com.suzei.racoon.ui.worldlist;


import android.app.Activity;
import android.content.Intent;
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
    private WorldPresenter worldPresenter;

    @BindView(R.id.recyclerview_list) RecyclerView listWorldChatView;

    public WorldFragment() {
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
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        worldPresenter = new WorldPresenter(this);
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

        listWorldChatView.setLayoutManager(layoutManager);
        listWorldChatView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        if (adapter != null) {
            listWorldChatView.setAdapter(adapter);
        } else {
            Timber.i("Adapter is null");
        }

    }

    @Override
    public void loadFailed() {
        Timber.i("Failed to load");
    }

}
