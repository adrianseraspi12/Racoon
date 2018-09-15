package com.suzei.racoon.ui.auth;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.auth.login.LoginFragment;
import com.suzei.racoon.ui.auth.register.RegisterFragment;
import com.suzei.racoon.ui.base.Callback;
import com.suzei.racoon.ui.base.MainActivity;

import butterknife.BindAnim;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class StartActivity extends AppCompatActivity {

    private Callback.ButtonView mCallback;

    private FirebaseUser currentUser;
    private Unbinder unbinder;

    private boolean isRegisterShow = true;

    @BindView(R.id.start_fragment_container) FrameLayout fragmentContainerView;
    @BindView(R.id.start_show_fragment) TextView changeFragmentView;
    @BindView(R.id.start_chat_now) Button chatNowView;
    @BindString(R.string.create_an_account) String createAccount;
    @BindString(R.string.already_have_an_account) String alreadyHaveAccount;
    @BindAnim(R.anim.fade_in) Animation fadeInAnim;
    @BindAnim(R.anim.fade_out) Animation fadeOutAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initObjects();
        showFragment(new RegisterFragment());
    }

    private void initObjects() {
        unbinder = ButterKnife.bind(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(fragmentContainerView.getId(), fragment);
        ft.commit();
    }

    @OnClick(R.id.start_forgot_password)
    public void onForgotPasswordClick() {
        startActivity(new Intent(StartActivity.this, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.start_chat_now)
    public void onChatNowClick() {

        if (mCallback == null) {
            return;
        }

        mCallback.onButtonClick();
    }

    @OnClick(R.id.start_show_fragment)
    public void onTextClick(TextView textView) {
        textView.setEnabled(false);
        if (isRegisterShow) {
            showFragment(new LoginFragment());
            changeText(createAccount);
            isRegisterShow = false;
            textView.setEnabled(true);
        } else {
            showFragment(new RegisterFragment());
            changeText(alreadyHaveAccount);
            isRegisterShow = true;
            textView.setEnabled(true);
        }
    }

    private void changeText(final String text) {
        fadeOutAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                changeFragmentView.setText(text);
                changeFragmentView.setAnimation(null);
                changeFragmentView.setAnimation(fadeInAnim);
                fadeInAnim.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        changeFragmentView.setAnimation(fadeOutAnim);
        fadeOutAnim.start();
    }

    public void setOnChatClick(Callback.ButtonView listener) {
        this.mCallback = listener;
    }

    public void removeOnBackPressedListener() {
        this.mCallback = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
