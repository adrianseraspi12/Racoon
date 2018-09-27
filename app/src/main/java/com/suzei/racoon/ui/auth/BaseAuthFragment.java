package com.suzei.racoon.ui.auth;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suzei.racoon.R;
import com.suzei.racoon.ui.base.Callback;
import com.suzei.racoon.view.DelayedProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseAuthFragment extends Fragment implements Callback.ButtonView {

    public DelayedProgressDialog progressDialog;

    private Unbinder unbinder;

    @BindView(R.id.start_register_email) public TextInputEditText emailView;
    @BindView(R.id.start_register_password) public TextInputEditText passwordView;
    @BindView(R.id.start_register_display_name) public TextInputEditText nameView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressDialog = new DelayedProgressDialog();

        Activity activity = getActivity();
        if (activity instanceof StartActivity) {
            ((StartActivity) activity).setOnChatClick(this);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onButtonClick() {

    }
}
