package com.suzei.racoon.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suzei.racoon.R;
import com.suzei.racoon.callback.ButtonListener;
import com.suzei.racoon.fragment.ChatFragment;
import com.suzei.racoon.fragment.FriendsFragment;
import com.suzei.racoon.fragment.NotificationFragment;
import com.suzei.racoon.profile.ui.ProfileFragment;
import com.suzei.racoon.fragment.WorldFragment;
import com.suzei.racoon.activity.AddActivity.Add;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.suzei.racoon.activity.AddActivity.EXTRA_FRAGMENT_TYPE;

public class MainActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener {

    //TODO Add onBoarding user
    //TODO (WARNING!) could cause of slowing of app because of ValueEventListener

    static { AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); }

    private DatabaseReference mNotifCountRef;

    private AHNotification.Builder notification;

    private ButtonListener mListener;

    @BindView(R.id.main_toolbar_layout) RelativeLayout toolbarLayout;
    @BindView(R.id.main_toolbar_shadow) View shadowView;
    @BindView(R.id.main_fragment_container) FrameLayout fragmentContainerView;
    @BindView(R.id.main_bottom_navigation) AHBottomNavigation bottomNavigationView;
    @BindView(R.id.main_primary_fab) FloatingActionButton fabPrimaryView;
    @BindView(R.id.main_secondary_fab) FloatingActionButton fabSecondaryView;
    @BindDrawable(R.drawable.world) Drawable drawableWorld;
    @BindDrawable(R.drawable.add_single_chat) Drawable drawableAddSingleChat;
    @BindDrawable(R.drawable.add_group_chat) Drawable drawableAddGroupChat;
    @BindDrawable(R.drawable.add_friend) Drawable drawableAddFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObjects();
        initBottomNavNotification();
        setUpBottomNavigation();
        fabSecondaryView.hide();
        bottomNavigationView.setCurrentItem(0);
    }

    private void initObjects() {
        ButterKnife.bind(this);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        mNotifCountRef = FirebaseDatabase.getInstance().getReference().child("notification_count")
                .child(currentUserId);
    }

    private void initBottomNavNotification() {
         notification = new AHNotification.Builder()
                .setBackgroundColor(Color.RED)
                .setTextColor(Color.WHITE);
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

    public void setOnButtonClickListener(ButtonListener listener) {
        this.mListener = listener;
    }

    public void removeOnButtonClickListener() {
        this.mListener = null;
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

    @OnClick(R.id.main_primary_fab)
    public void onPrimaryFabClick(View view) {
        if (mListener != null) {
            mListener.onButtonClick();
        }
        //Timber listener is null
    }

    @OnClick(R.id.main_secondary_fab)
    public void onSecondaryFabClick(View view) {
        Intent addActIntent = new Intent(MainActivity.this, AddActivity.class);
        addActIntent.putExtra(EXTRA_FRAGMENT_TYPE, Add.GROUP_CHAT);
        startActivity(addActIntent);
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        switch (position) {

            case 0:
                showShadow();
                configFab(drawableWorld);
                showFragment(new WorldFragment());
                fabSecondaryView.hide();
                break;

            case 1:
                showShadow();
                configFab(drawableAddSingleChat);
                showFragment(new ChatFragment());
                fabSecondaryView.show();
                fabSecondaryView.setImageDrawable(drawableAddGroupChat);


                break;

            case 2:
                showShadow();
                configFab(drawableAddFriend);
                showFragment(new FriendsFragment());
                fabSecondaryView.hide();


                break;

            case 3:
                showShadow();
                configFab(null);
                showFragment(new NotificationFragment());
                fabSecondaryView.hide();
                break;

            case 4:
                hideShadow();
                configFab(null);
                showFragment(new ProfileFragment());
                fabSecondaryView.hide();
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
    protected void onStart() {
        super.onStart();
        mNotifCountRef.child("alerts").addValueEventListener(initEventListeners(3));
        mNotifCountRef.child("chats").addValueEventListener(initEventListeners(1));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNotifCountRef.child("alerts").removeEventListener(initEventListeners(3));
        mNotifCountRef.child("chats").removeEventListener(initEventListeners(1));
    }
}
