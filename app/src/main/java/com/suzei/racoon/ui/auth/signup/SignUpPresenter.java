package com.suzei.racoon.ui.auth.signup;

import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.suzei.racoon.data.auth.AuthRepository;
import com.suzei.racoon.data.user.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class SignUpPresenter implements SignUpContract.Presenter {

    private static final String DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/racoon-89ee8.appspot.com/o/emoji%2Fhappy_512px.png?alt=media&amp;token=11c342c5-1896-4e04-a822-c76536cf5d1c";

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private SignUpContract.View mView;

    SignUpPresenter(AuthRepository authRepository,
                    UserRepository userRepository,
                    SignUpContract.View mView) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void createAccount(String email, String password, String name) {

        if (!hasError(email, password, name)) {
            mView.showProgress();
            authRepository.executeRegister(email, password, new AuthRepository.Listener() {

                @Override
                public void onAuthSuccess(String uid) {
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    Map<String, Object> detailsMap = new HashMap();
                    detailsMap.put("age", 18);
                    detailsMap.put("bio", "");
                    detailsMap.put("gender", "Unknown");
                    detailsMap.put("image", DEFAULT_IMAGE);
                    detailsMap.put("name", name);
                    detailsMap.put("online", true);
                    detailsMap.put("device_token", deviceToken);

                    saveUserDetails(uid, detailsMap);
                }

                @Override
                public void onAuthFailure(Exception e) {

                }

            });
        }
    }

    private void saveUserDetails(String uid,
                                 Map<String, Object> detailsMap) {
        userRepository.saveUserDetails(uid, detailsMap, new UserRepository.OnSaveListener() {

            @Override
            public void onSuccess() {
                mView.hideProgress();
                mView.onRegisterSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                mView.hideProgress();
                mView.onRegisterFailure("Can't create user details, Please login");
            }

        });
    }

    private boolean hasError(String email, String password, String name) {
        if (TextUtils.isEmpty(email)) {
            mView.onRegisterFailure("Email is required");
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            mView.onRegisterFailure("Password is required");
            return true;
        }

        if (TextUtils.isEmpty(name)) {
            mView.onRegisterFailure("Display name is required");
            return true;
        }

        return false;
    }

}