package com.suzei.racoon.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.suzei.racoon.R;
import com.suzei.racoon.ui.auth.forgotpassword.ForgotPasswordActivity;
import com.suzei.racoon.ui.auth.signup.SignUpActivity;
import com.suzei.racoon.ui.base.MainActivity;
import com.suzei.racoon.view.DelayedProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements LoginContract.View {

    private LoginContract.Presenter presenter;

    private DelayedProgressDialog dpd;

    @BindView(R.id.login_email_input) TextInputEditText emailView;
    @BindView(R.id.login_password_input) TextInputEditText passwordView;

    static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        dpd = new DelayedProgressDialog();
        return view;
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClick() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        presenter.loginUser(email, password);
    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpButtonClick() {
        startActivity(new Intent(getContext(), SignUpActivity.class));
    }

    @OnClick(R.id.forgot_password_button)
    public void onForgotPasswordClick() {
        startActivity(new Intent(getContext(), ForgotPasswordActivity.class));
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLoginSuccess() {
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        dpd.show(getFragmentManager(), "Progress Dialog");
    }

    @Override
    public void hideProgress() {
        dpd.dismiss();
    }
}
