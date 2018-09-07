package com.suzei.racoon.auth.ui;

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

import com.suzei.racoon.R;
import com.suzei.racoon.auth.ui.LoginFragment;
import com.suzei.racoon.auth.ui.RegisterFragment;
import com.suzei.racoon.callback.ButtonListener;

import butterknife.BindAnim;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class StartActivity extends AppCompatActivity {

    //TODO: Add forgot password

    private ButtonListener mCallback;

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
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(fragmentContainerView.getId(), fragment);
        ft.commit();
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

    @OnClick(R.id.start_chat_now)
    public void onChatNowClick(View view) {

        if (mCallback == null) {
            return;
        }

        mCallback.onButtonClick();
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

    public void setOnChatClick(ButtonListener listener) {
        this.mCallback = listener;
    }

    public void removeOnBackPressedListener() {
        this.mCallback = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
