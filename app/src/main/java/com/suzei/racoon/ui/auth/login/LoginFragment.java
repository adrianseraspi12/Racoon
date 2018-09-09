package com.suzei.racoon.auth.login;

import android.app.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.suzei.racoon.R;
import com.suzei.racoon.ui.base.MainActivity;
import com.suzei.racoon.auth.StartActivity;
import com.suzei.racoon.callback.ButtonListener;
import com.suzei.racoon.auth.AuthContract;
import com.suzei.racoon.util.DelayedProgressDialog;
import com.suzei.racoon.util.FirebaseExceptionUtil;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginFragment extends Fragment implements ButtonListener, AuthContract.LoginView {

    private DelayedProgressDialog progressDialog;

    private LoginPresenter presenter;

    private Unbinder unbinder;

    private boolean isEyeEnabled = true;

    @BindView(R.id.start_login_email) EditText emailView;
    @BindView(R.id.start_login_password) EditText passwordView;
    @BindView(R.id.start_login_show_hide_pass) ImageButton showHidePassView;
    @BindDrawable(R.drawable.eye_show) Drawable drawableShowEye;
    @BindDrawable(R.drawable.eye_hide) Drawable drawableHideEye;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initObjects(view);
        setListeners();
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        progressDialog = new DelayedProgressDialog();
        presenter = new LoginPresenter(this);
    }

    private void setListeners() {
        Activity activity = getActivity();
        if (activity instanceof StartActivity) {
            ((StartActivity) activity).setOnChatClick(this);
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onButtonClick() {
        String username = emailView.getText().toString().trim();
        String password = emailView.getText().toString().trim();
        presenter.validateLoginCredentials(getActivity(), username, password);
    }

    @OnClick(R.id.start_login_show_hide_pass)
    public void onEyeClick(ImageButton imageButton) {
        int inputtedLength = passwordView.getText().length();

        if (isEyeEnabled) {
            passwordView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageButton.setImageDrawable(drawableHideEye);
            isEyeEnabled = false;
        } else {
            passwordView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageButton.setImageDrawable(drawableShowEye);
            isEyeEnabled = true;
        }

        passwordView.setSelection(inputtedLength);
    }

    @Override
    public void showProgress() {
        progressDialog.show(getActivity().getSupportFragmentManager(), "tag");
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void setUsernameError(String message) {
        emailView.setError(message);
        emailView.requestFocus();
    }

    @Override
    public void setPasswordError(String message) {
        passwordView.setError(message);
        passwordView.requestFocus();
    }

    @Override
    public void onLoginSuccess() {
        Intent mainActIntent = new Intent(getContext(), MainActivity.class);
        mainActIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActIntent);
        getActivity().finish();
    }

    @Override
    public void onLoginFailure(Exception e) {
        FirebaseExceptionUtil.databaseError(getContext(), e);
    }
}
