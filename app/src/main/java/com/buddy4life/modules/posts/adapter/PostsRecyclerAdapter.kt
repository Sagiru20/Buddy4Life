package com.buddy4life.modules.posts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.R
import com.buddy4life.model.Post
import com.buddy4life.modules.posts.PostsRecyclerViewActivity

class PostsRecyclerAdapter(private var posts: List<Post>?) :
    RecyclerView.Adapter<PostViewHolder>() {
    var listener: PostsRecyclerViewActivity.OnItemClickListener? = null

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.posts_list_row, parent, false
        )
        return PostViewHolder(itemView, listener, posts)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts?.get(position)
        holder.bind(post)
    }
}