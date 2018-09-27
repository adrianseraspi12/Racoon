package com.suzei.racoon.ui.base;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.Racoon;
import com.suzei.racoon.ui.add.AddActivity;
import com.suzei.racoon.ui.auth.StartActivity;
import com.suzei.racoon.ui.chatlist.ChatFragment;
import com.suzei.racoon.ui.friendlist.FriendsFragment;
import com.suzei.racoon.ui.notificationlist.NotificationFragment;
import com.suzei.racoon.ui.preference.SettingsActivity;
import com.suzei.racoon.ui.profile.ProfileFragment;
import com.suzei.racoon.ui.worldlist.WorldFragment;
import com.suzei.racoon.ui.add.AddActivity.Add;
import com.suzei.racoon.util.OnlineStatus;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.suzei.racoon.ui.add.AddActivity.EXTRA_FRAGMENT_TYPE;

public class MainActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener {

    private DatabaseReference mNotifCountRef;
    private AHNotification.Builder notification;
    private InterstitialAd interstitialAd;
    private FirebaseAuth mAuth;

    private Callback.ButtonView mListener;

    @BindView(R.id.main_root) ConstraintLayout constraintLayout;
    @BindView(R.id.main_toolbar_layout) RelativeLayout toolbarLayout;
    @BindView(R.id.main_fragment_container) FrameLayout fragmentContainerView;
    @BindView(R.id.main_bottom_navigation) AHBottomNavigation bottomNavigationView;
    @BindView(R.id.main_primary_fab) FloatingActionButton fabPrimaryView;
    @BindView(R.id.main_secondary_fab) FloatingActionButton fabSecondaryView;
    @BindView(R.id.main_banner_ad) AdView bannerAd;
    @BindDrawable(R.drawable.world) Drawable drawableWorld;
    @BindDrawable(R.drawable.add_single_chat) Drawable drawableAddSingleChat;
    @BindDrawable(R.drawable.add_group_chat) Drawable drawableAddGroupChat;
    @BindDrawable(R.drawable.add_friend) Drawable drawableAddFriend;

    @Nullable
    @BindView(R.id.main_toolbar_shadow)
    View shadowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObjects();
        initBottomNavNotification();
        setUpAds();
        setUpSnackbarApp();
        setUpBottomNavigation();
    }

    private void initObjects() {
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mNotifCountRef = FirebaseDatabase.getInstance().getReference().child("notification_count");
    }

    private void initBottomNavNotification() {
         notification = new AHNotification.Builder()
                .setBackgroundColor(Color.RED)
                .setTextColor(Color.WHITE);
    }

    private void setUpAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void setUpSnackbarApp() {
        Racoon racoon = (Racoon) getApplication();
        racoon.setAppCallback(new Racoon.AppCallback() {

            @Override
            public void onConnected() {
                showSnackbar("Connected", Snackbar.LENGTH_SHORT);
            }

            @Override
            public void onDisconnected() {
                showSnackbar("Not connected", Snackbar.LENGTH_LONG);
            }

        });
    }

    private void setUpBottomNavigation() {
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this,
                R.menu.bottom_navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigationView);

        bottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigationView.setNotificationBackgroundColor(Color.parseColor("#a1887f"));
        bottomNavigationView.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigationView.setAccentColor(Color.parseColor("#306173"));
        bottomNavigationView.setOnTabSelectedListener(this);

        fabPrimaryView.setImageDrawable(drawableWorld);
        fabSecondaryView.hide();
        bottomNavigationView.setCurrentItem(0);
    }

    private ValueEventListener initEventListeners(int pos) {
        return new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("count")) {
                    int count = dataSnapshot.child("count").getValue(Integer.class);
                    addBadge(count, pos);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public void setOnButtonClickListener(Callback.ButtonView listener) {
        this.mListener = listener;
    }

    private void addBadge(int count, int pos) {
        if (count <= 0 ) {
            //remove notification badge
            bottomNavigationView.setNotification("", pos);
            return;
        }
        notification.setText(String.valueOf(count));
        bottomNavigationView.setNotification(notification.build(), pos);
    }

    @OnClick(R.id.main_settings)
    public void onSettingsClick() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    @OnClick(R.id.main_primary_fab)
    public void onPrimaryFabClick() {
        if (mListener != null) {
            mListener.onButtonClick();
        }
    }

    @OnClick(R.id.main_secondary_fab)
    public void onSecondaryFabClick() {
        Intent addActIntent = new Intent(MainActivity.this, AddActivity.class);
        addActIntent.putExtra(EXTRA_FRAGMENT_TYPE, Add.GROUP_CHAT);
        startActivity(addActIntent);
    }

    @OnClick(R.id.main_logout)
    public void onSignOutClick() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, StartActivity.class));
                    finish();
                }
            });
        } else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }

    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        switch (position) {

            case 0:
                showShadow();
                configFab(drawableWorld);
                showFragment(new WorldFragment());
                fabSecondaryView.hide();
                bannerAd.setVisibility(View.VISIBLE);
                break;

            case 1:
                showShadow();
                configFab(drawableAddSingleChat);
                showFragment(new ChatFragment());
                fabSecondaryView.show();
                fabSecondaryView.setImageDrawable(drawableAddGroupChat);
                bannerAd.setVisibility(View.VISIBLE);
                break;

            case 2:
                showShadow();
                configFab(drawableAddFriend);
                showFragment(new FriendsFragment());
                fabSecondaryView.hide();
                bannerAd.setVisibility(View.VISIBLE);
                break;

            case 3:
                showShadow();
                configFab(null);
                showFragment(new NotificationFragment());
                fabSecondaryView.hide();
                bannerAd.setVisibility(View.VISIBLE);
                break;

            case 4:
                hideShadow();
                configFab(null);
                showFragment(new ProfileFragment());
                fabSecondaryView.hide();
                bannerAd.setVisibility(View.GONE);
                break;

            default:
                throw new IllegalArgumentException("Invalid menu item =" + position);

        }

        return true;
    }

    private void showShadow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbarLayout.setElevation(4f);
        } else {
            shadowView.setVisibility(View.VISIBLE);
        }
    }

    private void hideShadow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbarLayout.setElevation(0f);
        } else {
            shadowView.setVisibility(View.GONE);
        }
    }

    private void configFab(Drawable drawable) {
        if (drawable == null) {
            fabPrimaryView.hide();
            return;
        }

        fabPrimaryView.setImageDrawable(drawable);
        fabPrimaryView.show();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(fragmentContainerView.getId(), fragment);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
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

        if (mAuth.getCurrentUser() != null) {
            mNotifCountRef.child(mAuth.getUid()).child("alerts")
                    .addValueEventListener(initEventListeners(3));
            mNotifCountRef.child(mAuth.getUid()).child("chats")
                    .addValueEventListener(initEventListeners(1));
        } else {
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuth.getCurrentUser() != null) {
            mNotifCountRef.child(mAuth.getUid()).child("alerts")
                    .removeEventListener(initEventListeners(3));
            mNotifCountRef.child(mAuth.getUid()).child("chats")
                    .removeEventListener(initEventListeners(1));
        }

    }

    private void showSnackbar(String text, int duration) {
        Snackbar snackbar = Snackbar.make(constraintLayout, text, duration);

        View view = snackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;

        // calculate actionbar height
        TypedValue tv = new TypedValue();
        int actionBarHeight= 0;

        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources()
                    .getDisplayMetrics());
        }

        params.setMargins(16, actionBarHeight + 16, 16, 0);
        view.setLayoutParams(params);
        snackbar.show();
    }

}
