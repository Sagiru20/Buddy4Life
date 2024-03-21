package com.buddy4life.modules.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.buddy4life.LoginActivity
import com.buddy4life.R
import com.buddy4life.databinding.FragmentUserInfoBinding
import com.buddy4life.model.User.User
import com.buddy4life.model.User.UserModel
import com.buddy4life.modules.posts.PostsViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding

    private var imageUri: String? = null
    private var isUserImageChanged: Boolean = false
    private var progressBar: ProgressBar? = null
    private lateinit var viewModel: UserAccountViewModel

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
        progressBar = binding.progressBar
        viewModel = ViewModelProvider(this)[UserAccountViewModel::class.java]
        viewModel.user = UserModel.instance.getCurrentUserInfo()

        viewModel.user?.observe(viewLifecycleOwner) {
            setupUI()
        }

        setupUI()
        return binding.root
    }

    private fun setupUI() {
        progressBar?.visibility = View.VISIBLE
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

            viewModel.user?.value?.photoUrl?.let {
                val imageToLoad = if (it.isNotEmpty()) it else R.drawable.ic_account_24.toString()
                Picasso.get().load(imageToLoad).placeholder(R.drawable.ic_account_24)
                    .into(binding.ivUserImage)
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            val currentUser: FirebaseUser? = Firebase.auth.currentUser
            if (imageUri == null) imageUri = viewModel.user?.value?.photoUrl

            if (currentUser != null && currentUser.email != null) {
                val editedUser = User(
                    currentUser.uid,
                    binding.etUserName.text.toString(),
                    imageUri,
                    currentUser.email!!
                )
                Log.d("TAG", "User photo that is going to be saved is: ${editedUser.photoUrl}")

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
            if (isUserSaved) loadUserInfo()
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

        binding.tvUserEmail.text = viewModel.user?.value?.email
        binding.tvUserName.text = viewModel.user?.value?.name
        binding.etUserName.setText(viewModel.user?.value?.name)

        viewModel.user?.value?.photoUrl?.let {
            if (it.isNotEmpty()) {
                Picasso.get().load(it).placeholder(R.drawable.ic_account_24)
                    .into(
                        binding.ivUserImage,
                        object : Callback {
                            override fun onSuccess() {
                                progressBar?.visibility = View.GONE
                            }

                            override fun onError(e: java.lang.Exception?) {
                                progressBar?.visibility = View.GONE
                            }
                        })
            }
        }

        if (viewModel.user?.value?.photoUrl.isNullOrEmpty()) {
            progressBar?.visibility = View.GONE
        }
    }



//    private fun loadUserInfo() {
//        UserModel.instance.getCurrentUserInfo { currentUserInfo ->
//            user = currentUserInfo
//            binding.tvUserEmail.text = currentUserInfo?.email
//            binding.tvUserName.text = currentUserInfo?.name
//            binding.etUserName.setText(currentUserInfo?.name)
//
//            currentUserInfo?.photoUrl?.let {
//                if (it.isNotEmpty()) {
//                    Picasso.get().load(it).placeholder(R.drawable.ic_account_24)
//                        .into(
//                            binding.ivUserImage,
//                            object : Callback {
//                                override fun onSuccess() {
//                                    progressBar?.visibility = View.GONE
//                                }
//
//                                override fun onError(e: java.lang.Exception?) {
//                                    progressBar?.visibility = View.GONE
//                                }
//                            })
//                }
//            }
//
//            if (currentUserInfo?.photoUrl == null) {
//                progressBar?.visibility = View.GONE
//            }
//        }
//    }
}