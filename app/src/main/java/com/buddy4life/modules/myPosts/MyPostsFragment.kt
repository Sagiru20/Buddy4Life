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
import com.buddy4life.model.User.FirebaseUserModel
import com.buddy4life.model.User.User
import com.buddy4life.model.User.UserModel
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

//        UserModel.instance.registerUser("fulltest@gmail.com", "password", "john doe", "someuri") {
//
//            Log.w("TAG", "Registered. Firebase User is: " +  it)
//
//        }
        val user = User ("another1", "photoUrl1", "hi7599@gmail.com")
        UserModel.instance.registerUser(user.email, "pass1234") {
            if (it?.uid != null) {
                user.uid = it.uid

                UserModel.instance.addUser(user) {
                    Log.d("TAG", "added user")

//                    UserModel.instance.getCurrentUserInfo {
//                        Log.d("TAG", "user return email: " + it?.email)
//                        Log.d("TAG", "user retrurn photoUrl: " + it?.photoUrl)
//                        Log.d("TAG", "user return uid is: " + it?.uid)
//                        Log.d("TAG", "user return name is: " + it?.name)
                    }
                }
             else {
                Log.w("TAG", "Erro adding user to Firebase, no UID returned")
            }


        }

//        UserModel.instance.signInUser("fulltest@gmail.com", "password") {
//            UserModel.instance.updateUserProfile("jonny", "someuri"){
//
//            }
//
//        }



//        UserModel.instance.getCurrentUserInfo() {
//
//            Log.w("TAG", "Firebase UserInfo is: " +  it)
//            Log.w("TAG", "email  is: " +  it?.email)
//        }



//        UserModel.instance.updateUserProfile("newOriEmail@gmail.com") {
//            Log.w("TAG", "finished updating. user name is: " + UserModel.instance.currentUser()?.displayName)
//
//        }

//        UserModel.instance.updateUserPassword("password1") {
//
//            Log.w("TAG", "Finished updating password")
//
//        }


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