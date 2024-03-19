package com.buddy4life.model.Post

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.buddy4life.dao.AppLocalDatabase
import java.util.concurrent.Executors

class PostModel private constructor() {

    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private val posts: LiveData<MutableList<Post>>? = null
    private val firebasePostModel = FirebasePostModel()

    companion object {
        val instance: PostModel = PostModel()
    }

    interface GetAllPostsListener {
        fun onComplete(posts: List<Post>)
    }

    fun getAllPosts(): LiveData<MutableList<Post>> {
        refreshAllPosts()
        return posts ?: database.postDao().getAll()
    }

    fun refreshAllPosts() {
        val lastUpdated: Long = Post.lastUpdated
        firebasePostModel.getAllPosts(lastUpdated) { postsList ->
            Log.i("TAG", "Firebase returned ${postsList.size}, lastUpdated: $lastUpdated")

            executor.execute {
                var time = lastUpdated
                for (post in postsList) {
                    if (!post.isExists) {
                        executor.execute { database.postDao().delete(post) }
                    } else {
                        database.postDao().insert(post)
                    }

                    post.lastUpdated?.let {
                        if (time < it) time = post.lastUpdated ?: System.currentTimeMillis()
                    }
                }

                Post.lastUpdated = time
            }
        }
    }


    fun getPost(id: String, callback: (Post?) -> Unit) {
        firebasePostModel.getPost(id) { post -> callback(post) }
    }


    fun addPost(post: Post, callback: () -> Unit) {


        firebasePostModel.addPost(post) { postId ->
            Log.i("TAG", "FB.addPost finished")
            postId.let {
                refreshAllPosts()

                if (!post?.dogImageUri.isNullOrEmpty()) {
                    Log.d("TAG", "Starting to save dog image")
                    firebasePostModel.setPostDogImage(postId, post.dogImageUri) {
                        callback()
                    }
                } else {
                    callback()
                }

            }

        }
    }


    fun updatePost(post: Post, isPostImageChanged: Boolean, callback: () -> Unit) {
        firebasePostModel.updatePost(post) {
            refreshAllPosts()

            if (isPostImageChanged && !post?.dogImageUri.isNullOrEmpty()) {
                firebasePostModel.setPostDogImage(post.id, post.dogImageUri) {
                    callback()
                }
            } else {
                callback()
            }
        }
    }

    fun getUserPosts(callback: (List<Post>) -> Unit) {
        firebasePostModel.getUserPosts() { posts ->
            refreshAllPosts()
            callback(posts)
        }
    }


    fun getPostDogImageUri(postId: String?, callback: (Uri?) -> Unit) {
        firebasePostModel.getPostDogImageUri(postId) { uri -> callback(uri) }
    }

    fun deletePost(post: Post, callback: (Boolean) -> Unit) {
        firebasePostModel.deletePost(post) { isPostDeleted ->
            refreshAllPosts()
            callback(isPostDeleted)
        }
    }
}