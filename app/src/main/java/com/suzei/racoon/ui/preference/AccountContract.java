package com.suzei.racoon.ui.preference;

import com.suzei.racoon.ui.auth.AuthContract;

public interface AccountContract {

    interface AccountView extends AuthContract.LoginView {

        void setAccountChangeError(String message);

    }

    interface  AccountListener extends AuthContract.onAuthListener {

        void onAccountChangeError(String message);

    }

}
