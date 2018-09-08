package com.suzei.racoon.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.R;
import com.suzei.racoon.activity.ChatRoomActivity;
import com.suzei.racoon.adapter.MessagesAdapter;
import com.suzei.racoon.callback.SendChatListener;
import com.suzei.racoon.chat.ChatContract;
import com.suzei.racoon.chat.group.GroupChatPresenter;
import com.suzei.racoon.model.Groups;
import com.suzei.racoon.model.Messages;
import com.suzei.racoon.util.EmptyRecyclerView;
import com.suzei.racoon.util.ImageHelper;
import com.vanniktech.emoji.EmojiEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatRoomFragment extends Fragment implements SendChatListener,
        ChatContract.ChatView, ChatRoomActivity.GroupDetailsListener {

    public static final String EXTRA_ID = "group_chat_id";
    public static final String EXTRA_IMAGE = "group_chat_image";
    public static final String EXTRA_MEMBERS = "group_chat_members";
    private static final int PICK_CAMERA = 0;
    private static final int PICK_GALLERY = 1;

    private GroupChatPresenter presenter;
    private DatabaseReference mRootRef;
    private DatabaseReference mMessageRef;
    private StorageReference mMessageStorage;

    private MessagesAdapter mAdapter;
    private ImageHelper imageHelper;
    private Unbinder unbinder;

    private String currentUserId;
    private Uri mImageFromCameraUri;

    private String mId;
    private String mImage;

    private ArrayList<String> mMembers;

    private EmojiEditText editText;
    private ImageButton button;

    @BindView(R.id.chat_room_recycler_refresh_layout) RecyclerRefreshLayout refreshLayout;
    @BindView(R.id.chat_room_messages_list) EmptyRecyclerView listMessagesView;
    @BindView(R.id.chat_room_empty) LinearLayout emptyLayout;
    @BindView(R.id.chat_room_image) RoundedImageView imageView;

    public GroupChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_room_message_list, container, false);
        initDetailsArg();
        initObjects(view);
        setUpRecyclerView();
        setUpAdapter();
        setListeners();
        return view;
    }

    private void initDetailsArg() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            mId = bundle.getString(EXTRA_ID);
            mImage = bundle.getString(EXTRA_IMAGE);
            mMembers = bundle.getStringArrayList(EXTRA_MEMBERS);
        }

        Timber.i("ID = %s", mId);
        Timber.i("Image = %s", mImage);
        Timber.i("Members = %s", mMembers);
    }

    private void initObjects(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter = new GroupChatPresenter(this);
        imageHelper = new ImageHelper(getContext());
        currentUserId = FirebaseAuth.getInstance().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
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
            }

            refreshLayout.setRefreshing(false);
        });

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
//        String messageId = mRootRef.push().getKey();
//        UploadTask uploadTask = mMessageStorage.putBytes(imageByte);
//        imageHelper.uploadThumbnailToStorage(mMessageStorage, uploadTask, image_url -> {
//            HashMap<String, Object> messageDetailsMap = getMessageDetailsMap(image_url,
//                    "image");
//
//            HashMap<String, Object> saveMessageMap = new HashMap<>();
//            for (int i = 0; i < mMembers.size(); i++) {
//                String member = mMembers.get(i);
//                saveMessageMap.put("messages/" + member + "/" + mId + "/" + messageId, messageDetailsMap);
//            }
//
//            mRootRef.updateChildren(saveMessageMap, (databaseError, databaseReference) -> {
//                if (databaseError == null) {
//                    Toast.makeText(getContext(), "Send", Toast.LENGTH_SHORT).show();
//                    updateChatDB("Image");
//                } else {
//                    FirebaseExceptionUtil.databaseError(getContext(),
//                            databaseError.toException());
//                }
//            });
//        });
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
    public void onSendClick(EmojiEditText _editText, ImageButton _button, String message) {
        _button.setEnabled(false);
        button = _button;
        editText = _editText;
        Timber.i("Group Id=%s", mId);
        Timber.i("Current User Id=%s", currentUserId);
        Timber.i("Members=%s", mMembers);
        Timber.i("Message + %s", message);
        presenter.sendGroupMessage(mId, currentUserId, "text", mMembers, message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void sendSuccess() {
        button.setEnabled(true);
        editText.setText("");
    }

    @Override
    public void sendFailed() {

    }

    @Override
    public void onLoadDetails(Groups groups) {
        mMembers = new ArrayList<>(groups.getMembers().keySet());
        mImage = groups.getImage();
    }
}
