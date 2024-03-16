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

            currentUser?.photoUrl?.let {

                Picasso.get().load(currentUser.photoUrl).into(binding.ivUserImage)

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

                    var newUser = User(
                        UserModel.instance.currentUser()!!.uid,
                        binding.etUserDisplayName.text.toString(),
                        imageUri,
                        UserModel.instance.currentUser()!!.email!!
                    )

                    Log.d("TAG", "imageUri when building user is :  ${newUser?.photoUrl} ")

                    if (binding.etUserDisplayName.text.isNotEmpty()) {

                        if (isUserImageProfileChanged) {

                            UserModel.instance.updateUserProfileImage(newUser) { isImageSaved ->
                                if (isImageSaved) {

                                    UserModel.instance.updateUser(newUser) { isUserSaved ->

                                        if (isUserSaved) {

                                            Log.i("TAG", "User: ${newUser.name} updated")
                                            Toast.makeText(
                                                requireContext(),
                                                "Profile Updated Successfully!",
                                                Toast.LENGTH_SHORT,
                                            ).show()

                                            Navigation.findNavController(it)
                                                .navigate(R.id.action_editAccountFragment_to_userAccountFragment)

                                        }
                                        else {

                                            Toast.makeText(
                                                requireContext(),
                                                "Failed to update profile, try later",
                                                Toast.LENGTH_SHORT,
                                            ).show()

                                            Navigation.findNavController(it)
                                                .navigate(R.id.action_editAccountFragment_to_userAccountFragment)

                                        }

                                    }


                                }

                            }

                        } else {

                            UserModel.instance.updateUser(newUser) { isUserSaved ->
                                if (isUserSaved) {

                                    Log.i("TAG", "User: ${newUser.name} updated")
                                    Toast.makeText(
                                        requireContext(),
                                        "Profile Updated Successfully!",
                                        Toast.LENGTH_SHORT,
                                    ).show()

                                    Navigation.findNavController(it)
                                        .navigate(R.id.action_editAccountFragment_to_userAccountFragment)

                                }

                            }

                        }


                    } else {

                        Log.d("TAG", "Display name cannot be empty")

                    }


                }

            }


        }

    }

}