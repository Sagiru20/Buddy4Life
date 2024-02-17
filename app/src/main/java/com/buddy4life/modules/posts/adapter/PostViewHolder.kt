package com.buddy4life.modules.posts.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.R
import com.buddy4life.model.Model
import com.buddy4life.model.Post
import com.squareup.picasso.Picasso
import com.buddy4life.modules.posts.PostsFragmentDirections

class PostViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private var nameTextView: TextView? = null
    private var breedTextView: TextView? = null
    private var ageTextView: TextView? = null
    private var descriptionTextView: TextView? = null
    private var readMoreButton: Button? = null
    private var dogImageImageView: ImageView? = null
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

        val action = post?.let { PostsFragmentDirections.actionPostsFragmentToPostFragment(it.id) }
        readMoreButton?.setOnClickListener(action?.let { Navigation.createNavigateOnClickListener(it) })

        Model.instance.getPostDogImageUri(post?.id) { uri ->
            uri?.let {
                Log.i("TAG", "Setting image from uri: $uri")
                Picasso.get().load(uri).into(dogImageImageView)

            }

        }

    }

}