package com.suzei.racoon.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.suzei.racoon.databinding.PickUserBinding
import com.suzei.racoon.model.Users
import com.suzei.racoon.ui.add.search.SearchAdapter
import com.suzei.racoon.ui.add.search.SearchContract
import com.suzei.racoon.ui.add.search.SearchPresenter
import com.suzei.racoon.ui.chatroom.single.SingleChatActivity
import java.util.*

class AddSingleChatFragment : Fragment(), SearchContract.SearchView {
    private var searchPresenter: SearchPresenter? = null

    private var _binding: PickUserBinding? = null
    private val binding get() = _binding!!

    private val onBackClick = View.OnClickListener {
        requireActivity().finish()
    }

    private val onSearchClick = View.OnClickListener {
        val users = ArrayList<Users>()
        val query = binding.pickUserSearchText.text.toString().trim { it <= ' ' }
        searchPresenter!!.startSearch(query, users)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = PickUserBinding.inflate(layoutInflater)
        initObjects()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerViews()
        setupClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initObjects() {
        searchPresenter = SearchPresenter(this)
    }

    private fun setUpRecyclerViews() {
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )

        binding.pickUserSearchList.layoutManager = layoutManager
        binding.pickUserSearchList.itemAnimator = DefaultItemAnimator()
        binding.pickUserSearchList.setEmptyView(binding.pickUserNoResult)
    }

    private fun setupClickListener() {
        binding.pickUserBack.setOnClickListener(onBackClick)
        binding.pickUserSearch.setOnClickListener(onSearchClick)
    }

    override fun setSearchAdapter(searchAdapter: SearchAdapter) {
        binding.pickUserSearchList.adapter = searchAdapter
    }

    override fun setSearchUserItemClick(users: Users) {
        val chatRoomIntent = Intent(context, SingleChatActivity::class.java)
        chatRoomIntent.putExtra(SingleChatActivity.EXTRA_ID, users.uid)
        startActivity(chatRoomIntent)
        requireActivity().finish()
    }

    override fun searchSuccess() {}
    override fun searchFailed() {}
    override fun showProgress() {
        binding.pickUserSearch.isEnabled = false
        binding.pickUserLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.pickUserSearch.isEnabled = true
        binding.pickUserLoading.visibility = View.GONE
    }
}