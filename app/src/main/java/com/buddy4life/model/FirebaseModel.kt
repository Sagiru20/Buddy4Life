package com.buddy4life.model


import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.buddy4life.model.User.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FirebaseModel {

    val db = Firebase.firestore
    val storage = Firebase.storage

    companion object {
        const val POSTS_COLLECTION_NAME = "posts"
        const val POSTS_DOG_PICTURE_FOLDER_NAME = "PostsDogsPictures"
    }

//    init {
//        val settings = com.google.firebase.firestore.firestoreSettings {
//            setLocalCacheSettings(memoryCacheSettings {  })
//        }
//        db.firestoreSettings = settings
//    }


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

    //todo check that it works
    fun getUserPosts(callback: (List<Post>) -> Unit) {
        Log.d("TAG", "called: getUserPosts")
        db.collection(POSTS_COLLECTION_NAME).whereEqualTo ("ownerId", UserModel.instance.currentUser()?.uid).get().addOnCompleteListener {
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

    fun deletePost(postId: String, callback: (Boolean) -> Unit) {

        db.collection(POSTS_COLLECTION_NAME).document(postId)
        .delete()
            .addOnSuccessListener {it
                Log.d("TAG", "DocumentSnapshot successfully deleted!")
                callback(true)
            }
            .addOnFailureListener {
                e -> Log.w("TAG", "Error deleting document", e)
                callback(false)
            }

    }


    fun updatePost(post: Post, id: String , callback: () -> Unit) {
        post.lastUpdated = System.currentTimeMillis()
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


    fun getPost(id: String , callback: (Post?) -> Unit) {

        db.collection(POSTS_COLLECTION_NAME).document(id)
            .get()
            .addOnCompleteListener {

                when (it.isSuccessful) {
                    true -> {

                        Log.d("TAG", "DocumentSnapshot successfully retrieved!")
                        val postJson = it.result
                        val post = postJson.data?.let { data -> Post.fromJSON(data, postJson.id) }
                        callback(post)

                    }

                    false -> {
                        Log.w("TAG", "Error getting document")
                        callback(null)
                    }
                }

            }
            .addOnFailureListener {

            }

    }





    fun addPostDogImage(postId: String?,stringUri: String?,  callback: () -> Unit) {

        var ref = FirebaseStorage.getInstance().reference
        var imagesRef: StorageReference? = null

        postId?.let {
            imagesRef = ref.child("${POSTS_DOG_PICTURE_FOLDER_NAME}/${postId}")
        }

        stringUri?.let {

            var uploadTask = imagesRef?.putFile(stringUri.toUri())

            uploadTask?.addOnFailureListener {

                Log.i("TAG", "failed to save dog image")
                callback()

            }?.addOnSuccessListener { taskSnapshot ->
                Log.i("TAG", "succeeded to save dog image!")
                callback()

            }
        }

        callback()
    }



    fun getPostDogImageUri(postId: String?,  callback: (Uri?) -> Unit) {

        postId?.let {

            var storageRef = storage.reference.child("${POSTS_DOG_PICTURE_FOLDER_NAME}/${postId}")
            storageRef.downloadUrl.addOnSuccessListener {uri ->

                Log.i("TAG", "successeded to get Uri")
                callback(uri)

            }.addOnFailureListener {

                Log.i("TAG", "failed to get dog image uri!")
                callback(null)

            }
        }

        callback(null)
    }






}