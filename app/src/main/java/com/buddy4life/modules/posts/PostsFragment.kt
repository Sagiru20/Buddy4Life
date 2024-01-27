package com.buddy4life.modules.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.databinding.FragmentPostsBinding
import com.buddy4life.model.Model
import com.buddy4life.model.Post
import com.buddy4life.modules.posts.adapter.PostsRecyclerAdapter

class PostsFragment : Fragment() {
    private var postsRecyclerView: RecyclerView? = null
    private var posts: List<Post>? = null
    private var adapter: PostsRecyclerAdapter? = null

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        val view = binding.root

        Model.instance.getAllPosts { posts ->
            this.posts = posts
            adapter?.posts = posts
            adapter?.notifyDataSetChanged()
        }

        postsRecyclerView = binding.rvPosts
        postsRecyclerView?.setHasFixedSize(true)
        postsRecyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(posts)
        adapter?.listener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.i("TAG", "PostsRecyclerAdapter: Position clicked $position")
            }

            override fun onPostClicked(post: Post?) {
                Log.i("TAG", "POST $post")
            }
        }

        postsRecyclerView?.adapter = adapter
        return view
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPostClicked(post: Post?)
    }

    override fun onResume() {
        super.onResume()
        Model.instance.getAllPosts { posts ->
            this.posts = posts
            adapter?.posts = posts
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}