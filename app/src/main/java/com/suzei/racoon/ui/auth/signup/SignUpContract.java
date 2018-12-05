package com.suzei.racoon.ui.auth.signup;

import com.suzei.racoon.ui.base.Contract;

public interface SignUpContract {

    interface View extends Contract.ProgressView {

        void setPresenter(Presenter presenter);

        void onRegisterSuccess();

        void onRegisterFailure(String message);

    }


    interface Presenter {

        void createAccount(String email, String password, String name);

    }

}
