package com.suzei.racoon.ui.auth.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.suzei.racoon.ui.auth.AuthContract;
import com.suzei.racoon.ui.auth.BaseAuthFragment;
import com.suzei.racoon.ui.base.Callback;
import com.suzei.racoon.ui.base.MainActivity;

public class RegisterFragment extends BaseAuthFragment implements
        AuthContract.RegisterView {

    private RegisterPresenter presenter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new RegisterPresenter(getContext(), this);
    }

    @Override
    public void onButtonClick() {
        super.onButtonClick();
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
    public void onLoginFailure(String e) {
        Toast.makeText(getContext(), e, Toast.LENGTH_SHORT).show();
    }
}
