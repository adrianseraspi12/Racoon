package com.suzei.racoon.ui.profile

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.suzei.racoon.model.Users
import com.suzei.racoon.ui.base.Contract
import com.suzei.racoon.ui.base.FirebaseManager

class ProfileInteractor internal constructor(
    private val profileListener: Contract.Listener<Users>
) : FirebaseManager.FirebaseCallback {

    private val firebaseManager: FirebaseManager = FirebaseManager(this)
    private val usersRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("users")

    fun loadDetails(uid: String) {
        firebaseManager.startValueEventListener(usersRef.child(uid))
    }

    fun destroy(uid: String) {
        firebaseManager.stopValueEventListener(usersRef.child(uid))
    }

    override fun onSuccess(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            val users = dataSnapshot.getValue(Users::class.java)
            profileListener.onLoadSuccess(users!!)
        }
    }

}