package com.buddy4life.modules.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.buddy4life.LoginActivity
import com.buddy4life.databinding.FragmentUserInfoBinding
import com.buddy4life.model.User.User
import com.buddy4life.model.User.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding

    private lateinit var user: User
    private var imageUri: String? = null
    private var isUserImageProfileChanged = false
    private var isEditing: Boolean = false

    private val launcher = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.ivUserImage.load(uri) {
                crossfade(true)
            }

            imageUri = uri.toString()
            isUserImageProfileChanged = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        updateEditingState()
        loadUserInfo()

        binding.layoutLogout.setOnClickListener {
            UserModel.instance.logout {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnEditProfile.setOnClickListener {
            isEditing = true
            updateEditingState()
        }

        binding.btnCancelProfile.setOnClickListener {
            isEditing = false
            updateEditingState()
            binding.etUserName.setText(binding.tvUserName.text)
        }

        binding.btnSaveProfile.setOnClickListener {
            if (imageUri == null) {
                imageUri = user.photoUrl
            }

            if (Firebase.auth.currentUser?.uid != null && Firebase.auth.currentUser?.email != null) {
                val editedUser = User(
                    Firebase.auth.currentUser!!.uid,
                    binding.etUserName.text.toString(),
                    imageUri,
                    Firebase.auth.currentUser!!.email!!
                )
                Log.d("TAG", "User that is going to be saved is: $editedUser")

                if (binding.etUserName.text.isNotEmpty()) {
                    UserModel.instance.updateUser(editedUser) { isUserSaved ->
                        if (isUserSaved && isUserImageProfileChanged) {
                            UserModel.instance.updateUserProfileImage(editedUser) {}
                        }
                    }
                } else {
                    Log.d("TAG", "User name cannot be empty")
                }
            }

            isEditing = false
            updateEditingState()
        }
    }

    private fun updateEditingState() {
        with(binding) {
            if (isEditing) {
                binding.ivUserImage.setOnClickListener {
                    launcher.launch(
                        PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
                    )
                }
            } else {
                ivUserImage.setOnClickListener(null)
            }
            btnEditProfile.visibility = if (isEditing) View.GONE else View.VISIBLE
            tvUserName.visibility = if (isEditing) View.GONE else View.VISIBLE

            btnCancelProfile.visibility = if (isEditing) View.VISIBLE else View.GONE
            btnSaveProfile.visibility = if (isEditing) View.VISIBLE else View.GONE
            cancelAndSaveProfileDivider.visibility = if (isEditing) View.VISIBLE else View.GONE
            etUserName.visibility = if (isEditing) View.VISIBLE else View.GONE
        }
    }

    private fun loadUserInfo() {
        UserModel.instance.getCurrentUserInfo { currentUser ->
            binding.tvUserEmail.text = currentUser?.email
            binding.tvUserName.text = currentUser?.name
            binding.etUserName.setText(currentUser?.name)

            UserModel.instance.getUserImageUri(currentUser?.uid) { uri ->
                uri?.let {
                    Picasso.get().load(uri).into(binding.ivUserImage)
                }
            }
        }
    }
}