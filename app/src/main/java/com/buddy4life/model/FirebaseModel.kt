package com.buddy4life.model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File

class FirebaseModel {

    val db = Firebase.firestore
    val storage = Firebase.storage

    companion object {
        const val POSTS_COLLECTION_NAME = "posts"
        const val USER_PROFILE_PICTURE_FOLDER_NAME = "UsersProfilePictures"
        const val POSTS_DOG_PICTURE_FOLDER_NAME = "PostsDogsPictures"
    }

    init {
        val settings = com.google.firebase.firestore.firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
        db.firestoreSettings = settings
    }


    fun getAllPosts(callback: (List<Post>) -> Unit) {
        Log.d("TAG", "called: getAllPosts")
        db.collection(POSTS_COLLECTION_NAME).get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val posts: MutableList<Post> = mutableListOf()
                    for (postJson in it.result) {
                        val post = Post.fromJSON(postJson.data, postJson.id)
                        posts.add(post)
                    }
                    callback(posts)
                }

                false -> callback(listOf())
            }
        }
    }
//
    fun addPost(post: Post, callback: (String) -> Unit) {

        // Add a new document with a generated ID
        db.collection(POSTS_COLLECTION_NAME)
        .add(post.json)
        .addOnSuccessListener { documentReference ->
            Log.w("TAG", "doc saved the id is: " +  documentReference.id)
            callback(documentReference.id)
        }
        .addOnFailureListener { e ->
            Log.w("TAG", "Error adding document", e)
            callback("")
        }
    }


    fun getUserPosts(callback: (List<Post>) -> Unit) {
        Log.d("TAG", "called: getUserPosts")
        db.collection(POSTS_COLLECTION_NAME).whereEqualTo("age", 11).get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val posts: MutableList<Post> = mutableListOf()
                    for (postJson in it.result) {
                        val post = Post.fromJSON(postJson.data, postJson.id)
                        posts.add(post)
                    }
                    callback(posts)
                }

                false -> callback(listOf())
            }
        }

    }

    fun deletePost(postId: String, callback: () -> Unit) {

        db.collection(POSTS_COLLECTION_NAME).document(postId)
        .delete()
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully deleted!")
                callback()
            }
            .addOnFailureListener {
                e -> Log.w("TAG", "Error deleting document", e)
            }

    }


    fun updatePost(post: Post, id: String , callback: () -> Unit) {
        post.lastUpdated = FieldValue.serverTimestamp()
        post.description ="Im updated all good"
        db.collection(POSTS_COLLECTION_NAME).document(id)
            .set(post.json)
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully updated!")
                callback()
            }
            .addOnFailureListener {
                    e -> Log.w("TAG", "Error updating document", e)
            }

    }



    fun addPostDogImage(postId: String?,uri: Uri?,  callback: () -> Unit) {
        Log.i("TAG", "starting saving image")
        // Create a storage reference from our app
        var storageRef = storage.getReference(POSTS_DOG_PICTURE_FOLDER_NAME)
        var ref = FirebaseStorage.getInstance().reference

//        var imagesRef: StorageReference? = storageRef.child("${POSTS_DOG_PICTURE_FOLDER_NAME}/test.png")
        var imagesRef: StorageReference? = null
        Log.i("TAG", "the complete docId is: " + postId)
        postId?.let {
            imagesRef = ref.child("${POSTS_DOG_PICTURE_FOLDER_NAME}/${postId}")
        }

        uri?.let {
            var uploadTask = imagesRef?.putFile(uri)
            Log.i("TAG", "uploaded?")
            uploadTask?.addOnFailureListener {
                Log.i("TAG", "failed to save dog image")
                callback()
            }?.addOnSuccessListener { taskSnapshot ->
                Log.i("TAG", "succeeded to save dog image!")
                callback()
            }
        }

        callback()

//        var file = Uri.fromFile(File("dog_icon_test.png"))
//        val file = File("test.txt").toUri()
//
//        var uploadTask = dogsRef.putFile(file)
//
//        // Register observers to listen for when the download is done or if it fails
//        uploadTask.addOnFailureListener {
//            Log.i("TAG", "failed to save dog image")
//            callback(listOf())
//        }.addOnSuccessListener { taskSnapshot ->
//            Log.i("TAG", "succeeded to save dog image!")
//            callback(listOf())
//        }

    }


}