package com.suzei.racoon.ui.base

import android.view.View

interface Callback {
    interface ButtonView {
        fun onButtonClick()
    }

    interface RecyclerviewListener<T> {
        fun onItemClick(data: T, view: View)
    }
}