package com.buddy4life.modules.myPosts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buddy4life.databinding.FragmentMyPostsBinding
import com.buddy4life.model.Post.PostModel
import com.buddy4life.model.Post.Post
import com.buddy4life.modules.posts.adapter.PostsRecyclerAdapter

class MyPostsFragment : Fragment() {
    private lateinit var binding: FragmentMyPostsBinding

    private var postsRecyclerView: RecyclerView? = null
    private var adapter: PostsRecyclerAdapter? = null

    private var posts: List<Post>? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false)

        postsRecyclerView = binding.rvPosts
        postsRecyclerView?.setHasFixedSize(true)
        postsRecyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(posts, "MY_POSTS")



//        UserModel.instance.getCurrentUserInfo() {
//
//            Log.w("TAG", "Firebase UserInfo is: " +  it)
//            Log.w("TAG", "email  is: " +  it?.email)
//        }



//        UserModel.instance.updateUserProfile("newOriEmail@gmail.com") {
//            Log.w("TAG", "finished updating. user name is: " + UserModel.instance.currentUser()?.displayName)
//
//        }



        PostModel.instance.getUserPosts { posts ->
            this.posts = posts
            adapter?.posts = posts
            adapter?.notifyDataSetChanged()
        }

        postsRecyclerView?.adapter = adapter
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        PostModel.instance.getUserPosts { posts ->
            this.posts = posts
            adapter?.posts = posts
            adapter?.notifyDataSetChanged()
        }
    }
}