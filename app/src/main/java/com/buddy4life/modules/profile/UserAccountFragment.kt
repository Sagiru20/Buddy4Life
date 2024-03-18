package com.buddy4life.modules.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.buddy4life.LoginActivity
import com.buddy4life.databinding.FragmentUserInfoBinding
import com.buddy4life.model.User.User
import com.buddy4life.model.User.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding

    private var user: User? = null
    private var imageUri: String? = null
    private var isUserImageChanged: Boolean = false

    private val launcher = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.ivUserImage.load(uri) {
                crossfade(true)
            }

            imageUri = uri.toString()
            isUserImageChanged = true
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
        loadUserInfo()
        updateEditingState(false)

        binding.layoutLogout.setOnClickListener {
            UserModel.instance.logout {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnEditProfile.setOnClickListener {
            updateEditingState(true)
        }

        binding.btnCancelProfile.setOnClickListener {
            updateEditingState(false)
            binding.etUserName.setText(binding.tvUserName.text)
        }

        binding.btnSaveProfile.setOnClickListener {
            val currentUser: FirebaseUser? = Firebase.auth.currentUser
            if (imageUri == null) imageUri = user?.photoUrl

            if (currentUser != null && currentUser.email != null) {
                val editedUser = User(
                    currentUser.uid,
                    binding.etUserName.text.toString(),
                    imageUri,
                    currentUser.email!!
                )
                Log.d("TAG", "User that is going to be saved is: ${editedUser.name}")

                if (binding.etUserName.text.isNotEmpty()) {
                    if (isUserImageChanged) {
                        UserModel.instance.updateUserProfileImage(editedUser) {
                            updateUser(editedUser)
                        }
                    } else {
                        updateUser(editedUser)
                    }
                } else {
                    Log.d("TAG", "User name cannot be empty")
                }
            }

            updateEditingState(false)
        }
    }

    private fun updateUser(user: User) {
        UserModel.instance.updateUser(user) { isUserSaved ->
            val message = if (isUserSaved) "Profile Updated Successfully!"
            else "Failed to update user profile."
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (isUserSaved) binding.tvUserName.text = user.name
        }
    }

    private fun updateEditingState(editingState: Boolean) {
        with(binding) {
            if (editingState) {
                binding.ivUserImage.setOnClickListener {
                    launcher.launch(
                        PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
                    )
                }
            } else {
                ivUserImage.setOnClickListener(null)
            }
            btnEditProfile.visibility = if (editingState) View.GONE else View.VISIBLE
            tvUserName.visibility = if (editingState) View.GONE else View.VISIBLE

            btnCancelProfile.visibility = if (editingState) View.VISIBLE else View.GONE
            btnSaveProfile.visibility = if (editingState) View.VISIBLE else View.GONE
            cancelAndSaveProfileDivider.visibility = if (editingState) View.VISIBLE else View.GONE
            etUserName.visibility = if (editingState) View.VISIBLE else View.GONE

            isUserImageChanged = false
        }
    }

    private fun loadUserInfo() {
        UserModel.instance.getCurrentUserInfo { currentUserInfo ->
            user = currentUserInfo
            binding.tvUserEmail.text = currentUserInfo?.email
            binding.tvUserName.text = currentUserInfo?.name
            binding.etUserName.setText(currentUserInfo?.name)

            if (!currentUserInfo?.photoUrl.isNullOrEmpty()) {
                Picasso.get().load(currentUserInfo!!.photoUrl).into(binding.ivUserImage)
            }
        }
    }
}