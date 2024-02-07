package com.buddy4life.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.buddy4life.dao.AppLocalDatabase
import java.util.concurrent.Executors

class Model private constructor() {

    private val database = AppLocalDatabase.db
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
//        executor.execute {
//            val posts = database.postDao().getAll()
//            mainHandler.post {
//                callback(posts)
//            }
//        }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        firebaseModel.addPost(post, callback)
//        executor.execute {
//            database.postDao().insert(post)
//            mainHandler.post {
//                callback()
//            }
//        }
    }
}