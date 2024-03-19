package com.buddy4life.modules.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.buddy4life.R
import com.buddy4life.databinding.FragmentPostBinding
import com.buddy4life.model.Post.Post
import com.buddy4life.model.Post.PostModel
import com.buddy4life.model.User.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private var post: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        // Get the post from DB by the given id
        val postId: String = PostFragmentArgs.fromBundle(requireArguments()).postId
        PostModel.instance.getPost(postId) { post ->
            this.post = post
            setupUI()
        }

        return binding.root
    }

    private fun setupUI() {
        if (!post?.dogImageUri.isNullOrEmpty()) {
            Picasso.get().load(post?.dogImageUri).into(binding.ivDogImage)
        }

        binding.tvDogName.text = post?.name
        binding.tvDogBreed.text = post?.breed
        binding.tvDogGender.text = post?.gender.toString()
        binding.tvDogAge.text = post?.age.toString()
        binding.tvDogDescription.text = post?.description

        UserModel.instance.getUserInfo(post?.ownerId) { userInfo ->
            val date = Date(post?.createdTime!!)
            val creationTime =
                SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(date)

            "Created by ${userInfo?.name} at: $creationTime".also {
                binding.tvPostOwnerAndCreationTime.text = it
            }
        }

        // Add text values to Dog Information card
        binding.tvDogInfoName.text = post?.name
        binding.tvDogInfoBreed.text = post?.breed
        binding.tvDogInfoGender.text = post?.gender.toString()
        binding.tvDogInfoAge.text = post?.age.toString()
        binding.tvDogInfoWeight.text =
            if (post?.weight?.toString() != "0") post?.weight?.toString() else "-"
        binding.tvDogInfoHeight.text =
            if (post?.height?.toString() != "0") post?.height?.toString() else "-"

        // Check if the current user is the owner of the post
        if (post?.ownerId == Firebase.auth.currentUser?.uid) {
            binding.btnDeletePost.visibility = View.VISIBLE
            binding.btnEditPost.visibility = View.VISIBLE

            binding.btnDeletePost.setOnClickListener { view ->
                post?.id?.let {
                    PostModel.instance.deletePost(post!!) { isPostDeleted ->
                        val message = if (isPostDeleted) "Post deleted successfully"
                        else "Failed to delete post"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                        Navigation.findNavController(view)
                            .navigate(R.id.action_postFragment_to_myPostsFragment)
                    }
                }
            }

            val action =
                post?.let { PostFragmentDirections.actionPostFragmentToEditPostFragment(it.id) }

            binding.btnEditPost.setOnClickListener {
                if (action != null && post != null && !post?.id.isNullOrEmpty()) {
                    it.findNavController().navigate(action)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Can't update post right now",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}