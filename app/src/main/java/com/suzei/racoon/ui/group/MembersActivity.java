package com.suzei.racoon.ui.group;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.auth.login.LoginActivity;
import com.suzei.racoon.util.OnlineStatus;

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

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MembersActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
