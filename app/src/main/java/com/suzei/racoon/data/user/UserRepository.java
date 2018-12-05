package com.suzei.racoon.data.user;

import java.util.Map;

public interface UserRepository {

    interface OnSaveListener {

        void onSuccess();

        void onFailure(Exception e);

    }

    void saveUserDetails(String uid, Map<String, Object> userMap, OnSaveListener listener);

}
