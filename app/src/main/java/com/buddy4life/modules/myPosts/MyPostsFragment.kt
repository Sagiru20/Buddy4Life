package com.buddy4life.modules.myPosts

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.databinding.FragmentPostsBinding
import com.buddy4life.model.Gender
import com.buddy4life.model.Model
import com.buddy4life.model.Post
import com.buddy4life.modules.posts.adapter.PostsRecyclerAdapter
import com.google.firebase.firestore.FieldValue

class MyPostsFragment : Fragment() {
    private lateinit var binding: FragmentPostsBinding

    private var postsRecyclerView: RecyclerView? = null
    private var adapter: PostsRecyclerAdapter? = null

    private var posts: List<Post>? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        postsRecyclerView = binding.rvPosts
        postsRecyclerView?.setHasFixedSize(true)
        postsRecyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(posts)


//        var post1 :Post = Post("updated", "Afador", Gender.MALE, 24, "im trying things", null,100, height = 200)
//        Model.instance.updatePost(post1, "5X6SYc8J0Zjirr4k9Gad") {}

        Model.instance.getUserPosts { posts ->
            this.posts = posts
            adapter?.posts = posts
            adapter?.notifyDataSetChanged()
        }

        postsRecyclerView?.adapter = adapter
        return binding.root
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPostClicked(post: Post, callback: () -> Unit)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        Model.instance.getUserPosts { posts ->
            this.posts = posts
            adapter?.posts = posts
            adapter?.notifyDataSetChanged()
        }
    }
}