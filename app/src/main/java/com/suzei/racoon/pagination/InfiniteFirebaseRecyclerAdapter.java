package com.suzei.racoon.pagination;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import timber.log.Timber;

public abstract class InfiniteFirebaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private static final String TAG = InfiniteFirebaseRecyclerAdapter.class.getSimpleName();

    Class<T> mModelClass;
    InfiniteFirebaseArray mSnapshots;
    private Query mQuery;

    InfiniteFirebaseRecyclerAdapter(Class<T> modelClass, InfiniteFirebaseArray snapshots) {
        mModelClass = modelClass;
        mSnapshots = snapshots;
        mSnapshots.setOnChangedListener(new InfiniteFirebaseArray.OnChangedListener() {

            @Override
            public void onChanged(@InfiniteFirebaseArray.EventType int type, int index, int oldIndex) {
                Timber.d("EventType: %s", type);
                Timber.d("Index: %s", index);
                switch (type) {
                    case InfiniteFirebaseArray.ADDED:
                        notifyItemInserted(index);
                        break;
                    case InfiniteFirebaseArray.CHANGED:
                        notifyItemChanged(index);
                        break;
                    case InfiniteFirebaseArray.REMOVED:
                        notifyItemRemoved(index);
                        break;
                    default:
                        throw new IllegalStateException("Incomplete case statement");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                InfiniteFirebaseRecyclerAdapter.this.onCancelled(databaseError);
            }
        });
    }

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public InfiniteFirebaseRecyclerAdapter(Class<T> modelClass,
                                           Query ref, int itemsPerPage) {
        this(modelClass, new InfiniteFirebaseArray(ref,itemsPerPage));
        mQuery = ref;
    }

    public void cleanup() {
        mSnapshots.cleanup();
    }

    public void more() {
        if (mSnapshots != null) {
            mSnapshots.more(mQuery);
        }
    }

    @Override
    public int getItemCount() {
        return mSnapshots.getCount();
    }

    public T getItem(int position) {
        return parseSnapshot(mSnapshots.getItem(position));
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected T parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(mModelClass);
    }

    public DatabaseReference getRef(int position) {
        return mSnapshots.getItem(position).getRef();
    }

    @Override
    public long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(position).getKey().hashCode();
    }

//    @Override
//    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
//        LoginView view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
//        try {
//            Constructor<VH> constructor = mViewHolderClass.getConstructor(LoginView.class);
//            return constructor.newInstance(view);
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int position) {
        T model = getItem(position);
        onBindViewHolder(viewHolder, model, position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * This method will be triggered in the event that this listener either failed at the server,
     * or is removed as a result of the security and Firebase Database rules.
     *
     * @param error A description of the error that occurred
     */
    protected void onCancelled(DatabaseError error) {
        Log.w(TAG, error.toException());
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     * @param model      The object containing the data used to populate the view
     * @param position   The position in the list of the view being populated
     */
//    abstract protected void populateViewHolder(VH viewHolder, T model, int position);
    protected abstract void onBindViewHolder(@NonNull VH holder, @NonNull T model, int position);
}
