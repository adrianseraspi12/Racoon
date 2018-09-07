package com.suzei.racoon.util;

import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

public class NumberPickerUtil {

    private static final String TAG = "NumberPickerUtil";

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.d(TAG, "NoSuchFieldException");
                }
                catch(IllegalAccessException e){
                    Log.d(TAG, "IllegalAccessException");
                }
                catch(IllegalArgumentException e){
                    Log.d(TAG, "IllegalArgumentException");
                }
            }
        }
        return false;
    }

}
