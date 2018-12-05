package com.suzei.racoon.ui.chatlist;


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

import static com.suzei.racoon.ui.add.AddActivity.EXTRA_FRAGMENT_TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements
        Callback.ButtonView,
        Contract.AdapterView<ChatAdapter> {

    private Unbinder unbinder;

    private ChatPresenter chatPresenter;

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
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        chatPresenter = new ChatPresenter(this);
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

    @Override
    public void onStart() {
        super.onStart();
        chatPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatPresenter.destroy();
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

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setAdapter(ChatAdapter adapter) {
        listChatView.setAdapter(adapter);
    }

    @Override
    public void loadFailed() {

    }
}
