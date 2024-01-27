package com.buddy4life.modules.posts.adapter

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.modules.posts.PostsFragment
import com.buddy4life.R
import com.buddy4life.model.Post

class PostViewHolder(
    private val itemView: View,
    private val listener: PostsFragment.OnItemClickListener?,
    var posts: List<Post>?
) : RecyclerView.ViewHolder(itemView) {

    private var nameTextView: TextView? = null
    private var breedTextView: TextView? = null
    private var ageTextView: TextView? = null
    private var post: Post? = null

    init {
        nameTextView = itemView.findViewById(R.id.tvDogName)
        breedTextView = itemView.findViewById(R.id.tvDogBreed)
        ageTextView = itemView.findViewById(R.id.tvDogAge)

        itemView.setOnClickListener {
            Log.i("TAG", "PostViewHolder: Position clicked $adapterPosition")

            listener?.onItemClick(adapterPosition)
            listener?.onPostClicked(post)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        nameTextView?.text = post?.name
        breedTextView?.text = post?.breed
        breedTextView?.text = post?.age.toString()
    }
}