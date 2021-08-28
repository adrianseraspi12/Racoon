package com.suzei.racoon.ui.base

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.aurelhubert.ahbottomnavigation.notification.AHNotification
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.suzei.racoon.R
import com.suzei.racoon.databinding.ActivityMainBinding
import com.suzei.racoon.ui.Racoon
import com.suzei.racoon.ui.Racoon.AppCallback
import com.suzei.racoon.ui.add.AddActivity
import com.suzei.racoon.ui.auth.login.LoginActivity
import com.suzei.racoon.ui.base.Callback.ButtonView
import com.suzei.racoon.ui.chatlist.ChatFragment
import com.suzei.racoon.ui.friendlist.FriendsFragment
import com.suzei.racoon.ui.notificationlist.NotificationFragment
import com.suzei.racoon.ui.preference.SettingsActivity
import com.suzei.racoon.ui.profile.ProfileFragment
import com.suzei.racoon.ui.worldlist.WorldFragment
import com.suzei.racoon.util.OnlineStatus

class MainActivity : AppCompatActivity(), AHBottomNavigation.OnTabSelectedListener {
    private var mNotifCountRef: DatabaseReference? = null
    private var notification: AHNotification.Builder? = null
    private var interstitialAd: InterstitialAd? = null
    private var mAuth: FirebaseAuth? = null
    private var mListener: ButtonView? = null

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val onSettingsClick = View.OnClickListener {
        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
    }

    private val onPrimaryFabClick = View.OnClickListener {
        if (mListener != null) {
            mListener!!.onButtonClick()
        }
    }

    private val onSecondaryFabClick = View.OnClickListener {
        val addActIntent = Intent(this@MainActivity, AddActivity::class.java)
        addActIntent.putExtra(AddActivity.EXTRA_FRAGMENT_TYPE, AddActivity.Add.GROUP_CHAT)
        startActivity(addActIntent)
    }

    private val onSignOutClick = View.OnClickListener {
        if (interstitialAd!!.isLoaded) {
            interstitialAd!!.show()
            interstitialAd!!.adListener = object : AdListener() {
                override fun onAdClosed() {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        } else {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObjects()
        initBottomNavNotification()
        setUpAds()
        setUpSnackbarApp()
        setUpBottomNavigation()
        setupClickListeners()
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
        if (mAuth!!.currentUser != null) {
            mNotifCountRef!!.child(mAuth!!.uid!!).child("alerts")
                .addValueEventListener(initEventListeners(3))
            mNotifCountRef!!.child(mAuth!!.uid!!).child("chats")
                .addValueEventListener(initEventListeners(1))
        } else {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAuth!!.currentUser != null) {
            mNotifCountRef!!.child(mAuth!!.uid!!).child("alerts")
                .removeEventListener(initEventListeners(3))
            mNotifCountRef!!.child(mAuth!!.uid!!).child("chats")
                .removeEventListener(initEventListeners(1))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
        val drawableWorld = resources.getDrawable(R.drawable.world, applicationContext.theme)
        val drawableAddSingleChat =
            resources.getDrawable(R.drawable.add_single_chat, applicationContext.theme)
        val drawableAddGroupChat =
            resources.getDrawable(R.drawable.add_group_chat, applicationContext.theme)
        val drawableAddFriend =
            resources.getDrawable(R.drawable.add_friend, applicationContext.theme)

        when (position) {
            0 -> {
                showShadow()
                configFab(drawableWorld)
                showFragment(WorldFragment())
                binding.mainSecondaryFab.hide()
                binding.mainBannerAd.visibility = View.VISIBLE
            }
            1 -> {
                showShadow()
                configFab(drawableAddSingleChat)
                showFragment(ChatFragment())
                binding.mainSecondaryFab.show()
                binding.mainSecondaryFab.setImageDrawable(drawableAddGroupChat)
                binding.mainBannerAd.visibility = View.VISIBLE
            }
            2 -> {
                showShadow()
                configFab(drawableAddFriend)
                showFragment(FriendsFragment())
                binding.mainSecondaryFab.hide()
                binding.mainBannerAd.visibility = View.VISIBLE
            }
            3 -> {
                showShadow()
                configFab(null)
                showFragment(NotificationFragment())
                binding.mainSecondaryFab.hide()
                binding.mainBannerAd.visibility = View.VISIBLE
            }
            4 -> {
                hideShadow()
                configFab(null)
                showFragment(ProfileFragment())
                binding.mainSecondaryFab.hide()
                binding.mainBannerAd.visibility = View.GONE
            }
            else -> throw IllegalArgumentException("Invalid menu item =$position")
        }
        return true
    }

    private fun initObjects() {
        mAuth = FirebaseAuth.getInstance()
        mNotifCountRef = FirebaseDatabase.getInstance().reference.child("notification_count")
    }

    private fun initBottomNavNotification() {
        notification = AHNotification.Builder()
            .setBackgroundColor(Color.RED)
            .setTextColor(Color.WHITE)
    }

    private fun setUpAds() {
        val adRequest = AdRequest.Builder().build()
        binding.mainBannerAd.loadAd(adRequest)
        interstitialAd = InterstitialAd(this)
        interstitialAd!!.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        interstitialAd!!.loadAd(AdRequest.Builder().build())
    }

    private fun setupClickListeners() {
        binding.mainSettings.setOnClickListener(onSettingsClick)
        binding.mainPrimaryFab.setOnClickListener(onPrimaryFabClick)
        binding.mainSecondaryFab.setOnClickListener(onSecondaryFabClick)
        binding.mainLogout.setOnClickListener(onSignOutClick)
    }

    private fun setUpSnackbarApp() {
        val racoon = application as Racoon
        racoon.setAppCallback(object : AppCallback {
            override fun onConnected() {
                showSnackbar("Connected", Snackbar.LENGTH_SHORT)
            }

            override fun onDisconnected() {
                showSnackbar("Not connected", Snackbar.LENGTH_LONG)
            }
        })
    }

    private fun setUpBottomNavigation() {
        val drawableWorld = resources.getDrawable(R.drawable.world, applicationContext.theme)
        val navigationAdapter = AHBottomNavigationAdapter(
            this,
            R.menu.bottom_navigation
        )
        navigationAdapter.setupWithBottomNavigation(binding.mainBottomNavigation)
        binding.mainBottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        binding.mainBottomNavigation.setNotificationBackgroundColor(Color.parseColor("#a1887f"))
        binding.mainBottomNavigation.defaultBackgroundColor = Color.WHITE
        binding.mainBottomNavigation.accentColor = Color.parseColor("#306173")
        binding.mainBottomNavigation.setOnTabSelectedListener(this)
        binding.mainPrimaryFab.setImageDrawable(drawableWorld)
        binding.mainSecondaryFab.hide()
        binding.mainBottomNavigation.currentItem = 0
    }

    private fun initEventListeners(pos: Int): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("count")) {
                    val count = dataSnapshot.child("count").getValue(Int::class.java)!!
                    addBadge(count, pos)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
    }

    fun setOnButtonClickListener(listener: ButtonView?) {
        mListener = listener
    }

    private fun addBadge(count: Int, pos: Int) {
        if (count <= 0) {
            //remove notification badge
            binding.mainBottomNavigation.setNotification("", pos)
            return
        }
        notification!!.setText(count.toString())
        binding.mainBottomNavigation.setNotification(notification!!.build(), pos)
    }

    private fun showShadow() {
        binding.mainToolbarLayout.elevation = 4f
    }

    private fun hideShadow() {
        binding.mainToolbarLayout.elevation = 0f
    }

    private fun configFab(drawable: Drawable?) {
        if (drawable == null) {
            binding.mainPrimaryFab.hide()
            return
        }
        binding.mainPrimaryFab.setImageDrawable(drawable)
        binding.mainPrimaryFab.show()
    }

    private fun showFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(binding.mainFragmentContainer.id, fragment)
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        ft.commit()
    }

    private fun showSnackbar(text: String, duration: Int) {
        val snackbar = Snackbar.make(binding.mainRoot, text, duration)
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP

        // calculate actionbar height
        val tv = TypedValue()
        var actionBarHeight = 0
        if (theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                tv.data, resources
                    .displayMetrics
            )
        }
        params.setMargins(16, actionBarHeight + 16, 16, 0)
        view.layoutParams = params
        snackbar.show()
    }
}