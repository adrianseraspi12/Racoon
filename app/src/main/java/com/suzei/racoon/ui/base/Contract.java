package com.suzei.racoon.ui.base;

import androidx.recyclerview.widget.RecyclerView;

public interface Contract {

    interface ProgressView {

        void showProgress();

        void hideProgress();

    }

    interface DetailsView<M> extends ProgressView {

        void onLoadSuccess(M data);

        void onLoadFailed(String message);

    }

    interface AdapterView<A extends RecyclerView.Adapter> extends ProgressView {

        void setAdapter(A adapter);

        void loadFailed();

    }

    interface Listener<M> {

        void onLoadSuccess(M data);

        void onLoadFailed(String message);

    }

    interface AdapterListener<A extends RecyclerView.Adapter> {

        void onLoadSuccess(A data);

        void onLoadFailed();

    }

}
