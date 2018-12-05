package com.suzei.racoon.ui.auth.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.base.MainActivity;
import com.suzei.racoon.view.DelayedProgressDialog;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements SignUpContract.View {

    private SignUpContract.Presenter presenter;

    private DelayedProgressDialog dpd;

    @BindView(R.id.signup_email) TextInputEditText emailView;
    @BindView(R.id.signup_password) TextInputEditText passwordView;
    @BindView(R.id.signup_name) TextInputEditText nameView;

    static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        dpd = new DelayedProgressDialog();
        return view;
    }

    @OnClick(R.id.signup_chat_now)
    public void onChatNowClick() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String displayName = nameView.getText().toString();

        presenter.createAccount(email, password, displayName);
    }

    @Override
    public void setPresenter(SignUpContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onRegisterSuccess() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRegisterFailure(String message) {
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