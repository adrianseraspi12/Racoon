package com.suzei.racoon.ui.friend;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.ChatRoomActivity;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.ui.chat.single.SingleChatActivity;
import com.suzei.racoon.ui.friend.data.FriendPresenter;
import com.suzei.racoon.model.Users;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class FriendActivity extends AppCompatActivity implements Contract.DetailsView<String> {

    public static final String EXTRA_PROFILE_DETAILS = "profile_details";

    private FriendPresenter friendPresenter;

    private String currentUserId;
    private String mId;
    private String mCurrentState;

    private Users users;

    @BindDrawable(R.drawable.gender_male) Drawable drawableMale;
    @BindDrawable(R.drawable.gender_female) Drawable drawableFemale;
    @BindDrawable(R.drawable.gender_unknown) Drawable drawableUnknown;
    @BindString(R.string.add_friend) String stringAddFriend;
    @BindString(R.string.accept_friend) String stringAcceptFriend;
    @BindString(R.string.cancel_request) String stringCancelReq;
    @BindString(R.string.unfriend) String stringUnfriend;
    @BindView(R.id.profile_back) ImageButton backView;
    @BindView(R.id.profile_image) RoundedImageView imageView;
    @BindView(R.id.profile_name) TextView nameView;
    @BindView(R.id.profile_description) TextView bioView;
    @BindView(R.id.profile_age) TextView ageView;
    @BindView(R.id.profile_gender) TextView genderView;
    @BindView(R.id.profile_add_friend) Button addFriendView;
    @BindView(R.id.profile_message) Button messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        initBundleUserDetails();
        initObjects();
        showUserDetails();
    }

    private void initBundleUserDetails() {
        users = getIntent().getParcelableExtra(EXTRA_PROFILE_DETAILS);
        mId = users.getUid();
        Timber.i("ID = %s", mId);
    }

    private void initObjects() {
        ButterKnife.bind(this);
        currentUserId = FirebaseAuth.getInstance().getUid();
        friendPresenter = new FriendPresenter(this);
    }

    private void showUserDetails() {
        nameView.setText(users.getName());
        bioView.setText(users.getBio());
        genderView.setText(users.getGender());
        ageView.setText(String.valueOf(users.getAge()));
        Picasso.get().load(users.getImage()).fit().into(imageView);

        switch (users.getGender()) {
            case "Male":
                genderView.setCompoundDrawablesWithIntrinsicBounds(drawableMale, null,
                        null, null);
                break;

            case "Female":
                genderView.setCompoundDrawablesWithIntrinsicBounds(drawableFemale, null,
                        null, null);
                break;

            case "Unknown":
                genderView.setCompoundDrawablesWithIntrinsicBounds(drawableUnknown, null,
                        null, null);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        friendPresenter.readCurrentState(currentUserId, mId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        friendPresenter.destroy(currentUserId, mId);
    }

    @OnClick(R.id.profile_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.profile_add_friend)
    public void onAddFriendClick() {
        addFriendView.setEnabled(false);
        friendPresenter.sendRequest(currentUserId, mId, mCurrentState);
    }

    @OnClick(R.id.profile_message)
    public void onMessageClick() {
        Intent chatIntent = new Intent(FriendActivity.this, SingleChatActivity.class);
        chatIntent.putExtra(SingleChatActivity.EXTRA_ID, mId);
        startActivity(chatIntent);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onLoadSuccess(String currentState) {
        switch (currentState) {

            case "request_sent":
                mCurrentState = "request_sent";
                addFriendView.setText(stringCancelReq);
                break;

            case "friend":
                mCurrentState = "friend";
                addFriendView.setText(stringUnfriend);
                break;

            case "not_friend":
                mCurrentState = "not_friend";
                addFriendView.setText(stringAddFriend);
                break;

            case "request_received":
                mCurrentState = "request_received";
                addFriendView.setText(stringAcceptFriend);
                break;

            default:
                throw new IllegalArgumentException("Invalid current state = " + mCurrentState);
        }
        addFriendView.setEnabled(true);
    }

    @Override
    public void onLoadFailed(DatabaseError error) {

    }
}
