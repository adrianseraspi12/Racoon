package com.suzei.racoon.pagination;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class InfiniteFirebaseArray implements ChildEventListener {

    private int mCurrentPage = 1;
    private String mPrevKey;
    private String mLastKey;

    public static final int ADDED = 0;
    public static final int CHANGED = 1;
    public static final int REMOVED = 2;

    @IntDef({ADDED, CHANGED, REMOVED})
    @Retention(RetentionPolicy.SOURCE)
    @interface EventType {
    }

    public interface OnChangedListener {

        void onChanged(@EventType int type, int index, int oldIndex);

        void onCancelled(DatabaseError databaseError);
    }

    private Query mQuery;
    private List<DataSnapshot> mSnapshots = new ArrayList<>();
    private int mNumberPerPage;
    private int itemPos = 0;
    private boolean isDuplicateKey;
    private boolean isFirstLoad = true;
    private OnChangedListener mListener;

    public InfiniteFirebaseArray(Query ref, int numberPerPage) {
        mNumberPerPage = numberPerPage;
        initQuery(ref);
    }

    private void initQuery(Query ref) {
        Timber.d("NumberPerPage: %s", mNumberPerPage);
        mQuery = ref.limitToLast(mNumberPerPage * mCurrentPage);
        Timber.d("Page (Current)= %s", mCurrentPage);
        mQuery.addChildEventListener(this);
    }

    private void initNextQuery(Query ref) {
        itemPos = 0;
        isFirstLoad = false;
        Timber.d("NumberPerPage: %s", mNumberPerPage);
        Timber.d("IFA (Current)= %s", mCurrentPage);

        ref.orderByKey().limitToLast(mNumberPerPage).endAt(mLastKey);
        ref.addChildEventListener(this);
    }

    public void cleanup() {
        mQuery.removeEventListener(this);
    }

    public void more(Query ref) {
        if (isHasMore()) {
            initNextQuery(ref);
        }
    }

    public int getCount() {
        return mSnapshots.size();
    }

    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildKey) {
        if (snapshot == null) {
            return;
        }

        if (isFirstLoad) {

            itemPos++;

            if (checkDuplicateKey(snapshot.getKey())) {
                isDuplicateKey = true;
                return;
            }

            if (itemPos == 1) {
                String messageKey = snapshot.getKey();

                mLastKey = messageKey;
                mPrevKey = messageKey;

            }

            mSnapshots.add(snapshot);
            Timber.d("IFA (itemPos:LastKey)= " + itemPos + " : " + mLastKey);

        } else {

            String messageKey = snapshot.getKey();

            if (checkDuplicateKey(messageKey)) {
                isDuplicateKey = true;
                return;
            }

            if (!mPrevKey.equals(messageKey)) {
                mSnapshots.add(itemPos++, snapshot);
            } else {

                mPrevKey = mLastKey;

            }

            if (itemPos == 1) {
                mLastKey = messageKey;
            }
            Timber.d("IFA (itemPos:LastKey)= " + itemPos + " : " + mLastKey);
        }

        notifyChangedListeners(ADDED, itemPos);
    }

    private boolean checkDuplicateKey(String nextChildKey) {
        if (mSnapshots.size() > 0) {

            for (int i = 0; i < mSnapshots.toArray().length; i++) {
                DataSnapshot ds = mSnapshots.get(i);
                String key = ds.getKey();
                if (key.equals(nextChildKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHasMore() {
        boolean isHasMore = true;
        if (isDuplicateKey) {
            isHasMore = false;
        }
        Timber.d("isHasMore: %s", isHasMore);
        return isHasMore;
    }

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }

    private void notifyChangedListeners(@EventType int type, int index) {
        notifyChangedListeners(type, index, -1);
    }

    protected void notifyChangedListeners(@EventType int type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }

    protected void notifyCancelledListeners(DatabaseError databaseError) {
        if (mListener != null) {
            mListener.onCancelled(databaseError);
        }
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {
        int index = getIndexForKey(snapshot.getKey());
        if (index != -1) {
            mSnapshots.set(index, snapshot);
            notifyChangedListeners(CHANGED, index);
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
        int index = getIndexForKey(snapshot.getKey());
        if (index != -1) {
            mSnapshots.remove(index);
            notifyChangedListeners(REMOVED, index);
        }
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildKey) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        notifyCancelledListeners(databaseError);
    }
}
