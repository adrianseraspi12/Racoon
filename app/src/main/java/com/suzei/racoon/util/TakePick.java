package com.suzei.racoon.util;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.suzei.racoon.R;
import com.suzei.racoon.adapter.DialogEmojiAdapter;
import com.suzei.racoon.model.Emoji;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class TakePick {

    private static final int PICK_CAMERA = 10;
    private static final int PICK_GALLERY = 11;

    private DatabaseReference mEmojiRef;
    private LifecycleOwner owner;

    private ImageListener imageListener;

    private Activity activity;
    private Uri mImageFromCameraUri;

    public TakePick(Activity activity, LifecycleOwner owner, ImageListener listener) {
        this.activity = activity;
        this.owner = owner;
        this.imageListener = listener;
        initObjects();
    }

    private void initObjects() {
        mEmojiRef = FirebaseDatabase.getInstance().getReference().child("emoji");
    }

    public void showPicker() {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(activity);
        View bottomView = activity.getLayoutInflater().inflate(R.layout.dialog_image_picker, null);
        LinearLayout pickCamera = bottomView.findViewById(R.id.dialog_image_picker_camera);
        LinearLayout pickGallery = bottomView.findViewById(R.id.dialog_image_picker_gallery);
        LinearLayout pickEmoji = bottomView.findViewById(R.id.dialog_image_picker_emoji);

        bottomDialog.setContentView(bottomView);
        bottomDialog.show();

        pickCamera.setOnClickListener(v -> {
            Timber.i("camera pick");

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photo = null;

            try {
                photo = createImageFile();
                photo.delete();
            } catch (IOException e) {
                Timber.e(e);
                e.printStackTrace();
                Toast.makeText(activity, "Please check your SD card",
                        Toast.LENGTH_SHORT).show();
            }

            mImageFromCameraUri = Uri.fromFile(photo);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFromCameraUri);
            bottomDialog.dismiss();
            activity.startActivityForResult(cameraIntent, PICK_CAMERA);
        });

        pickGallery.setOnClickListener(v -> {
            Timber.i("gallery pick");
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
            activity.startActivityForResult(chooserIntent, PICK_GALLERY);
            bottomDialog.dismiss();
        });

        pickEmoji.setOnClickListener(v -> {
            Timber.i("emoji pick");
            bottomDialog.dismiss();

            View emojiView = View.inflate(activity, R.layout.dialog_emoji_picker, null);
            RecyclerView listEmojiView = emojiView.findViewById(R.id.dialog_emoji_list);
            listEmojiView.setLayoutManager(new LinearLayoutManager(activity));

            AlertDialog emojiDialog = new AlertDialog.Builder(activity).create();
            emojiDialog.setTitle("Select Emoji");
            emojiDialog.setView(emojiView);
            emojiDialog.setCancelable(true);

            FirebaseRecyclerOptions<Emoji> options = new FirebaseRecyclerOptions.Builder<Emoji>()
                    .setQuery(mEmojiRef.orderByChild("name"), Emoji.class).setLifecycleOwner(owner)
                    .build();

            DialogEmojiAdapter emojiAdapter = new DialogEmojiAdapter(options, image -> {
                imageListener.onEmojiPick(image);
                emojiDialog.dismiss();
            });

            listEmojiView.setAdapter(emojiAdapter);

            emojiDialog.show();
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        byte[] imageByte;
        switch (requestCode) {
            case PICK_CAMERA:
                if (resultCode == RESULT_OK) {
                    File thumbCameraFilePath = new File(mImageFromCameraUri.getPath());
                    Bitmap compressedCameraBitmap = compressedImage(thumbCameraFilePath);
                    imageByte = thumbToByteArray(compressedCameraBitmap);
                    imageListener.onCameraGalleryPick(imageByte);
                }
                break;

            case PICK_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri galleryUri = data.getData();
                    File galleryFile = getRealPathFromUri(galleryUri);
                    Bitmap compressedGalleryBitmap = compressedImage(galleryFile);
                    imageByte = thumbToByteArray(compressedGalleryBitmap);
                    imageListener.onCameraGalleryPick(imageByte);
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid request code= " + requestCode);
        }


    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

    }

    private File getRealPathFromUri(Uri uri) {
        String result;
        Cursor cursor = activity.getContentResolver().query(uri, null, null,
                null, null);

        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(dataColumnIndex);
            cursor.close();
        }

        return new File(result);
    }

    private Bitmap compressedImage(File thumbnail) {
        try {
            return new Compressor(activity)
                    .setMaxWidth(150)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumbnail);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    private byte[] thumbToByteArray(Bitmap thumb_bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public void uploadThumbnailToStorage(final StorageReference storageReference, UploadTask uploadTask,
                                         final FirebaseCallback firebaseCallback) {
        uploadTask.addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                storageReference.getDownloadUrl().addOnCompleteListener(task1 ->
                        firebaseCallback.afterUploading(task1.getResult().toString()));
            } else {
                String error = task.getException().getMessage();
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                Timber.e(error);
            }

        });

    }

    public interface FirebaseCallback {
        void afterUploading(String image_url);
    }

    public interface ImageListener {

        void onEmojiPick(String image);

        void onCameraGalleryPick(byte[] imageByte);

    }

}
