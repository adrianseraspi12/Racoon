package com.suzei.racoon.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;
import timber.log.Timber;

public class ImageHelper {

    private Context mContext;

    public ImageHelper(Context mContext) {
        this.mContext = mContext;
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

    }

    public File getRealPathFromUri(Uri uri) {
        String result;
        Cursor cursor = mContext.getContentResolver().query(uri, null, null,
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

    public Bitmap compressedImage(File thumbnail) {
        try {
            return new Compressor(mContext)
                    .setMaxWidth(150)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumbnail);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    public byte[] thumbToByteArray(Bitmap thumb_bitmap) {
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
                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                Timber.e(error);
            }

        });


    }

    public interface FirebaseCallback {
        void afterUploading(String image_url);
    }

}
