package com.suzei.racoon.ui.profile

import com.suzei.racoon.model.Users
import com.suzei.racoon.ui.base.Contract
import com.suzei.racoon.ui.base.Contract.DetailsView

class ProfilePresenter(
    private val profileView: DetailsView<Users>
) : Contract.Listener<Users> {
    private val profileInteractor: ProfileInteractor = ProfileInteractor(this)

    fun showUserDetails(uid: String) {
        profileView.showProgress()
        profileInteractor.loadDetails(uid)
    }

    fun destroy(uid: String) {
        profileInteractor.destroy(uid)
    }

    override fun onLoadSuccess(data: Users) {
        profileView.hideProgress()
        profileView.onLoadSuccess(data)
    }

    override fun onLoadFailed(message: String) {
        profileView.hideProgress()
        profileView.onLoadFailed(message)
    }

}