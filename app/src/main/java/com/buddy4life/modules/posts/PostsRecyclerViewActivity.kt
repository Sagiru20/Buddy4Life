package com.buddy4life.modules.posts

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.R
import com.buddy4life.model.Post
import com.buddy4life.modules.posts.adapter.PostsRecyclerAdapter

class PostsRecyclerViewActivity : AppCompatActivity() {
    private var postsRecyclerView: RecyclerView? = null
    private var posts: List<Post>? = mutableListOf(
        Post("dog1", "breed1", 1),
        Post("dog2", "breed2", 2),
        Post("dog3", "breed3", 3),
        Post("dog4", "breed4", 4),
        Post("dog5", "breed5", 5),
        Post("dog6", "breed6", 6),
        Post("dog7", "breed7", 7),
        Post("dog8", "breed8", 8),
        Post("dog9", "breed9", 9),
        Post("dog10", "breed10", 10)
    )
    private var adapter: PostsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        postsRecyclerView = findViewById(R.id.rvPosts)
        postsRecyclerView?.setHasFixedSize(true)
        postsRecyclerView?.layoutManager = LinearLayoutManager(this)

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
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPostClicked(post: Post?)
    }
}