package com.suzei.racoon.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.suzei.racoon.ui.auth.AuthContract;
import com.suzei.racoon.ui.auth.BaseAuthFragment;
import com.suzei.racoon.ui.base.MainActivity;

public class LoginFragment extends BaseAuthFragment implements
        AuthContract.LoginView {

    private LoginPresenter presenter;

    public LoginFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nameView.setVisibility(View.GONE);
        presenter = new LoginPresenter(getActivity(),this);
    }

    @Override
    public void onButtonClick() {
        String username = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        presenter.validateLoginCredentials(username, password);
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
    public void onLoginFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
