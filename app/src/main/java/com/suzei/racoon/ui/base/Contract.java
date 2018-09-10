package com.suzei.racoon.ui.base;

import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;

public interface Contract {

    interface ProgressView {

        void showProgress();

        void hideProgress();

    }

    interface DetailsView<V> extends ProgressView {

        void onLoadSuccess(V data);

        void onLoadFailed(DatabaseError error);

    }

    interface AdapterView<V extends RecyclerView.Adapter> extends ProgressView {

        void setAdapter(V adapter);

        void loadFailed();

    }

    interface Listener<V> {

        void onLoadSuccess(V data);

        void onLoadFailed(DatabaseError error);

    }

}
