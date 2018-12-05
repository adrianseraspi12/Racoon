package com.suzei.racoon.util;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

public class DialogEditor {

    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;
    public static final int GENDER_UNKNOWN = 2;

    public static void genderPick(Context context, int gender, DialogEditorCallback callback) {
        CharSequence[] genderSeq = {"Male", "Female", "Unknown"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose gender:");
        builder.setSingleChoiceItems(genderSeq, gender, null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Ok", (dialog, which) -> {

            ListView listView = ((AlertDialog) dialog).getListView();
            Object checkedItem = listView.getAdapter().getItem(listView.getCheckedItemPosition());
            callback.onResult(checkedItem);
            dialog.dismiss();

        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void agePick(Context context, int defValue, DialogEditorCallback callback) {
        NumberPicker numberPicker = new NumberPicker(context);
//        NumberPick.setNumberPickerTextColor(numberPicker, colorBlack);
        numberPicker.setMaxValue(115);
        numberPicker.setMinValue(13);
        numberPicker.setValue(defValue);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Choose an age:");
        builder.setView(numberPicker);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            int selectedAge = numberPicker.getValue();
            callback.onResult(selectedAge);
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void inputDialog(Context context,
                                   String title, String defText,
                                   DialogEditorCallback callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        EditText editText = new EditText(context);
        editText.setText(defText);
//        editText.setTextColor(colorBlack);
        builder.setView(editText);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String inputtedText = editText.getText().toString().trim();
            callback.onResult(inputtedText);
            dialog.dismiss();

        });

        builder.show();
    }

    public interface DialogEditorCallback {

        void onResult(Object data);

    }
}
