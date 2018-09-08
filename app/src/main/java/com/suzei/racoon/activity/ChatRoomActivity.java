package com.suzei.racoon.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.callback.ChatListener;
import com.suzei.racoon.fragment.GroupChatRoomFragment;
import com.suzei.racoon.fragment.SingleChatRoomFragment;
import com.suzei.racoon.friend.ui.FriendActivity;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.FirebaseExceptionUtil;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class ChatRoomActivity extends AppCompatActivity {

    //TODO Add typing indicator for sending messages

    public static final String EXTRA_CHAT_TYPE = "chat_type";
    public static final String EXTRA_DETAILS = "details";

    private Users users;
    private Groups groups;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserChatRef;
    private DatabaseReference mMessageRef;
    private DatabaseReference mNotifCountRef;

    private ChatListener mListener;

    private EmojiPopup emojiPopup;

    private String currentUserId;

    private int mChatType;
    private String mId;
    private String mName;
    private String mImage;
    private boolean mIsOnline;

    private ArrayList<String> mMembers;
    private ArrayList<String> mAdmins;

    @BindDrawable(R.drawable.back) Drawable drawableBack;
    @BindDrawable(R.drawable.online) Drawable drawableOnline;
    @BindDrawable(R.drawable.offline) Drawable drawableOffline;
    @BindView(R.id.chat_room_root_view) View rootView;
    @BindView(R.id.chat_room_toolbar) Toolbar toolbar;
    @BindView(R.id.chat_room_image) RoundedImageView imageView;
    @BindView(R.id.chat_room_emoji) ImageButton emojiButtonView;
    @BindView(R.id.chat_room_name) TextView nameView;
    @BindView(R.id.chat_room_status) ImageView statusView;
    @BindView(R.id.chat_room_fragment_container) FrameLayout fragmentContainerView;
    @BindView(R.id.chat_room_message_input) EmojiEditText mesageView;

    public class ChatType {
        public static final int SINGLE_CHAT = 0;
        public static final int GROUP_CHAT = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initBundleData();
        initObjects();
        setUpToolbar();
        setUpEmojiKeyboard();
        showFragmentMessage();
        decrementNotifCount();
        updateSeenThisChat();
    }

    private void initBundleData() {
        mChatType = getIntent().getIntExtra(EXTRA_CHAT_TYPE, -1);

            switch (mChatType) {

                case ChatType.SINGLE_CHAT:
                    users = getIntent().getParcelableExtra(EXTRA_DETAILS);
                    mId = users.getUid();
                    break;

                case ChatType.GROUP_CHAT:
                    groups = getIntent().getParcelableExtra(EXTRA_DETAILS);;
                    mId = groups.getUid();
                    break;

                default:
                    throw new IllegalArgumentException("Invalid chat type = " + mChatType);
            }

        Timber.i("type = %s", mChatType);
        Timber.i("ID = %s", mId);
    }

    private void initObjects() {
        ButterKnife.bind(this);
        currentUserId = FirebaseAuth.getInstance().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserChatRef = mRootRef.child("user_chats").child(currentUserId).child(mId);
        mMessageRef = mRootRef.child("messages").child(currentUserId).child(mId);
        mNotifCountRef = mRootRef.child("notification_count").child(currentUserId).child("chats");
    }

    private void showChatDetails() {
        switch (mChatType) {

            case ChatType.SINGLE_CHAT:
                showUserDetails();
                break;

            case ChatType.GROUP_CHAT:
                showGroupDetails();
                break;
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(drawableBack);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            Timber.e("ActionBar is null");
        }
    }

    private void setUpEmojiKeyboard() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiPopupShownListener(() ->
                        emojiButtonView.setImageResource(R.drawable.keyboard))
                .setOnEmojiPopupDismissListener(() ->
                        emojiButtonView.setImageResource(R.drawable.emoji))
                .build(mesageView);
    }

    private void showFragmentMessage() {
        Bundle bundle = new Bundle();
        Fragment fragment;

        switch (mChatType) {

            case ChatType.SINGLE_CHAT:
                fragment = new SingleChatRoomFragment();
                bundle.putString(SingleChatRoomFragment.EXTRA_ID, mId);
                bundle.putString(SingleChatRoomFragment.EXTRA_IMAGE, mImage);
                fragment.setArguments(bundle);
                break;

            case ChatType.GROUP_CHAT:
                fragment = new GroupChatRoomFragment();
                bundle.putString(GroupChatRoomFragment.EXTRA_ID, mId);
                bundle.putString(GroupChatRoomFragment.EXTRA_IMAGE, mImage);
                bundle.putStringArrayList(GroupChatRoomFragment.EXTRA_MEMBERS, mMembers);
                fragment.setArguments(bundle);
                break;

            default:
                throw new IllegalArgumentException("Invalid chat type = " + mChatType);
        }

        showFragment(fragment);
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(fragmentContainerView.getId(), fragment);
        ft.commit();
    }

    private void decrementNotifCount() {
        mUserChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChildren()) {
                    return;
                }

                boolean isSeen = dataSnapshot.child("seen").getValue(Boolean.class);
                if (isSeen) {
                    return;
                }

                mNotifCountRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = dataSnapshot.child("count").getValue(Integer.class);
                        int updatedCount = count - 1;
                        mNotifCountRef.child("count").setValue(updatedCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        FirebaseExceptionUtil.databaseError(ChatRoomActivity.this,
                                databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSeenThisChat() {
        HashMap<String, Object> seenChat = new HashMap<>();
        seenChat.put("seen", true);
        seenChat.put("timestamp", ServerValue.TIMESTAMP);

        if (mChatType == ChatType.SINGLE_CHAT) {
            seenChat.put("type", "single");
        } else if (mChatType == ChatType.GROUP_CHAT){
            seenChat.put("type", "group");
        }

        mUserChatRef.updateChildren(seenChat).addOnSuccessListener(aVoid ->
                Toast.makeText(ChatRoomActivity.this, "Updated",
                        Toast.LENGTH_SHORT).show());
    }

    public void setChatListener(ChatListener listener) {
        this.mListener = listener;
    }

    @OnClick(R.id.chat_room_emoji)
    public void onEmojiClick() {
        emojiPopup.toggle();
    }

    @OnClick(R.id.chat_room_camera)
    public void onCameraClick() {
        mListener.onCameraClick();
    }

    @OnClick(R.id.chat_room_send)
    public void onSendClick(ImageButton button) {
        String message = mesageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        if (mListener != null) {
            button.setEnabled(false);
            mListener.onSendClick(mesageView, button, message);
        } else {
            throw new IllegalArgumentException("Chat Listener is null");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (mChatType) {
            case ChatType.SINGLE_CHAT:
                getMenuInflater().inflate(R.menu.single_chat_options, menu);
                return true;

            case ChatType.GROUP_CHAT:
                getMenuInflater().inflate(R.menu.group_chat_options, menu);
                return true;

            default:
                throw new IllegalArgumentException("Invalid chat type = " + mChatType);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_visit_profile:
                visitProfile();
                return true;

            case R.id.menu_delete_conv:
                deleteConversation();
                return true;

            case R.id.menu_view_members:
                viewMembers();
                return true;

            case R.id.menu_add_members:
                addMembers();
                return true;

            case R.id.menu_manage_group:
                // Only admin can access this
                manageGroup();
                return true;

            default:
                throw new IllegalArgumentException("Invalid menu id = " + item.getItemId());
        }
    }

    private void visitProfile() {
        Timber.i("ID = %s", users.getUid());
        Intent intent = new Intent(this, FriendActivity.class);
        intent.putExtra(FriendActivity.EXTRA_PROFILE_DETAILS, users);
        startActivity(intent);
    }

    private void deleteConversation() {
        mUserChatRef.removeValue();
        mMessageRef.removeValue();
        finish();
        Toast.makeText(this, "Conversation Deleted" , Toast.LENGTH_SHORT).show();
    }

    private void viewMembers() {
        Bundle args = new Bundle();
        args.putString(MembersActivity.EXTRA_ID, mId);
        args.putInt(MembersActivity.EXTRA_MEMBERS_TYPE, MembersActivity.MembersType.VIEW_MEMBERS);

        Intent membersIntent = new Intent(this, MembersActivity.class);
        membersIntent.putExtras(args);
        startActivity(membersIntent);
    }

    private void addMembers() {
        Bundle args = new Bundle();
        args.putString(MembersActivity.EXTRA_ID, mId);
        args.putInt(MembersActivity.EXTRA_MEMBERS_TYPE, MembersActivity.MembersType.ADD_MEMBERS);
        args.putStringArrayList(MembersActivity.EXTRA_MEMBERS, mMembers);

        Intent membersIntent = new Intent(this, MembersActivity.class);
        membersIntent.putExtras(args);
        startActivity(membersIntent);
    }

    private void manageGroup() {
        if (!mAdmins.contains(currentUserId)) {
            Toast.makeText(this, "Only admin users can manage the group",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent groupIntent = new Intent(this, GroupActivity.class);
        groupIntent.putExtra(GroupActivity.EXTRA_ID, mId);
        startActivity(groupIntent);
    }

    private void showUserDetails() {
        mRootRef.child("users").child(mId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.getKey();
                users = dataSnapshot.getValue(Users.class);
                users.setUid(uid);
                mName = users.getName();
                mImage = users.getImage();
                mIsOnline = users.isOnline();

                nameView.setText(mName);
                Picasso.get().load(mImage).into(imageView);

                if (mIsOnline) {
                    statusView.setImageDrawable(drawableOnline);
                } else {
                    statusView.setImageDrawable(drawableOffline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FirebaseExceptionUtil.databaseError(ChatRoomActivity.this,
                        databaseError.toException());
            }
        });
    }

    private void showGroupDetails() {
        mRootRef.child("groups").child(mId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groups = dataSnapshot.getValue(Groups.class);
                mName = groups.getName();
                mImage = groups.getImage();
                mMembers = new ArrayList<>(groups.getMembers().keySet());
                mAdmins = new ArrayList<>(groups.getAdmin().keySet());

                nameView.setText(mName);
                Picasso.get().load(mImage).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FirebaseExceptionUtil.databaseError(ChatRoomActivity.this,
                        databaseError.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showChatDetails();
    }

    @Override
    protected void onStop() {
        if (emojiPopup != null) {
            emojiPopup.dismiss();
        }

        super.onStop();
    }
}
