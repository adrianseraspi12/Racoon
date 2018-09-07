package com.suzei.racoon.callback;

import android.widget.ImageButton;

import com.vanniktech.emoji.EmojiEditText;

public interface ChatListener {

    // show the bottom dialog that contains CAMERA, GALLERY
    void onCameraClick();

    // save the message to database
    void onSendClick(EmojiEditText editText, ImageButton button, String message);

}
