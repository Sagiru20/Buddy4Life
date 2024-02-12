package com.buddy4life.model

import android.net.Uri
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
//import com.buddy4life.dao.AppLocalDatabase
import java.util.concurrent.Executors

class Model private constructor() {

//    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val firebaseModel = FirebaseModel()

    companion object {
        val instance: Model = Model()
    }

    interface GetAllPostsListener {
        fun onComplete(posts: List<Post>)
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {

        firebaseModel.getAllPosts(callback)

    }

    fun addPost(post: Post, dogUri: Uri?, callback: () -> Unit) {

        firebaseModel.addPost(post) {
            val postId = it
            firebaseModel.addPostDogImage(postId, dogUri) {
//                callback()
            }
            callback()

        }

    }

    fun updatePost(post: Post, id: String, callback: () -> Unit) {

        firebaseModel.updatePost(post, id, callback)

    }

    fun getUserPosts(callback: (List<Post>) -> Unit) {

        firebaseModel.getUserPosts(callback)

    }

    fun addPostDogImage(uri: Uri?, docId: String?, callback: () -> Unit) {

//        firebaseModel.addPostDogImage(uri, docId, callback)
    }

    fun deletePost(postId: String, callback: () -> Unit) {

        firebaseModel.deletePost(postId, callback)

    }
}