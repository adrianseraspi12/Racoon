package com.suzei.racoon.ui.auth.forgotpassword;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.suzei.racoon.R;
import com.suzei.racoon.view.DelayedProgressDialog;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment implements ForgotPasswordContract.View {

    private DelayedProgressDialog dpd;
    private ForgotPasswordContract.Presenter presenter;

    @BindView(R.id.forgot_password_email) TextInputEditText emailView;

    static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ButterKnife.bind(this, view);
        dpd = new DelayedProgressDialog();
        return view;
    }

    @OnClick(R.id.forgot_password_reset)
    public void onResetClick() {
        String email = emailView.getText().toString();
        presenter.resetPassword(email);
    }

    @Override
    public void setPresenter(ForgotPasswordContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResetPasswordSuccess() {
        Toast.makeText(getContext(),
                "We have sent you an email to reset your password, " +
                        "Please follow the instruction",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResetPasswordFailure(String message) {
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
