package com.buddy4life.modules.posts

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.databinding.FragmentPostsBinding
import com.buddy4life.model.Post.Post
import com.buddy4life.model.Post.PostModel
import com.buddy4life.modules.posts.adapter.PostsRecyclerAdapter

class PostsFragment : Fragment() {
    private lateinit var binding: FragmentPostsBinding

    private var postsRecyclerView: RecyclerView? = null
    private var adapter: PostsRecyclerAdapter? = null
    private var progressBar: ProgressBar? = null
    private lateinit var viewModel: PostsViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)
        progressBar = binding.progressBar
        progressBar?.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this)[PostsViewModel::class.java]
        viewModel.posts = PostModel.instance.getAllPosts()
        postsRecyclerView = binding.rvPosts
        postsRecyclerView?.setHasFixedSize(true)
        postsRecyclerView?.layoutManager = LinearLayoutManager(context)

        adapter = PostsRecyclerAdapter(viewModel.posts?.value, "POSTS")
        postsRecyclerView?.adapter = adapter

        viewModel.posts?.observe(viewLifecycleOwner) {
            adapter?.posts = it
            adapter?.notifyDataSetChanged()
            progressBar?.visibility = View.GONE
        }

        return binding.root
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPostClicked(post: Post?)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        reloadPosts()
    }

    private fun reloadPosts() {
        progressBar?.visibility = View.VISIBLE
        PostModel.instance.refreshAllPosts()
        progressBar?.visibility = View.GONE
    }
}