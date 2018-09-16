package com.suzei.racoon.ui.preference;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.auth.StartActivity;
import com.suzei.racoon.util.OnlineStatus;
import com.suzei.racoon.view.DelayedProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountActivity extends AppCompatActivity implements AccountContract.AccountView {

    public static final String CHANGE = "change";
    public static final int CHANGE_EMAIL = 0;
    public static final int RESET_PASSWORD = 1;
    public static final int DELETE_ACCOUNT = 2;

    private AccountPresenter accountPresenter;
    private DelayedProgressDialog delayedProgressDialog;

    private int change;

    @BindView(R.id.account_toolbar) Toolbar toolbar;
    @BindView(R.id.account_password_layout) TextInputLayout passwordLayout;
    @BindView(R.id.account_text_input_reset_layout) TextInputLayout resetLayout;
    @BindView(R.id.account_email) EditText emailView;
    @BindView(R.id.account_password) EditText passwordView;
    @BindView(R.id.account_text_input_reset) EditText resetView;
    @BindView(R.id.account_button) Button buttonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initObjects();
        setUpToolbar();
        setUpUi();
    }

    private void initObjects() {
        ButterKnife.bind(this);
        accountPresenter = new AccountPresenter(AccountActivity.this, this);
        delayedProgressDialog = new DelayedProgressDialog();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpUi() {
        change = getIntent().getIntExtra(CHANGE, -1);
        switch (change) {

            case CHANGE_EMAIL:
                getSupportActionBar().setTitle("Change Email");
                resetLayout.setHint("New Email");
                passwordLayout.setHint("Password");
                buttonView.setText(R.string.change);
                break;

            case RESET_PASSWORD:
                getSupportActionBar().setTitle("Reset Password");
                passwordLayout.setHint("Old Password");
                resetLayout.setHint("New Password");
                buttonView.setText(R.string.reset);
                break;

            case DELETE_ACCOUNT:
                getSupportActionBar().setTitle("Delete Account");
                passwordLayout.setHint("Password");
                resetLayout.setVisibility(View.GONE);
                buttonView.setText("Delete");
                break;

            default:
                throw new IllegalArgumentException("Invalid type=" + change);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        OnlineStatus.set(false);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        OnlineStatus.set(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(AccountActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @OnClick(R.id.account_button)
    public void onButtonClick() {
        buttonView.setEnabled(false);
        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        String strValue = resetView.getText().toString().trim();

        switch (change) {

            case CHANGE_EMAIL:
                accountPresenter.changeEmail(email, password, strValue);
                break;

            case RESET_PASSWORD:
                accountPresenter.resetPassword(email, password, strValue);
                break;

            case DELETE_ACCOUNT:
                accountPresenter.deleteAccount(email, password);
                break;
        }
    }

    @Override
    public void setUsernameError(String message) {
        buttonView.setEnabled(true);
        emailView.setError(message);
        emailView.requestFocus();
    }

    @Override
    public void setPasswordError(String message) {
        buttonView.setEnabled(true);
        passwordView.setError(message);
        passwordView.requestFocus();
    }

    @Override
    public void onLoginSuccess() {
        if (change == DELETE_ACCOUNT) {
            showDialog();
            return;
        }

        buttonView.setEnabled(true);
        Toast.makeText(AccountActivity.this, "Updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onLoginFailure(String e) {
        Toast.makeText(AccountActivity.this, e, Toast.LENGTH_SHORT).show();
        buttonView.setEnabled(true);
    }

    @Override
    public void setAccountChangeError(String message) {
        buttonView.setEnabled(true);
        resetView.setError(message);
        resetView.requestFocus();
    }

    @Override
    public void showProgress() {
        delayedProgressDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void hideProgress() {
        delayedProgressDialog.cancel();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("Account Deleted");
        builder.setMessage("Your account has been deleted. Thank You for using Racoon Chat");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(AccountActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
