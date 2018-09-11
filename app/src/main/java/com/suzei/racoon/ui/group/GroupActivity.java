package com.suzei.racoon.ui.group;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.ui.base.Contract;
import com.suzei.racoon.util.TakePick;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class GroupActivity extends AppCompatActivity implements Contract.DetailsView<Groups> {

    public static final String EXTRA_ID = "group_id";
    private static final int CHANGE_INPUT_NAME = 0;
    private static final int CHANGE_INPUT_DESC = 1;

    private DatabaseReference mGroupsRef;
    private StorageReference mGroupStorage;

    private TakePick takePick;
    private GroupDetailsPresenter groupDetailsPresenter;
    private ContextThemeWrapper themeWrapper;

    private Bundle args;

    private String mId;

    @BindColor(android.R.color.black) int colorBlack;
    @BindView(R.id.group_back) ImageButton backView;
    @BindView(R.id.group_image) RoundedImageView groupImageView;
    @BindView(R.id.group_name) TextView nameView;
    @BindView(R.id.group_description) TextView descView;
    @BindView(R.id.group_add_members) Button addMembersView;
    @BindView(R.id.group_view_members) Button viewMembersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        initGroupBundle();
        initObjects();
        setUpTakePick();
    }

    private void initGroupBundle() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mId = bundle.getString(EXTRA_ID);
        }

        Timber.i("id = %s", mId);
    }

    private void initObjects() {
        ButterKnife.bind(this);
        args = new Bundle();
        groupDetailsPresenter = new GroupDetailsPresenter(this);
        themeWrapper = new ContextThemeWrapper(this, R.style.AlertDialogTheme);
        mGroupsRef = FirebaseDatabase.getInstance().getReference().child("groups").child(mId);
        mGroupStorage = FirebaseStorage.getInstance().getReference().child("groups").child(mId);
    }

    private void setUpTakePick() {
        takePick = new TakePick(
                GroupActivity.this,
                this,
                new TakePick.ImageListener() {

                    @Override
                    public void onEmojiPick(String image) {
                        mGroupsRef.child("image").setValue(image);
                    }

                    @Override
                    public void onCameraGalleryPick(byte[] imageByte) {
                        UploadTask uploadTask = mGroupStorage.putBytes(imageByte);
                        takePick.uploadThumbnailToStorage(
                                mGroupStorage,
                                uploadTask,
                                image_url -> mGroupsRef.child("image").setValue(image_url)
                        );
                    }
                });
    }

    @OnClick(R.id.group_back)
    public void backClick() {
        finish();
    }

    @OnClick({R.id.group_name, R.id.group_description})
    public void onTextFieldClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.group_name:
                String name = nameView.getText().toString().trim();
                showInputDialog("Change name", name, CHANGE_INPUT_NAME);
                break;

            case R.id.group_description:
                String desc = descView.getText().toString().trim();
                showInputDialog("Change Description", desc, CHANGE_INPUT_DESC);
                break;

            default:
                throw new IllegalArgumentException("Invalid Id= " + id);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        takePick.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.group_image)
    public void onImageClick() {
        takePick.showPicker();
    }

    @OnClick(R.id.group_add_members)
    public void onAddMemberClick() {
        args.putString(MembersActivity.EXTRA_ID, mId);
        args.putInt(MembersActivity.EXTRA_MEMBERS_TYPE, MembersActivity.MembersType.ADD_MEMBERS);
        Intent membersIntent = new Intent(GroupActivity.this, MembersActivity.class);
        membersIntent.putExtras(args);
        startActivity(membersIntent);
    }

    @OnClick(R.id.group_view_members)
    public void onViewMemberClick() {
        args.putInt(MembersActivity.EXTRA_MEMBERS_TYPE, MembersActivity.MembersType.VIEW_MEMBERS);
        Intent membersIntent = new Intent(GroupActivity.this, MembersActivity.class);
        membersIntent.putExtras(args);
        startActivity(membersIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupDetailsPresenter.showGroupDetails(mId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        groupDetailsPresenter.destroy(mId);
    }

    private void showInputDialog(String title, String defaultText, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper);

        builder.setTitle(title);

        EditText editText = new EditText(this);
        editText.setText(defaultText);
        editText.setTextColor(colorBlack);
        builder.setView(editText);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String inputtedText = editText.getText().toString().trim();

            switch (type) {
                case CHANGE_INPUT_DESC:
                    mGroupsRef.child("description").setValue(inputtedText);
                    break;

                case CHANGE_INPUT_NAME:
                    mGroupsRef.child("name").setValue(inputtedText);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid dialog type= " + type);
            }

            dialog.dismiss();

        });

        builder.show();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onLoadSuccess(Groups data) {
        ArrayList<String> members = new ArrayList<>(data.getMembers().keySet());

        args.putStringArrayList(MembersActivity.EXTRA_MEMBERS, members);

        nameView.setText(data.getName());
        descView.setText(data.getDescription());
        Picasso.get().load(data.getImage()).fit().centerCrop().into(groupImageView);
    }

    @Override
    public void onLoadFailed(DatabaseError error) {

    }
}
