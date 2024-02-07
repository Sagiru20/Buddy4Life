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
//            isPersistenceEnabled = false
        }
        db.firestoreSettings = settings
    }


    fun getAllPosts(callback: (List<Post>) -> Unit) {
        db.collection(POSTS_COLLECTION_NAME).get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val posts: MutableList<Post> = mutableListOf()
                    for (json in it.result) {
                        val id = json.getString("id") ?: ""
                        val name = json.getString("name") ?: ""
                        val breed = json.getString("breed") ?: ""
                        val age = json.getLong("age") ?: 0

                        // cast age from Long to Int
                        val ageInt = age.toInt()
                        val post = Post(name, breed, ageInt)
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

    // Create a new user with a first and last name
    val post = hashMapOf(
        "name" to post.name,
        "breed" to post.breed,
        "age" to post.age,
    )

//    db.collection(POSTS_COLLECTION_PATH).document(post.id).set(post.json).addOnSuccessListener {
//        callback()
//    }

    // Add a new document with a generated ID
    db.collection(POSTS_COLLECTION_NAME)
    .add(post)
    .addOnSuccessListener { documentReference ->
        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
    }
    .addOnFailureListener { e ->
        Log.w("TAG", "Error adding document", e)
    }

    }


}