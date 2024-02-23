package com.buddy4life.model.Post

import android.net.Uri
import android.os.Looper
import android.util.Log
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

                //TODO decide if its string or null
                if (!dogUri.isNullOrEmpty()) {

                    this.setPostDogImage(postId, dogUri) { isDogImageSaved ->

                        if(!isDogImageSaved) {

                            callback()

                        }

                    }

                }

            }

            callback()

        }

    }

    fun updatePost(post: Post, callback: (Boolean) -> Unit) {

        firebasePostModel.updatePost(post) { isPostUpdated ->

            callback(isPostUpdated)

        }

    }

    fun setPostDogImage(postId: String, dogImageUri: String, callback: (Boolean) -> Unit) {

        firebasePostModel.setPostDogImage(postId, dogImageUri) { isDogImageSaved ->

            callback(isDogImageSaved)

        }

    }


    fun getUserPosts(callback: (List<Post>) -> Unit) {

        firebasePostModel.getUserPosts() { posts ->

            callback(posts)

        }

    }


    fun getPostDogImageUri(postId: String?, callback: (Uri?) -> Unit) {

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