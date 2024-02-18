package com.buddy4life.modules.posts.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.R
import com.buddy4life.model.Post
import com.buddy4life.modules.posts.PostsFragment

class PostsRecyclerAdapter(var posts: List<Post>?, fragmentName: String) : RecyclerView.Adapter<PostViewHolder>() {

    var listener: PostsFragment.OnItemClickListener? = null
    var fragmentName = fragmentName

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.posts_list_row, parent, false
        )
        return PostViewHolder(itemView, fragmentName)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts?.get(position)
        holder.bind(post)
    }
}