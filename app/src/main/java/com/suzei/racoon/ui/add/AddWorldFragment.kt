package com.suzei.racoon.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.suzei.racoon.databinding.PickUserBinding
import com.suzei.racoon.model.Users
import com.suzei.racoon.ui.add.create.CreateGroupContract.CreateGroupView
import com.suzei.racoon.ui.add.create.CreateGroupPresenter
import com.suzei.racoon.ui.add.search.*
import com.suzei.racoon.ui.add.search.SelectUserContract.SelectUserView
import com.suzei.racoon.ui.chatroom.group.GroupChatActivity
import timber.log.Timber
import java.util.*

class AddWorldFragment : Fragment(), SearchContract.SearchView, SelectUserView, CreateGroupView {
    private var searchPresenter: SearchPresenter? = null
    private var selectPresenter: SelectPresenter? = null
    private var createGroupPresenter: CreateGroupPresenter? = null
    private val selectedUserList = ArrayList<Users>()

    private var _binding: PickUserBinding? = null
    private val binding get() = _binding!!

    private val onBackClick = View.OnClickListener {
        requireActivity().finish()
    }

    private val onAddClick = View.OnClickListener {
        createGroupPresenter!!.create(selectedUserList)
    }

    private val onSearchClick = View.OnClickListener {
        val query = binding.pickUserSearchText.text.toString()
        searchPresenter!!.startSearch(query, selectedUserList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PickUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerViews()
        setUpPresenters()
        setupClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpRecyclerViews() {
        binding.pickUserSearchList.layoutManager = LinearLayoutManager(context)
        binding.pickUserSelectedList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL, false
        )
        binding.pickUserSearchList.setEmptyView(binding.pickUserNoResult)
    }

    private fun setUpPresenters() {
        searchPresenter = SearchPresenter(this)
        selectPresenter = SelectPresenter(this)
        createGroupPresenter =
            CreateGroupPresenter(context, this)
    }

    private fun setupClickListener() {
        binding.pickUserBack.setOnClickListener(onBackClick)
        binding.pickUserAdd.setOnClickListener(onAddClick)
        binding.pickUserSearch.setOnClickListener(onSearchClick)
    }

    override fun createSuccess(id: String) {
        FirebaseDatabase.getInstance().reference.child("world_chats").child(id).setValue(true)
        val chatRoomIntent = Intent(context, GroupChatActivity::class.java)
        chatRoomIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        chatRoomIntent.putExtra(GroupChatActivity.EXTRA_GROUP_ID, id)
        startActivity(chatRoomIntent)
        requireActivity().finish()
    }

    override fun createFailed() {}
    override fun setSearchAdapter(searchAdapter: SearchAdapter) {
        binding.pickUserSearchList.adapter = searchAdapter
    }

    override fun setSearchUserItemClick(users: Users) {
        selectedUserList.add(users)
        selectPresenter!!.addSelectedUser(users)
        searchPresenter!!.removeFromSearch(users)
        binding.pickUserSelectedList.scrollToPosition(selectPresenter!!.itemCount - 1)
        binding.pickUserSelectedListLayout.visibility = View.VISIBLE
    }

    override fun searchSuccess() {}
    override fun searchFailed() {
        Timber.i("Failed")
    }

    override fun showProgress() {
        binding.pickUserSearch.isEnabled = false
        binding.pickUserLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.pickUserSearch.isEnabled = true
        binding.pickUserLoading.visibility = View.GONE
    }

    override fun setSelectUserAdapter(selectUserAdapter: SelectedAdapter) {
        binding.pickUserSelectedList.adapter = selectUserAdapter
    }

    override fun setSelectUserItemClick(users: Users) {
        selectedUserList.remove(users)
        selectPresenter!!.removeSelectedUser(users)
        searchPresenter!!.addFromSearch(users)
        if (selectedUserList.size == 0) {
            binding.pickUserSelectedListLayout.visibility = View.GONE
        }
    }

    override fun selectUserFailed() {
        Timber.i("Failed")
    }
}