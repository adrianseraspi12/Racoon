package com.suzei.racoon.ui.base;

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

    interface AdapterView<V> extends ProgressView {

        void setAdapter(V adapter);

        void loadFailed();

    }

    interface Listener<V> {

        void onLoadSuccess(V data);

        void onLoadFailed(DatabaseError error);

    }

}
