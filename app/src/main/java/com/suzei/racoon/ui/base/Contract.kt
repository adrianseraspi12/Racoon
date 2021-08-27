package com.suzei.racoon.ui.base

import androidx.recyclerview.widget.RecyclerView

interface Contract {
    interface ProgressView {
        fun showProgress()
        fun hideProgress()
    }

    interface DetailsView<M> : ProgressView {
        fun onLoadSuccess(data: M)
        fun onLoadFailed(message: String)
    }

    interface AdapterView<A : RecyclerView.Adapter<*>> : ProgressView {
        fun setAdapter(adapter: A)
        fun loadFailed()
    }

    interface Listener<M> {
        fun onLoadSuccess(data: M)
        fun onLoadFailed(message: String)
    }

    interface AdapterListener<A : RecyclerView.Adapter<*>> {
        fun onLoadSuccess(data: A)
        fun onLoadFailed()
    }
}