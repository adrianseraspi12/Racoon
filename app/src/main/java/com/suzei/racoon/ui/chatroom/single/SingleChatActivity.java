package com.suzei.racoon.ui.chatroom.single;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.ui.auth.login.LoginActivity;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.chatroom.ChatContract;
import com.suzei.racoon.ui.chatroom.messagelist.MessagePresenter;
import com.suzei.racoon.ui.chatroom.messagelist.MessagesAdapter;
import com.suzei.racoon.ui.friend.FriendActivity;
import com.suzei.racoon.ui.profile.ProfilePresenter;
import com.suzei.racoon.util.OnlineStatus;
import com.suzei.racoon.util.TakePicture;
import com.suzei.racoon.view.EmptyRecyclerView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import butterknife.BindColor;
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
    private TakePicture takePicture;

    private ProfilePresenter profilePresenter;
    private SingleChatPresenter singleChatPresenter;
    private MessagePresenter messagePresenter;

    private DatabaseReference mUserChatRef;
    private DatabaseReference mMessageRef;
    private StorageReference mMessageStorage;

    private Users users;
    private String chatId;
    private String currentUserId;

    @BindColor(android.R.color.white) int whiteColor;
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
        setContentView(R.layout.activity_chat_room);
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
        takePicture = new TakePicture(this, this, new TakePicture.ImageListener() {

            @Override
            public void onEmojiPick(String image) {
                singleChatPresenter.sendMessage(
                        chatId,
                        "image",
                        image
                );
            }

            @Override
            public void onCameraGalleryPick(byte[] imageByte) {
                UploadTask uploadTask = mMessageStorage.putBytes(imageByte);
                takePicture.uploadThumbnailToStorage(mMessageStorage, uploadTask, image_url ->
                        singleChatPresenter.sendMessage(
                        chatId,
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
    protected void onStop() {
        super.onStop();
        profilePresenter.destroy(chatId);
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
    protected void onDestroy() {
        super.onDestroy();
        messagePresenter.destroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(SingleChatActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            profilePresenter.showUserDetails(chatId);
            singleChatPresenter.seenChat(chatId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        takePicture.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.chat_room_emoji)
    public void onEmojiClick() {
        emojiPopup.toggle();
    }

    @OnClick(R.id.chat_room_camera)
    public void onCameraClick() {
        takePicture.showPicker();
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
    public void onLoadFailed(String message) {

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
