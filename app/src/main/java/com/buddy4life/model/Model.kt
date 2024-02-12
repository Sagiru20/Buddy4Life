package com.buddy4life.model

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.buddy4life.dao.AppLocalDatabase
import java.util.concurrent.Executors

class Model private constructor() {

    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val instance: Model = Model()
    }

    interface GetAllPostsListener {
        fun onComplete(posts: List<Post>)
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        executor.execute {
            val posts = database.postDao().getAll()
            mainHandler.post {
                callback(posts)
            }
        }
    }

    fun getPost(id: Long, callback: (Post) -> Unit) {
        executor.execute {
            val post: Post = database.postDao().getById(id)
            mainHandler.post {
                callback(post)
            }
        }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        executor.execute {
            database.postDao().insert(post)
            mainHandler.post {
                callback()
            }
        }
    }
}