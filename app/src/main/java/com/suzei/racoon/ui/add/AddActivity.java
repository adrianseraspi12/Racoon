package com.suzei.racoon.ui.add;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.suzei.racoon.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends AppCompatActivity {

    public static final String EXTRA_FRAGMENT_TYPE = "fragment_type";

    @BindView(R.id.creator_fragment_container) FrameLayout container;

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
}
