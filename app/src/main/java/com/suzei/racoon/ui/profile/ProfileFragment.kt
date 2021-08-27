package com.suzei.racoon.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.suzei.racoon.R
import com.suzei.racoon.databinding.ProfileBinding
import com.suzei.racoon.model.Users
import com.suzei.racoon.ui.base.Contract.DetailsView
import com.suzei.racoon.util.DialogEditor
import com.suzei.racoon.util.TakePicture
import com.suzei.racoon.util.TakePicture.ImageListener
import timber.log.Timber

class ProfileFragment : Fragment(), DetailsView<Users> {

    private var profilePresenter: ProfilePresenter? = null
    private var mUserRef: DatabaseReference? = null
    private var mUserStorage: StorageReference? = null
    private var takePicture: TakePicture? = null
    private var currentUserId: String? = null

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObjects()
        setUpPresenter()
        hideViews()
        setUpTakePick()
        setupClickListener()
    }

    override fun onStart() {
        super.onStart()
        profilePresenter!!.showUserDetails(currentUserId!!)
    }

    override fun onStop() {
        super.onStop()
        profilePresenter!!.destroy(currentUserId!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObjects() {
        currentUserId = FirebaseAuth.getInstance().uid
        mUserRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserId!!)
        mUserStorage = FirebaseStorage.getInstance().reference.child("users").child("pictures")
            .child(currentUserId!!)
    }

    private fun setUpPresenter() {
        profilePresenter = ProfilePresenter(this)
    }

    private fun hideViews() {
        binding.profileShadow.visibility = View.GONE
        binding.profileBack.visibility = View.GONE
        binding.profileActionButtonsLayout.visibility = View.GONE
    }

    private fun setUpTakePick() {
        takePicture = TakePicture(activity, this, object : ImageListener {
            override fun onEmojiPick(image: String) {
                mUserRef!!.child("image").setValue(image)
            }

            override fun onCameraGalleryPick(imageByte: ByteArray) {
                val uploadTask = mUserStorage!!.putBytes(imageByte)
                takePicture!!.uploadThumbnailToStorage(
                    mUserStorage,
                    uploadTask
                ) { image_url: String? -> mUserRef!!.child("image").setValue(image_url) }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.i("onActivityResult")
        takePicture!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupClickListener() {
        binding.profileImage.setOnClickListener(onImageClick)
        binding.profileGender.setOnClickListener(onGenderClick)
        binding.profileAge.setOnClickListener(onAgeClick)
        binding.profileName.setOnClickListener(onTextFieldClick(binding.profileName))
        binding.profileDescription.setOnClickListener(onTextFieldClick(binding.profileDescription))
    }

    override fun showProgress() {
        //Show loading animation
    }

    override fun hideProgress() {
        //hide loading animation
    }

    override fun onLoadSuccess(data: Users) {
        binding.profileName.text = data.name
        binding.profileAge.text = data.age.toString()
        Picasso.get().load(data.image).fit().centerCrop().into(binding.profileImage)
        binding.profileGender.text = data.gender

        val resources = requireContext().resources
        val drawableMale = resources.getDrawable(R.drawable.gender_male, requireContext().theme)
        val drawableFemale =
            resources.getDrawable(R.drawable.gender_female, requireContext().theme)
        val drawableUnknown =
            resources.getDrawable(R.drawable.gender_unknown, requireContext().theme)

        when (data.gender) {
            "Male" -> binding.profileGender.setCompoundDrawablesWithIntrinsicBounds(
                drawableMale, null,
                null, null
            )
            "Female" -> binding.profileGender.setCompoundDrawablesWithIntrinsicBounds(
                drawableFemale, null,
                null, null
            )
            "Unknown" -> binding.profileGender.setCompoundDrawablesWithIntrinsicBounds(
                drawableUnknown, null,
                null, null
            )
        }
    }

    override fun onLoadFailed(message: String) {}

    // OnClick Listeners

    private val onAgeClick = View.OnClickListener {
        val age = binding.profileAge.text.toString().toInt()
        DialogEditor.agePick(context, age) { data: Any? -> mUserRef!!.child("age").setValue(data) }
    }

    private val onImageClick = View.OnClickListener {
        Timber.i("Take Pick")
        takePicture!!.showPicker()
    }

    private val onGenderClick = View.OnClickListener {
        val gender = binding.profileGender.text.toString()
        val genderNum = when (gender) {
            "Male" -> DialogEditor.GENDER_MALE
            "Female" -> DialogEditor.GENDER_FEMALE
            "Unknown" -> DialogEditor.GENDER_UNKNOWN
            else -> throw IllegalArgumentException("Invalid gender=$gender")
        }
        DialogEditor.genderPick(
            context,
            genderNum
        ) { data: Any? -> mUserRef!!.child("gender").setValue(data) }
    }

    private fun onTextFieldClick(view: View) = View.OnClickListener {
        val id = view.id
        when (id) {
            R.id.profile_name -> {
                val name = binding.profileName.text.toString()
                DialogEditor.inputDialog(
                    context,
                    "Change name",
                    name
                ) { data: Any? -> mUserRef!!.child("name").setValue(data) }
            }
            R.id.profile_description -> {

                val bio = binding.profileDescription.text.toString()
                DialogEditor.inputDialog(
                    context,
                    "Change bio",
                    bio
                ) { data: Any? -> mUserRef!!.child("bio").setValue(data) }
            }
        }
    }
}