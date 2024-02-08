package com.buddy4life.model

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings

class FirebaseModel {

    val db = Firebase.firestore

    companion object {
        const val POSTS_COLLECTION_NAME = "posts"
    }

    init {
        val settings = com.google.firebase.firestore.firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
//            setLocalCacheSettings(persistentCacheSettings {  })
        }
        db.firestoreSettings = settings
    }


    fun getAllPosts(callback: (List<Post>) -> Unit) {
        db.collection(POSTS_COLLECTION_NAME).get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val posts: MutableList<Post> = mutableListOf()
                    for (json in it.result) {
                        val post = Post.fromJSON(json.data)
                        posts.add(post)
                    }
                    callback(posts)
                }

                false -> callback(listOf())
            }
//        executor.execute {
//            val posts = database.postDao().getAll()
//            mainHandler.post {
//                callback(posts)
//            }
//        }
        }
    }
//
    fun addPost(post: Post, callback: () -> Unit) {

    // Add a new document with a generated ID
    db.collection(POSTS_COLLECTION_NAME)
    .add(post.json)
    .addOnSuccessListener { documentReference ->
        callback()
    }
    .addOnFailureListener { e ->
        Log.w("TAG", "Error adding document", e)
    }

    }


}