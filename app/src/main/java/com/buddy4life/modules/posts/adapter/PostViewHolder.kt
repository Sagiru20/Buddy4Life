package com.buddy4life.modules.posts.adapter

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.R
import com.buddy4life.model.Post.Post
import com.buddy4life.model.Post.PostModel
import com.buddy4life.modules.myPosts.MyPostsFragmentDirections
import com.buddy4life.modules.posts.PostsFragmentDirections
import com.squareup.picasso.Picasso

class PostViewHolder(
    itemView: View,
    fragmentName: String,
) : RecyclerView.ViewHolder(itemView) {

    private var nameTextView: TextView? = null
    private var breedTextView: TextView? = null
    private var ageTextView: TextView? = null
    private var descriptionTextView: TextView? = null
    private var readMoreButton: Button? = null
    private var dogImageImageView: ImageView? = null
    private var fragmentName = fragmentName

    private var post: Post? = null

    init {
        nameTextView = itemView.findViewById(R.id.tvDogName)
        breedTextView = itemView.findViewById(R.id.tvDogBreed)
        ageTextView = itemView.findViewById(R.id.tvDogAge)
        descriptionTextView = itemView.findViewById(R.id.tvDogDescription)
        dogImageImageView = itemView.findViewById(R.id.ivDogImage)
        readMoreButton = itemView.findViewById(R.id.btnReadMore)
    }

    fun bind(post: Post?) {
        this.post = post
        nameTextView?.text = post?.name
        breedTextView?.text = post?.breed
        ageTextView?.text = post?.age.toString()
        descriptionTextView?.text = post?.description.toString()

        if (fragmentName == "POSTS") {
            val action =
                post?.let { PostsFragmentDirections.actionPostsFragmentToPostFragment(it.id) }
            readMoreButton?.setOnClickListener(action?.let {
                Navigation.createNavigateOnClickListener(it)
            })
        } else if (fragmentName == "MY_POSTS") {
            val myPostsToPostAction =
                post?.let { MyPostsFragmentDirections.actionMyPostsFragmentToPostFragment(it.id) }
            readMoreButton?.setOnClickListener(myPostsToPostAction?.let {
                Navigation.createNavigateOnClickListener(it)
            })
        }

        this.post?.dogImageUri?.let {
            if (this.post!!.dogImageUri!!.isNotEmpty()) {
                PostModel.instance.getPostDogImageUri(post?.id) { uri ->
                    uri?.let {
                        Picasso.get().load(uri).into(dogImageImageView)
                    }
                }
            }
        }
    }
}