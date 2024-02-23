package com.buddy4life.modules.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.buddy4life.LoginActivity
import com.buddy4life.MainActivity
import com.buddy4life.R
import com.buddy4life.databinding.FragmentUserInfoBinding
import com.buddy4life.model.Post.PostModel
import com.buddy4life.model.User.UserModel
import com.squareup.picasso.Picasso

class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        loadUserInfo(binding.root)
        return binding.root


    }

    private fun loadUserInfo(view: View) {


        UserModel.instance.getCurrentUserInfo { currentUser ->

            binding.tvUserDisplayName.text = currentUser?.name
            binding.tvUserEmail.text = currentUser?.email

            UserModel.instance.getUserImageUri(currentUser?.uid) { uri ->
                uri?.let {

                    Picasso.get().load(uri).into(binding.ivUserImage)

                }


            }


            binding.ivLogout.setOnClickListener {

                UserModel.instance.logout() {

                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)

                }

            }



            binding.btnEditProfile.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_userAccountFragment_to_editAccountFragment
                )
            )

        }

    }


    override fun onResume() {

        super.onResume()

    }

}