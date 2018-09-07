package com.suzei.racoon.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
import com.suzei.racoon.adapter.DialogEmojiAdapter;
import com.suzei.racoon.model.Emoji;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.FirebaseExceptionUtil;
import com.suzei.racoon.util.ImageHelper;
import com.suzei.racoon.util.NumberPickerUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements ValueEventListener {

    private static final int PICK_CAMERA = 10;
    private static final int PICK_GALLERY = 11;
    private static final int CHANGE_INPUT_BIO = 2;
    private static final int CHANGE_INPUT_NAME = 4;

    private DatabaseReference mUserRef;
    private DatabaseReference mEmojiRef;
    private StorageReference mUserStorage;
    private ImageHelper mImageHelper;
    private Unbinder unbinder;
    private ContextThemeWrapper themeWrapper;

    private Uri mImageFromCameraUri;

    @BindString(R.string.add_bio) String stringAddBio;
    @BindColor(R.color.colorPrimary) int colorPrimary;
    @BindColor(android.R.color.black) int colorBlack;
    @BindDrawable(R.drawable.gender_male) Drawable drawableMale;
    @BindDrawable(R.drawable.gender_female) Drawable drawableFemale;
    @BindDrawable(R.drawable.gender_unknown) Drawable drawableUnknown;
    @BindView(R.id.profile_back) ImageButton backView;
    @BindView(R.id.profile_age) TextView ageView;
    @BindView(R.id.profile_description) TextView descView;
    @BindView(R.id.profile_gender) TextView genderView;
    @BindView(R.id.profile_image) RoundedImageView picView;
    @BindView(R.id.profile_name) TextView nameView;
    @BindView(R.id.profile_action_buttons_layout) LinearLayout actionButtonsLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);
        initObjects(view);
        return view;
    }

    private void initObjects(View view) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        mEmojiRef = FirebaseDatabase.getInstance().getReference().child("emoji");
        mUserStorage = FirebaseStorage.getInstance().getReference().child("users").child("pictures")
                .child(currentUserId);
        mImageHelper = new ImageHelper(getContext());
        unbinder = ButterKnife.bind(this, view);
        themeWrapper = new ContextThemeWrapper(getContext(), R.style.AlertDialogTheme);

        backView.setVisibility(View.GONE);
        actionButtonsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        byte[] imageByte = new byte[0];
        switch (requestCode) {
            case PICK_CAMERA:
                if (resultCode == RESULT_OK) {
                    File thumbCameraFilePath = new File(mImageFromCameraUri.getPath());
                    Bitmap compressedCameraBitmap = mImageHelper.compressedImage(thumbCameraFilePath);
                    imageByte = mImageHelper.thumbToByteArray(compressedCameraBitmap);

                }
                break;

            case PICK_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri galleryUri = data.getData();
                    File galleryFile = mImageHelper.getRealPathFromUri(galleryUri);
                    Bitmap compressedGalleryBitmap = mImageHelper.compressedImage(galleryFile);
                    imageByte = mImageHelper.thumbToByteArray(compressedGalleryBitmap);
                }
                break;
        }

        if (imageByte.length != 0) {
            saveImageToStorageAndDB(imageByte);
        }
    }

    @OnClick({R.id.profile_name, R.id.profile_description})
    public void OnTextFieldClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.profile_name:
                String name = nameView.getText().toString();
                showInputDialog("Change name", name, CHANGE_INPUT_NAME);
                break;

            case R.id.profile_description:
                String bio = descView.getText().toString();
                showInputDialog("Change bio", bio, CHANGE_INPUT_BIO);
                break;
        }

    }

    @OnClick({R.id.profile_age, R.id.profile_gender})
    public void OnAgeGenderClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.profile_age:
                showNumberPicker();
                break;

            case R.id.profile_gender:
                String gender = genderView.getText().toString();
                int passGender;
                switch (gender) {
                    case "Male":
                        passGender = 0;
                        break;

                    case "Female":
                        passGender = 1;
                        break;

                    case "Unknown":
                        passGender = 2;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid gender=" + gender);
                }
                showGenderPicker(passGender);
                break;
        }
    }

    @OnClick(R.id.profile_image)
    public void onImageClick() {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(getContext());
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
                photo = mImageHelper.createImageFile();
                photo.delete();
            } catch (IOException e) {
                Timber.e(e);
                e.printStackTrace();
                Toast.makeText(getContext(), "Please check your SD card",
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

            View emojiView = View.inflate(getContext(), R.layout.dialog_emoji_picker, null);
            RecyclerView listEmojiView = emojiView.findViewById(R.id.dialog_emoji_list);
            listEmojiView.setLayoutManager(new LinearLayoutManager(getContext()));

            AlertDialog emojiDialog = new AlertDialog.Builder(themeWrapper).create();
            emojiDialog.setTitle("Select Emoji");
            emojiDialog.setView(emojiView);
            emojiDialog.setCancelable(true);

            FirebaseRecyclerOptions<Emoji> options = new FirebaseRecyclerOptions.Builder<Emoji>()
                    .setQuery(mEmojiRef.orderByChild("name"), Emoji.class).setLifecycleOwner(this)
                    .build();

            DialogEmojiAdapter emojiAdapter = new DialogEmojiAdapter(options, image -> {
                mUserRef.child("image").setValue(image);
                emojiDialog.dismiss();
            });

            listEmojiView.setAdapter(emojiAdapter);

            emojiDialog.show();
        });

    }

    private void saveImageToStorageAndDB(byte[] imageByte) {
        UploadTask uploadTask = mUserStorage.putBytes(imageByte);
        mImageHelper.uploadThumbnailToStorage(mUserStorage, uploadTask, image_url ->
                mUserRef.child("image").setValue(image_url));
    }

    private void showGenderPicker(int gender) {
        CharSequence[] genderSeq = {"Male", "Female", "Unknown"};

        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper);
        builder.setTitle("Choose gender:");
        builder.setSingleChoiceItems(genderSeq, gender, null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Ok", (dialog, which) -> {
            ListView listView = ((AlertDialog) dialog).getListView();
            Object checkedItem = listView.getAdapter().getItem(listView.getCheckedItemPosition());
            mUserRef.child("gender").setValue(checkedItem);
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showNumberPicker() {
        int age = Integer.parseInt(ageView.getText().toString());
        NumberPicker numberPicker = new NumberPicker(getContext());
        NumberPickerUtil.setNumberPickerTextColor(numberPicker, colorBlack);
        numberPicker.setMaxValue(115);
        numberPicker.setMinValue(13);
        numberPicker.setValue(age);

        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper);
        builder.setMessage("Choose an age:");
        builder.setView(numberPicker);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            int selectedAge = numberPicker.getValue();
            mUserRef.child("age").setValue(selectedAge);
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void show(Users user) {
        nameView.setText(user.getName());
        ageView.setText(String.valueOf(user.getAge()));
        Picasso.get().load(user.getImage()).into(picView);
        genderView.setText(user.getGender());

        if (user.getBio().equals("")) {
            descView.setText(stringAddBio);
        } else {
            descView.setText(user.getBio());
        }

        if (user.getGender().equals("Male")) {

            genderView.setCompoundDrawablesWithIntrinsicBounds(drawableMale, null, null,
                    null);

        } else if (user.getGender().equals("Female")) {

            genderView.setCompoundDrawablesWithIntrinsicBounds(drawableFemale, null, null,
                    null);

        } else if (user.getGender().equals("Unknown")) {

            genderView.setCompoundDrawablesWithIntrinsicBounds(drawableUnknown, null,
                    null, null);

        }
    }

    private void showInputDialog(String title, String defaultText, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper);

        builder.setTitle(title);

        EditText editText = new EditText(getContext());
        editText.setText(defaultText);
        editText.setTextColor(colorBlack);
        builder.setView(editText);

        builder.setPositiveButton("Change",
                (dialog, which) -> {
            String inputtedText = editText.getText().toString().trim();

            switch (type) {
                case CHANGE_INPUT_BIO:
                    mUserRef.child("bio").setValue(inputtedText);
                    break;

                case CHANGE_INPUT_NAME:
                    mUserRef.child("name").setValue(inputtedText);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid dialog type= " + type);
            }

            dialog.dismiss();

        });

        builder.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mUserRef.addValueEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mUserRef.removeEventListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Users user = dataSnapshot.getValue(Users.class);

        if (user != null) {
            show(user);
        } else {
            //Timber user is null
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Exception e = databaseError.toException();
        FirebaseExceptionUtil.databaseError(getContext(), e);
    }
}
