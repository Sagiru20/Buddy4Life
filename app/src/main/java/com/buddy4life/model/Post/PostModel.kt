package com.buddy4life.model.Post

import android.net.Uri
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.buddy4life.dao.AppLocalDatabase
import java.util.concurrent.Executors

class PostModel private constructor() {

    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val firebasePostModel = FirebasePostModel()

    companion object {
        val instance: PostModel = PostModel()
    }

    interface GetAllPostsListener {
        fun onComplete(posts: List<Post>)
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {

        firebasePostModel.getAllPosts() { posts ->

            callback(posts)

        }

    }

    fun getPost(id: String, callback: (Post?) -> Unit) {

        firebasePostModel.getPost(id) { post ->

            callback(post)

        }

    }


    fun addPost(post: Post, dogUri: String?, callback: () -> Unit) {

        firebasePostModel.addPost(post) {
            val postId = it
            postId?.let {

                firebasePostModel.addPostDogImage(postId, dogUri) {

                    callback()

                }

            }

        }

    }

    fun updatePost(post: Post, id: String, callback: () -> Unit) {

        firebasePostModel.updatePost(post, id) {

        }

    }

    fun getUserPosts(callback: (List<Post>) -> Unit) {

        firebasePostModel.getUserPosts() { posts ->

            callback(posts)

        }

    }


    fun getPostDogImageUri(postId: String?,  callback: (Uri?) -> Unit) {

        firebasePostModel.getPostDogImageUri(postId) { uri ->

            callback(uri)

        }

    }

    fun deletePost(postId: String, callback: (Boolean) -> Unit) {

        firebasePostModel.deletePost(postId) { isPostDeleted ->

            callback(isPostDeleted)

        }

    }
}