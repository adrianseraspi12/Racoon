package com.suzei.racoon.ui.auth.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.suzei.racoon.R;
import com.suzei.racoon.ui.auth.AuthContract;
import com.suzei.racoon.ui.auth.StartActivity;
import com.suzei.racoon.ui.base.Callback;
import com.suzei.racoon.ui.base.MainActivity;
import com.suzei.racoon.util.view.DelayedProgressDialog;
import com.suzei.racoon.util.ErrorHandler;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RegisterFragment extends Fragment implements
        Callback.ButtonView,
        AuthContract.RegisterView{

    private DelayedProgressDialog progressDialog;

    private RegisterPresenter presenter;

    private Unbinder unbinder;

    private boolean isEyeEnable = true;

    @BindView(R.id.start_register_email) EditText emailView;
    @BindView(R.id.start_register_password) EditText passwordView;
    @BindView(R.id.start_register_show_hide_pass) ImageButton showHidePassView;
    @BindView(R.id.start_register_display_name) EditText nameView;
    @BindDrawable(R.drawable.eye_show) Drawable drawableShowPass;
    @BindDrawable(R.drawable.eye_hide) Drawable drawablehidePass;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initObjects(view);
        setListeners();
        return view;
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        progressDialog = new DelayedProgressDialog();
        presenter = new RegisterPresenter(getContext(), this);
    }

    private void setListeners() {
        Activity activity = getActivity();
        if (activity instanceof StartActivity) {
            ((StartActivity) activity).setOnChatClick(this);
        }
    }

    @OnClick(R.id.start_register_show_hide_pass)
    public void onEyeClick(ImageButton imageButton) {
        int inputtedLength = passwordView.getText().length();

        if (isEyeEnable) {
            passwordView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageButton.setImageDrawable(drawablehidePass);
            isEyeEnable = false;
        } else {
            passwordView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageButton.setImageDrawable(drawableShowPass);
            isEyeEnable = true;
        }

        passwordView.setSelection(inputtedLength);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onButtonClick() {
        String email = emailView.getText().toString().trim();
        String pass = passwordView.getText().toString().trim();
        String name = nameView.getText().toString().trim();

        presenter.validateRegisterCredentials(name, email, pass);
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
    public void setDisplayNameError(String message) {
        nameView.setError(message);
        nameView.requestFocus();
    }

    @Override
    public void onLoginSuccess() {
        Intent mainActIntent = new Intent(getContext(), MainActivity.class);
        mainActIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActIntent);
        getActivity().finish();
    }

    @Override
    public void onLoginFailure(Exception e) {
        ErrorHandler.authError(getContext(), e);
    }
}
