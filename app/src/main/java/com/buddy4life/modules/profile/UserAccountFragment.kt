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
import com.buddy4life.R
import com.buddy4life.databinding.FragmentUserInfoBinding
import com.buddy4life.model.User.UserModel
import com.squareup.picasso.Picasso

class UserAccountFragment : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        loadUserInfo()
        return binding.root


    }

    private fun loadUserInfo() {


        UserModel.instance.getCurrentUserInfo { currentUser ->

            binding.tvUserDisplayName.text = currentUser?.name
            binding.tvUserEmail.text = currentUser?.email

//            UserModel.instance.getUserImageUri(currentUser?.photoUrl) { uri ->
            currentUser?.photoUrl?.let {

                    Picasso.get().load(currentUser.photoUrl).into(binding.ivUserImage)

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
    
    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}