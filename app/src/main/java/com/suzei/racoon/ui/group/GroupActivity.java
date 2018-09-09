package com.suzei.racoon.ui.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.MembersActivity;
import com.suzei.racoon.adapter.DialogEmojiAdapter;
import com.suzei.racoon.model.Emoji;
import com.suzei.racoon.util.ImageHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class GroupActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "group_id";
    private static final int CHANGE_INPUT_NAME = 0;
    private static final int CHANGE_INPUT_DESC = 1;
    private static final int PICK_CAMERA = 2;
    private static final int PICK_GALLERY = 3;

    private DatabaseReference mEmojiRef;
    private DatabaseReference mGroupsRef;
    private StorageReference mGroupStorage;
    private ValueEventListener eventListener;

    private ImageHelper imageHelper;
    private ContextThemeWrapper themeWrapper;

    private Bundle args;

    private Uri mImageFromCameraUri;

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
        showDetails();
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
        imageHelper = new ImageHelper(this);
        themeWrapper = new ContextThemeWrapper(this, R.style.AlertDialogTheme);
        mEmojiRef = FirebaseDatabase.getInstance().getReference().child("emoji");
        mGroupsRef = FirebaseDatabase.getInstance().getReference().child("groups").child(mId);
        mGroupStorage = FirebaseStorage.getInstance().getReference().child("groups").child(mId);
    }

    private void showDetails() {
        eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String desc = dataSnapshot.child("description").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);

                HashMap<String, Object> membersMap =
                        (HashMap<String, Object>) dataSnapshot.child("members").getValue();
                HashMap<String, Object> adminsMap =
                        (HashMap<String, Object>) dataSnapshot.child("admin").getValue();

                ArrayList<String> members = new ArrayList<>(membersMap.keySet());
                ArrayList<String> admins = new ArrayList<>(adminsMap.keySet());

                args.putStringArrayList(MembersActivity.EXTRA_MEMBERS, members);
                args.putStringArrayList(MembersActivity.EXTRA_ADMIN, admins);

                nameView.setText(name);
                descView.setText(desc);
                Picasso.get().load(image).into(groupImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

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
        byte[] imageByte = new byte[0];
        switch (requestCode) {
            case PICK_CAMERA:
                if (resultCode == RESULT_OK) {
                    File thumbCameraFilePath = new File(mImageFromCameraUri.getPath());
                    Bitmap compressedCameraBitmap = imageHelper.compressedImage(thumbCameraFilePath);
                    imageByte = imageHelper.thumbToByteArray(compressedCameraBitmap);

                }
                break;

            case PICK_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri galleryUri = data.getData();
                    File galleryFile = imageHelper.getRealPathFromUri(galleryUri);
                    Bitmap compressedGalleryBitmap = imageHelper.compressedImage(galleryFile);
                    imageByte = imageHelper.thumbToByteArray(compressedGalleryBitmap);
                }
                break;
        }

        if (imageByte.length != 0) {
            saveImageToStorageAndDB(imageByte);
        }
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

    @OnClick(R.id.group_image)
    public void onImageClick() {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(this);
        View bottomView = getLayoutInflater().inflate(R.layout.dialog_image_picker, null);
        LinearLayout pickCamera = bottomView.findViewById(R.id.dialog_image_picker_camera);
        LinearLayout pickGallery = bottomView.findViewById(R.id.dialog_image_picker_gallery);
        LinearLayout pickEmoji = bottomView.findViewById(R.id.dialog_image_picker_emoji);

        bottomDialog.setContentView(bottomView);
        bottomDialog.show();

        pickCamera.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photo = null;

            try {
                photo = imageHelper.createImageFile();
                photo.delete();
            } catch (IOException e) {
                Timber.e(e);
                e.printStackTrace();
                Toast.makeText(this, "Please check your SD card",
                        Toast.LENGTH_SHORT).show();
            }

            mImageFromCameraUri = Uri.fromFile(photo);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFromCameraUri);
            bottomDialog.dismiss();
            startActivityForResult(cameraIntent, PICK_CAMERA);
        });

        pickGallery.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
            startActivityForResult(chooserIntent, PICK_GALLERY);
            bottomDialog.dismiss();
        });

        pickEmoji.setOnClickListener(v -> {
            bottomDialog.dismiss();

            View emojiView = View.inflate(this, R.layout.dialog_emoji_picker, null);
            RecyclerView listEmojiView = emojiView.findViewById(R.id.dialog_emoji_list);
            listEmojiView.setLayoutManager(new LinearLayoutManager(this));

            AlertDialog emojiDialog = new AlertDialog.Builder(themeWrapper).create();
            emojiDialog.setTitle("Select Emoji");
            emojiDialog.setView(emojiView);
            emojiDialog.setCancelable(true);

            FirebaseRecyclerOptions<Emoji> options = new FirebaseRecyclerOptions.Builder<Emoji>()
                    .setQuery(mEmojiRef.orderByChild("name"), Emoji.class).setLifecycleOwner(this)
                    .build();

            DialogEmojiAdapter emojiAdapter = new DialogEmojiAdapter(options, image -> {
                mGroupsRef.child("image").setValue(image);
                emojiDialog.dismiss();
            });

            listEmojiView.setAdapter(emojiAdapter);

            emojiDialog.show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGroupsRef.addValueEventListener(eventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGroupsRef.removeEventListener(eventListener);
    }

    private void saveImageToStorageAndDB(byte[] imageByte) {
        UploadTask uploadTask = mGroupStorage.putBytes(imageByte);
        imageHelper.uploadThumbnailToStorage(mGroupStorage, uploadTask, image_url ->
                mGroupsRef.child("image").setValue(image_url));
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
}
