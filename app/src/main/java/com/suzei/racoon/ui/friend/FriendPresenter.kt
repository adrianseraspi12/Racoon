package com.suzei.racoon.ui.friend

import com.suzei.racoon.model.Users

class FriendPresenter(
    private val mUser: Users?,
    private val mView: FriendContract.View
) : FriendContract.Presenter {
    init {
        mView.setPresenter(this)
    }

    private val friendInteractor: FriendInteractor = FriendInteractor(this)

    override fun sendRequest(currentUserId: String?, friendId: String?, currentState: String?) {
        friendInteractor.executeRequest(currentUserId, friendId, currentState)
    }

    override fun readCurrentState(currentUserId: String?, friendId: String?) {
        friendInteractor.readCurrentState(currentUserId, friendId)
    }

    override fun destroy(currentUserId: String?, friendId: String?) {
        friendInteractor.destroyReading(currentUserId, friendId)
    }

    override fun setupDetails() {
        mUser?.let {
            mView.showDetails(it)
        }
    }

    override fun onLoadSuccess(data: String) {
        mView.onLoadSuccess(data)
    }

    override fun onLoadFailed(message: String) {
        mView.hideProgress()
        mView.onLoadSuccess(message)
    }
}