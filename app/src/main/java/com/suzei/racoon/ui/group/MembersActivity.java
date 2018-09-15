package com.suzei.racoon.ui.group;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.suzei.racoon.R;
import com.suzei.racoon.util.lib.OnlineStatus;

import java.util.ArrayList;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MembersActivity extends AppCompatActivity {

    public static final String EXTRA_MEMBERS_TYPE = "members_action";
    public static final String EXTRA_MEMBERS = "group_members";
    public static final String EXTRA_ID = "group_id";

    private ArrayList<String> membersList = new ArrayList<>();

    private String mId;
    private int type;

    public class MembersType {
        public static final int ADD_MEMBERS = 0;
        public static final int VIEW_MEMBERS = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        initGroupArgs();
        initObjects();
        showFragment();
    }

    private void initGroupArgs() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mId = bundle.getString(EXTRA_ID);
            type = bundle.getInt(EXTRA_MEMBERS_TYPE);
            membersList = bundle.getStringArrayList(EXTRA_MEMBERS);
        }

        Timber.i("Id= %s", mId);
        Timber.i("Type= %s", type);
        Timber.i("Members= %s", membersList);
    }

    private void initObjects() {
        ButterKnife.bind(this);
    }

    private void showFragment() {
        Bundle bundle = new Bundle();
        Fragment fragment;

        switch (type) {
            case MembersType.ADD_MEMBERS:
                bundle.putString(EXTRA_ID, mId);
                bundle.putStringArrayList(EXTRA_MEMBERS, membersList);
                fragment = new AddMembersFragment();
                fragment.setArguments(bundle);
                break;

            case MembersType.VIEW_MEMBERS:
                bundle.putString(EXTRA_ID, mId);
                fragment = new ViewMembersFragment();
                fragment.setArguments(bundle);
                break;

            default:
                throw new IllegalArgumentException("Invalid type= " + type);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.members_fragment_container, fragment);
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
}
