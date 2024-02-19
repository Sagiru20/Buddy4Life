package com.buddy4life.modules.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.buddy4life.databinding.FragmentUserInfoBinding
import com.buddy4life.model.User.UserModel
import com.squareup.picasso.Picasso

class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        return binding.root


    }

    private fun setupUI(view: View) {

        Log.d("TAG", "trying etCurrentUserInfo")
        UserModel.instance.getCurrentUserInfo { currentUser ->
            Log.d("TAG", "in userAccoun, user name is: ${currentUser?.name}")

            binding.tvUserDisplayName.text = currentUser?.name
            binding.tvUserEmail.text = currentUser?.email

            UserModel.instance.getUserImageUri(currentUser?.uid) { uri ->
                uri?.let {

                    Picasso.get().load(uri).into(binding.ivUserImage)

                }


            }

        }

    }



}