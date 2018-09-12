package com.suzei.racoon.ui.notificationlist;

import com.suzei.racoon.ui.base.Contract;

public class NotificationPresenter implements Contract.AdapterListener<NotificationAdapter> {

    private Contract.AdapterView<NotificationAdapter> notificationView;
    private NotificationInteractor notificationInteractor;

    NotificationPresenter(Contract.AdapterView<NotificationAdapter> notificationView) {
        this.notificationView = notificationView;
        notificationInteractor = new NotificationInteractor(this);
        notificationInteractor.performFirebaseDatabaseLoad();
    }

    public void start() {
        notificationInteractor.start();
    }

    public void destroy() {
        notificationInteractor.destroy();
    }

    @Override
    public void onLoadSuccess(NotificationAdapter data) {
        notificationView.setAdapter(data);
    }

    @Override
    public void onLoadFailed() {
        notificationView.loadFailed();
    }

}
