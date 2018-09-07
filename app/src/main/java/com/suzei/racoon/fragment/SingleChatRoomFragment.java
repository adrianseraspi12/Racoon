package com.suzei.racoon.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.ChatRoomActivity;
import com.suzei.racoon.adapter.MessagesAdapter;
import com.suzei.racoon.callback.ChatListener;
import com.suzei.racoon.model.Messages;
import com.suzei.racoon.model.Users;
import com.suzei.racoon.util.EmptyRecyclerView;
import com.suzei.racoon.util.FirebaseExceptionUtil;
import com.suzei.racoon.util.ImageHelper;
import com.vanniktech.emoji.EmojiEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleChatRoomFragment extends Fragment implements ChatListener {

    public static final String EXTRA_ID = "single_chat_id";
    public static final String EXTRA_IMAGE = "single_chat_image";
    private static final int PICK_CAMERA = 0;
    private static final int PICK_GALLERY = 1;

    private DatabaseReference mRootRef;
    private DatabaseReference mMessageRef;
    private DatabaseReference mUserChatRef;
    private StorageReference mMessageStorage;

    private ImageHelper imageHelper;
    private MessagesAdapter mAdapter;
    private Unbinder unbinder;

    private Uri mImageFromCameraUri;
    private String currentUserId;

    private String mId;
    private String mImage;

    @BindView(R.id.chat_room_recycler_refresh_layout) RecyclerRefreshLayout refreshLayout;
    @BindView(R.id.chat_room_messages_list) EmptyRecyclerView listMessagesView;
    @BindView(R.id.chat_room_empty) LinearLayout emptyLayout;
    @BindView(R.id.chat_room_image) RoundedImageView imageView;

    public SingleChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_room_message_list, container, false);
        initDetailsArgs();
        initObjects(view);
        setUpRecyclerView();
        setUpAdapter();
        setListeners();
        return view;
    }

    private void initDetailsArgs() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mId = bundle.getString(EXTRA_ID);
            mImage = bundle.getString(EXTRA_IMAGE);
        }

        Timber.i("ID = %s", mId);
        Timber.i("Image = %s", mImage);
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        imageHelper = new ImageHelper(getContext());
        currentUserId = FirebaseAuth.getInstance().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserChatRef = mRootRef.child("user_chats");
        mMessageRef = mRootRef.child("messages").child(currentUserId).child(mId);
        mMessageStorage = FirebaseStorage.getInstance().getReference().child("messages")
                .child(mId).child("pictures").child(String.valueOf(System.currentTimeMillis()));

        Picasso.get().load(mImage).into(imageView);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        listMessagesView.setLayoutManager(layoutManager);
        listMessagesView.setEmptyView(emptyLayout);
    }

    private void setUpAdapter() {
        mAdapter = new MessagesAdapter(Messages.class, mMessageRef, 10);
        listMessagesView.setAdapter(mAdapter);
    }

    private void setListeners() {
        Activity activity = getActivity();
        if (activity instanceof ChatRoomActivity) {
            ((ChatRoomActivity) activity).setChatListener(this);
        }

        refreshLayout.setOnRefreshListener(() -> {

            if (mAdapter.getItemCount() != 0) {
                mAdapter.more();
                Toast.makeText(getContext(), "Loaded", Toast.LENGTH_SHORT).show();
            }

            refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mAdapter.cleanup();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void saveImageToStorageAndDB(byte[] imageByte) {
        UploadTask uploadTask = mMessageStorage.putBytes(imageByte);
        imageHelper.uploadThumbnailToStorage(mMessageStorage, uploadTask, image_url -> {
            HashMap<String, Object> messageDetailsMap = getMessageDetailsMap(image_url,
                    "image");

            HashMap<String, Object> saveMessageMap = new HashMap<>();
            saveMessageMap.put("messages/" + currentUserId + "/" + mId, messageDetailsMap);
            saveMessageMap.put("messages/" + mId + "/" + currentUserId, messageDetailsMap);

            mRootRef.updateChildren(saveMessageMap, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    updateChatDB("Image");
                } else {
                    FirebaseExceptionUtil.databaseError(getContext(),
                            databaseError.toException());
                }
            });
        });
    }

    @Override
    public void onCameraClick() {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(getContext());
        View bottomView = getLayoutInflater().inflate(R.layout.dialog_image_picker, null);
        LinearLayout pickCamera = bottomView.findViewById(R.id.dialog_image_picker_camera);
        LinearLayout pickGallery = bottomView.findViewById(R.id.dialog_image_picker_gallery);
        LinearLayout pickEmoji = bottomView.findViewById(R.id.dialog_image_picker_emoji);

        pickEmoji.setVisibility(View.GONE);

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
    }

    @Override
    public void onSendClick(EmojiEditText editText, ImageButton button, String message) {
        String messageId = mRootRef.push().getKey();
        HashMap<String, Object> messageDetailsMap = getMessageDetailsMap(message, "text");

        HashMap<String, Object> saveMessageMap = new HashMap<>();
        saveMessageMap.put("messages/" + currentUserId + "/" + mId + "/" + messageId,
                messageDetailsMap);
        saveMessageMap.put("messages/" + mId + "/" + currentUserId + "/" + messageId,
                messageDetailsMap);

        mRootRef.updateChildren(saveMessageMap, (databaseError, databaseReference) -> {

            if (databaseError == null) {
                updateChatDB(message);
                editText.setText("");

            } else {
                FirebaseExceptionUtil.databaseError(getContext(), databaseError.toException());
            }
            button.setEnabled(true);

        });
    }

    private void updateChatDB(String message) {
        HashMap<String, Object> chatDetailMap = new HashMap<>();
        chatDetailMap.put("timestamp", ServerValue.TIMESTAMP);
        chatDetailMap.put("last_message", message);
        chatDetailMap.put("type", "single");

        HashMap<String, Object> chatMap = new HashMap<>();
        chatDetailMap.put("seen", true);
        chatMap.put(currentUserId + "/" + mId, chatDetailMap);

        chatDetailMap.put("seen", false);
        chatMap.put(mId + "/" + currentUserId, chatDetailMap);

        mUserChatRef.updateChildren(chatMap);
    }

    private HashMap<String, Object> getMessageDetailsMap(String message, String type) {
        HashMap<String, Object> seenMap = new HashMap<>();
        seenMap.put(currentUserId, true);
        seenMap.put(mId, false);

        HashMap<String, Object> messageDetailsMap = new HashMap<>();
        messageDetailsMap.put("message", message);
        messageDetailsMap.put("type", type);
        messageDetailsMap.put("from", currentUserId);
        messageDetailsMap.put("timestamp", ServerValue.TIMESTAMP);
        messageDetailsMap.put("seen", seenMap);

        return messageDetailsMap;
    }

}
