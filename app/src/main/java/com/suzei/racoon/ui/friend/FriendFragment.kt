package com.suzei.racoon.ui.friend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.suzei.racoon.R
import com.suzei.racoon.databinding.FragmentProfileBinding
import com.suzei.racoon.model.Users
import com.suzei.racoon.ui.auth.login.LoginActivity
import com.suzei.racoon.ui.chatroom.single.SingleChatActivity

class FriendFragment : Fragment(), FriendContract.View {

    lateinit var friendPresenter: FriendContract.Presenter

    private var currentUserId: String? = null
    private var mId: String? = null
    private var mCurrentState: String? = null

    private val onBackClick = View.OnClickListener {
        activity?.finish()
    }

    private val onAddFriendClick = View.OnClickListener {
        friendPresenter.sendRequest(currentUserId, mId, mCurrentState)
    }

    private val onMessageClick = View.OnClickListener {
        val chatIntent = Intent(activity, SingleChatActivity::class.java)
        chatIntent.putExtra(SingleChatActivity.EXTRA_ID, mId)
        startActivity(chatIntent)
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): FriendFragment {
            return FriendFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObjects()
        setupClickListener()
        friendPresenter.setupDetails()
    }

    override fun onStop() {
        super.onStop()
        friendPresenter.destroy(currentUserId, mId)
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            friendPresenter.readCurrentState(currentUserId, mId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setPresenter(presenter: FriendContract.Presenter) {
        this.friendPresenter = presenter
    }

    override fun showDetails(user: Users) {
        mId = user.uid
        binding.profileName.text = user.name
        binding.profileTvBio.text = user.bio
        binding.profileTvAgeGender.text = "${user.age} years old | ${user.gender}"
        Picasso.get().load(user.image).fit().centerCrop().into(binding.profileImage)
    }

    override fun showProgress() {}

    override fun hideProgress() {}

    override fun onLoadSuccess(data: String) {
        when (data) {
            "request_sent" -> {
                mCurrentState = "request_sent"
                binding.profileBtnAddFriend.text = getString(R.string.requested)
            }
            "friend" -> {
                mCurrentState = "friend"
                binding.profileBtnAddFriend.text = getString(R.string.friends)
            }
            "not_friend" -> {
                mCurrentState = "not_friend"
                binding.profileBtnAddFriend.text = getString(R.string.add_friend)
            }
            "request_received" -> {
                mCurrentState = "request_received"
                binding.profileBtnAddFriend.text = getString(R.string.accept)
            }
            else -> throw IllegalArgumentException("Invalid current state = $mCurrentState")
        }
        binding.profileBtnAddFriend.isEnabled = true
    }

    override fun onLoadFailed(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun initObjects() {
        currentUserId = FirebaseAuth.getInstance().uid
    }

    private fun setupClickListener() {
        binding.profileBack.setOnClickListener(onBackClick)
        binding.profileBtnAddFriend.setOnClickListener(onAddFriendClick)
        binding.profileMessage.setOnClickListener(onMessageClick)
    }
}