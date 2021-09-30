package com.suzei.racoon.ui.friend

import com.suzei.racoon.model.Users
import com.suzei.racoon.ui.base.Contract

interface FriendContract {

    interface Presenter: Contract.Listener<String> {
        fun setupDetails()
        fun sendRequest(currentUserId: String?, friendId: String?, currentState: String?)
        fun readCurrentState(currentUserId: String?, friendId: String?)
        fun destroy(currentUserId: String?, friendId: String?)
    }

    interface View: Contract.DetailsView<String> {
        fun setPresenter(presenter: Presenter)
        fun showDetails(user: Users)
    }

}