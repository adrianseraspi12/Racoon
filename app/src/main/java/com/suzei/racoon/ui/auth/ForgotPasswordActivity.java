package com.suzei.racoon.ui.auth;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.suzei.racoon.R;
import com.suzei.racoon.util.view.DelayedProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DelayedProgressDialog dialog;

    @BindView(R.id.forgot_password_email) EditText emailView;
    @BindView(R.id.forgot_password_toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initObjects();
        setUpToolbar();
    }

    private void initObjects() {
        ButterKnife.bind(this);
        dialog = new DelayedProgressDialog();
        mAuth = FirebaseAuth.getInstance();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.forgot_password_confirm)
    public void onConfirmClick() {
        dialog.show(getSupportFragmentManager(), "");
        String email = emailView.getText().toString().trim();
        performRequestResetPassword(email);
    }

    private void performRequestResetPassword(String email) {
        new Handler().postDelayed(() -> {
            if (TextUtils.isEmpty(email)) {
                emailView.setError("Email is required");
                emailView.requestFocus();
                dialog.dismiss();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailView.setError("Invalid Email");
                emailView.requestFocus();
                dialog.dismiss();
                return;
            }

            sendResetPasswordEmail(email);
        }, 2000);
    }

    private void sendResetPasswordEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "We have sent you an email to reset your password, " +
                                        "Please follow the instruction",
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Failed to send email", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                });
    }
}
