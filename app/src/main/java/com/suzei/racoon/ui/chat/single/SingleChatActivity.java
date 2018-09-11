package com.suzei.racoon.ui.chat.single;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.messagelist.adapter.MessagesAdapter;
import com.suzei.racoon.ui.messagelist.data.MessagePresenter;
import com.suzei.racoon.ui.chat.ChatContract;
import com.suzei.racoon.ui.friend.FriendActivity;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.profile.data.ProfilePresenter;
import com.suzei.racoon.util.EmptyRecyclerView;
import com.suzei.racoon.util.TakePick;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class SingleChatActivity extends AppCompatActivity implements
        Contract.DetailsView<Users>,
        ChatContract.ChatView,
        Contract.AdapterView<MessagesAdapter> {

    public static final String EXTRA_ID = "_id";

    private EmojiPopup emojiPopup;
    private TakePick takePick;

    private ProfilePresenter profilePresenter;
    private SingleChatPresenter singleChatPresenter;
    private MessagePresenter messagePresenter;

    private DatabaseReference mUserChatRef;
    private DatabaseReference mMessageRef;
    private StorageReference mMessageStorage;

    private Users users;
    private String chatId;
    private String currentUserId;

    @BindDrawable(R.drawable.back) Drawable drawableBack;
    @BindDrawable(R.drawable.online) Drawable drawableOnline;
    @BindDrawable(R.drawable.offline) Drawable drawableOffline;
    @BindView(R.id.group_chat_room_root_view) View rootView;
    @BindView(R.id.chat_room_toolbar) Toolbar toolbar;
    @BindView(R.id.chat_room_toolbar_image) RoundedImageView toolbarImageView;
    @BindView(R.id.chat_room_emoji) ImageButton emojiButtonView;
    @BindView(R.id.chat_room_camera) ImageButton cameraView;
    @BindView(R.id.chat_room_send) ImageButton sendView;
    @BindView(R.id.chat_room_name) TextView nameView;
    @BindView(R.id.chat_room_recycler_refresh_layout) RecyclerRefreshLayout refreshLayout;
    @BindView(R.id.chat_room_list) EmptyRecyclerView listMessagesView;
    @BindView(R.id.chat_room_empty) LinearLayout emptyLayout;
    @BindView(R.id.chat_room_image) RoundedImageView imageView;
    @BindView(R.id.chat_room_message_input) EmojiEditText mesageView;
    @BindView(R.id.chat_room_status) ImageView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        initBundle();
        initObjects();
        setUpToolbar();
        setUpEmojiKeyboard();
        setUpTakePick();
        setUpRecyclerView();
        setUpListeners();
    }

    private void initBundle() {
        chatId = getIntent().getStringExtra(EXTRA_ID);
    }

    private void initObjects() {
        ButterKnife.bind(this);
        profilePresenter = new ProfilePresenter(this);
        singleChatPresenter = new SingleChatPresenter(this);
        messagePresenter = new MessagePresenter(this);

        currentUserId = FirebaseAuth.getInstance().getUid();
        mUserChatRef = FirebaseDatabase.getInstance().getReference()
                .child("user_chats").child(currentUserId).child(chatId);
        mMessageRef = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(currentUserId).child(chatId);
        mMessageStorage = FirebaseStorage.getInstance().getReference().child("messages")
                .child(chatId).child("pictures").child(String.valueOf(System.currentTimeMillis()));
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(drawableBack);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpEmojiKeyboard() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiPopupShownListener(() ->
                        emojiButtonView.setImageResource(R.drawable.keyboard))
                .setOnEmojiPopupDismissListener(() ->
                        emojiButtonView.setImageResource(R.drawable.emoji))
                .build(mesageView);
    }

    private void setUpTakePick() {
        takePick = new TakePick(this, this, new TakePick.ImageListener() {

            @Override
            public void onEmojiPick(String image) {
                singleChatPresenter.sendMessage(
                        chatId,
                        currentUserId,
                        "image",
                        image
                );
            }

            @Override
            public void onCameraGalleryPick(byte[] imageByte) {
                UploadTask uploadTask = mMessageStorage.putBytes(imageByte);
                takePick.uploadThumbnailToStorage(mMessageStorage, uploadTask, image_url ->
                        singleChatPresenter.sendMessage(
                        chatId,
                        currentUserId,
                        "image",
                        image_url
                ));
            }

        });
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        listMessagesView.setLayoutManager(layoutManager);
        listMessagesView.setEmptyView(emptyLayout);
    }

    private void setUpListeners() {
        messagePresenter.start(currentUserId, chatId);
        refreshLayout.setOnRefreshListener(() -> {
            messagePresenter.loadMore();
            refreshLayout.setRefreshing(false);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        profilePresenter.showUserDetails(chatId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        profilePresenter.destroy(chatId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messagePresenter.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        takePick.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.chat_room_emoji)
    public void onEmojiClick() {
        emojiPopup.toggle();
    }

    @OnClick(R.id.chat_room_camera)
    public void onCameraClick() {
        takePick.showPicker();
    }

    @OnClick(R.id.chat_room_send)
    public void onSendClick() {
        String message = mesageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        sendView.setEnabled(false);

        singleChatPresenter.sendMessage(
                chatId,
                currentUserId,
                "text",
                message
        );
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onLoadSuccess(Users users) {
        this.users = users;
        users.setUid(chatId);

        nameView.setText(users.getName());
        Picasso.get().load(users.getImage()).fit().centerCrop().into(imageView);
        Picasso.get().load(users.getImage()).fit().centerCrop().into(toolbarImageView);

        if (users.isOnline()) {
            statusView.setImageDrawable(drawableOnline);
        } else {
            statusView.setImageDrawable(drawableOffline);
        }

    }

    @Override
    public void onLoadFailed(DatabaseError error) {

    }

    @Override
    public void sendSuccess() {
        sendView.setEnabled(true);
        mesageView.setText("");
    }

    @Override
    public void sendFailed() {
        sendView.setEnabled(true);
    }

    @Override
    public void setAdapter(MessagesAdapter adapter) {
        if (adapter != null) {
            listMessagesView.setAdapter(adapter);
        } else {
            Timber.i("Adapter is null");
        }
    }

    @Override
    public void loadFailed() {
        Timber.i("Adapter is null");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_chat_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_delete_conv:
                deleteConversation();
                return true;

            case R.id.menu_visit_profile:
                visitProfile();
                return true;

            default:
                throw new IllegalArgumentException("Invalid menu Id= " + id);
        }

    }

    private void deleteConversation() {
        mUserChatRef.removeValue();
        mMessageRef.removeValue();
        finish();
        Toast.makeText(this, "Conversation Deleted" , Toast.LENGTH_SHORT).show();
    }

    private void visitProfile() {
        Intent profileIntent = new Intent(this, FriendActivity.class);
        profileIntent.putExtra(FriendActivity.EXTRA_PROFILE_DETAILS, users);
        startActivity(profileIntent);
    }

}
