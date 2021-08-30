package com.suzei.racoon.ui.add

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.suzei.racoon.databinding.ActivityCreatorBinding
import com.suzei.racoon.ui.auth.login.LoginActivity
import com.suzei.racoon.util.OnlineStatus

class AddActivity : AppCompatActivity() {
    private var _binding: ActivityCreatorBinding? = null
    private val binding get() = _binding!!

    object Add {
        const val WORLD = 0
        const val SINGLE_CHAT = 1
        const val GROUP_CHAT = 2
        const val FRIENDS = 3
    }

    companion object {
        const val EXTRA_FRAGMENT_TYPE = "fragment_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showFragment()
        setUpBannerAd()
    }

    override fun onPause() {
        super.onPause()
        OnlineStatus.set(false)
    }

    override fun onPostResume() {
        super.onPostResume()
        OnlineStatus.set(true)
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this@AddActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showFragment() {
        val fragmentType = intent.getIntExtra(EXTRA_FRAGMENT_TYPE, -1)
        val fragment: Fragment = when (fragmentType) {
            Add.WORLD -> AddWorldFragment()
            Add.SINGLE_CHAT -> AddSingleChatFragment()
            Add.GROUP_CHAT -> AddGroupChatFragment()
            Add.FRIENDS -> AddFriendFragment()
            else -> throw IllegalArgumentException("Invalid fragment type=$fragmentType")
        }
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(binding.creatorFragmentContainer.id, fragment)
        ft.commit()
    }

    private fun setUpBannerAd() {
        val adRequest = AdRequest.Builder().build()
        binding.creatorBannerAd.loadAd(adRequest)
    }
}