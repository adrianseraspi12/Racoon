package com.suzei.racoon.ui.friend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suzei.racoon.R
import com.suzei.racoon.model.Users
import com.suzei.racoon.util.OnlineStatus

class FriendActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PROFILE_DETAILS = "profile_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val users = intent.getParcelableExtra<Users>(EXTRA_PROFILE_DETAILS)

        val friendFragment = FriendFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.base_root_container, friendFragment)
            .commit()

        FriendPresenter(
            users,
            friendFragment
        )
    }

    override fun onPause() {
        super.onPause()
        OnlineStatus.set(false)
    }

    override fun onPostResume() {
        super.onPostResume()
        OnlineStatus.set(true)
    }
}