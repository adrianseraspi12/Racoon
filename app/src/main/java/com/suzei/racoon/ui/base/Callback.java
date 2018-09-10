package com.suzei.racoon.ui.base;

import android.view.View;

public interface Callback {

    interface ButtonView {

        void onButtonClick();

    }

    interface RecyclerviewListener<T> {

        void onItemClick(T data, View view);

    }

}
