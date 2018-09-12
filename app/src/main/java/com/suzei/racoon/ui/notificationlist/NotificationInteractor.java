package com.suzei.racoon.ui.notificationlist;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.suzei.racoon.model.Notifications;
import com.suzei.racoon.ui.base.Contract;

public class NotificationInteractor {

    private Contract.AdapterListener<NotificationAdapter> notificationListener;

    private NotificationAdapter adapter;
    private DatabaseReference notifRef;

    public NotificationInteractor(Contract.AdapterListener<NotificationAdapter> notificationListener) {
        this.notificationListener = notificationListener;
        String currentUserId = FirebaseAuth.getInstance().getUid();
        notifRef = FirebaseDatabase.getInstance().getReference()
                .child("notifications")
                .child(currentUserId);
    }

    public void performFirebaseDatabaseLoad() {
        Query query = notifRef.orderByChild("timestamp");
        FirebaseRecyclerOptions<Notifications> options = new FirebaseRecyclerOptions
                .Builder<Notifications>().setQuery(query, Notifications.class).build();

        adapter = new NotificationAdapter(options);
        notificationListener.onLoadSuccess(adapter);
    }

    public void start() {
        adapter.startListening();
    }

    public void destroy() {
        adapter.stopListening();
    }

}
