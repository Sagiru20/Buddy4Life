package com.buddy4life.modules.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.buddy4life.R
import com.buddy4life.databinding.FragmentPostBinding
import com.buddy4life.model.Post.PostModel
import com.buddy4life.model.Post.Post
import com.buddy4life.model.User.UserModel
import com.squareup.picasso.Picasso

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
        binding.tvDogName.text = post?.name
        binding.tvDogBreed.text = post?.breed
        binding.tvDogGender.text = post?.gender.toString()
        binding.tvDogAge.text = post?.age.toString()
        binding.tvDogDescription.text = post?.description.toString()

        val currentUserUID = UserModel.instance.currentUser()?.uid
        val isPostOfUser = (post?.ownerId == currentUserUID)

        if (isPostOfUser) {

            binding.ivDeletePost?.visibility = View.VISIBLE

            binding.ivDeletePost.setOnClickListener {

                post?.id?.let {

                    PostModel.instance.deletePost(post!!.id) { isPostDeleted ->
                        if (isPostDeleted) {

                            //Todo make toast deleted successfully

                        } else {

                        }
                    }

                }

                Navigation.findNavController(it)
                    .navigate(R.id.action_postFragment_to_myPostsFragment)
            }
        }


        PostModel.instance.getPostDogImageUri(post?.id) { uri ->
            uri?.let {
                Log.i("TAG", "Setting image from uri: $uri")
                Picasso.get().load(uri).into(binding.ivDogImage)

            }

        }


        // Add text values to Dog Information card
        binding.tvDogInfoName.text = post?.name
        binding.tvDogInfoBreed.text = post?.breed
        binding.tvDogInfoGender.text = post?.gender.toString()
        binding.tvDogInfoAge.text = post?.age.toString()
        binding.tvDogInfoWeight.text = post?.weight?.toString() ?: "-"
        binding.tvDogInfoHeight.text = post?.height?.toString() ?: "-"


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
