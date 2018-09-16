package com.suzei.racoon.ui.add;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.auth.StartActivity;
import com.suzei.racoon.util.OnlineStatus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends AppCompatActivity {

    public static final String EXTRA_FRAGMENT_TYPE = "fragment_type";

    @BindView(R.id.creator_fragment_container) FrameLayout container;
    @BindView(R.id.creator_banner_ad) AdView bannerAd;

    public static class Add {
        public static final int WORLD = 0;
        public static final int SINGLE_CHAT = 1;
        public static final int GROUP_CHAT = 2;
        public static final int FRIENDS = 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator);
        ButterKnife.bind(this);
        showFragment();
        setUpBannerAd();
    }

    private void showFragment() {
        int fragmentType = getIntent().getIntExtra(EXTRA_FRAGMENT_TYPE, -1);
        Fragment fragment;
        switch (fragmentType) {

            case Add.WORLD:
                fragment = new AddWorldFragment();
                break;

            case Add.SINGLE_CHAT:
                fragment = new AddSingleChatFragment();
                break;

            case Add.GROUP_CHAT:
                fragment = new AddGroupChatFragment();
                break;

            case Add.FRIENDS:
                fragment = new AddFriendFragment();
                break;

            default:
                throw new IllegalArgumentException("Invalid fragment type=" + fragmentType);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(container.getId(), fragment);
        ft.commit();
    }

    private void setUpBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);
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
            Intent intent = new Intent(AddActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
