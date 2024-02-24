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
import androidx.navigation.Navigation
import coil.load
import com.buddy4life.R
import com.buddy4life.databinding.FragmentEditProfileBinding
import com.buddy4life.model.User.FirebaseUserModel
import com.buddy4life.model.User.User
import com.buddy4life.model.User.UserModel
import com.squareup.picasso.Picasso

class EditAccountFragment : Fragment() {


    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        return binding.root


    }

    private fun setupUI(view: View) {

        var user: User?
        var imageUri: String? = null
        var isUserImageProfileChanged = false

        var launcher = registerForActivityResult<PickVisualMediaRequest, Uri>(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {

                binding.ivUserImage.load(uri) {
                    crossfade(true)
                }

                imageUri = uri?.toString()
                isUserImageProfileChanged = true
            }

        }



        Log.d("TAG", "trying etCurrentUserInfo")
        UserModel.instance.getCurrentUserInfo { currentUser ->

            user = currentUser
            Log.d("TAG", "in userAccoun, user name is: ${currentUser?.name}")

            binding.etUserDisplayName.setText(currentUser?.name)

            UserModel.instance.getUserImageUri(currentUser?.uid) { uri ->
                uri?.let {

                    Picasso.get().load(uri).into(binding.ivUserImage)

                }

            }

            binding.btnCancel.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_editAccountFragment_to_userAccountFragment
                )
            )


            binding.ivUserImage.setOnClickListener {
                launcher.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
                )
            }

            binding.tvUserEmail.text = currentUser?.email

            binding.btnSave.setOnClickListener {


                if (imageUri == null) {

                    imageUri = user?.photoUrl

                }

                if (UserModel.instance.currentUser()?.uid != null && UserModel.instance.currentUser()?.email != null) {

                    val newUser = User(
                        UserModel.instance.currentUser()!!.uid,
                        binding.etUserDisplayName.text.toString(),
                        imageUri,
                        UserModel.instance.currentUser()!!.email!!
                    )

                    Log.d("TAG", "user that is going to be saved is :  ${newUser?.name} ")

                    if (binding.etUserDisplayName.text.isNotEmpty()) {

                        UserModel.instance.updateUser(newUser) { isUserSaved ->

                            if (isUserSaved && isUserImageProfileChanged) {

                                UserModel.instance.updateUserProfileImage(newUser) {

                                    Log.d("TAG", "user save response is:  $isUserSaved")

                                }

                            }


                        }


                    } else {

                        Log.d("TAG", "Display name cannot be empty")

                    }


                }

                Navigation.findNavController(it)
                    .navigate(R.id.action_editAccountFragment_to_userAccountFragment)


            }


        }

    }

}