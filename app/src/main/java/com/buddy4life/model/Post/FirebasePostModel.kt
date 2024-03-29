package com.buddy4life.model.Post


import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.buddy4life.model.User.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FirebasePostModel {

    val db = Firebase.firestore
    val storage = Firebase.storage

    init {
        val settings = com.google.firebase.firestore.firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        db.firestoreSettings = settings
    }

    companion object {
        const val POSTS_COLLECTION_NAME = "posts"
        const val POSTS_DOG_PICTURE_FOLDER_NAME = "PostsDogsPictures"
    }

    fun getAllPosts(since: Long, callback: (List<Post>) -> Unit) {
        db.collection(POSTS_COLLECTION_NAME)
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED_KEY, Timestamp(since, 0)).get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        Log.d("TAG", "posts retrieved by since: ${it.result.size()}")
                        val posts: MutableList<Post> = mutableListOf()
                        for (postJson in it.result) {
                            val post = Post.fromJSON(postJson.data, postJson.id)
                            posts.add(post)
                        }
                        Log.d("TAG", "after post.json size is: ${posts.size}")
                        callback(posts)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun addPost(post: Post, callback: (String?) -> Unit) {
        // Add a new document with a generated ID
        db.collection(POSTS_COLLECTION_NAME).add(post.json)
            .addOnSuccessListener { documentReference ->
                Log.w("TAG", "doc saved the id is: " + documentReference.id)
                callback(documentReference.id)
            }.addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
                callback(null)
            }
    }

    //todo check that it works
    fun getUserPosts(callback: (List<Post>) -> Unit) {


        Log.d("TAG", "called: getUserPosts")
        db.collection(POSTS_COLLECTION_NAME)
            .whereEqualTo("ownerId", Firebase.auth.currentUser?.uid).get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (postJson in it.result) {
                            val post = Post.fromJSON(
                                postJson.data, postJson.id
                            )
                            posts.add(post)
                        }
                        callback(posts)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun deletePost(post: Post, callback: (Boolean) -> Unit) {
        db.collection(POSTS_COLLECTION_NAME).document(post.id).set(post.markForDeletion)
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully deleted!")
                callback(true)
            }.addOnFailureListener { e ->
                Log.w("TAG", "Error deleting document", e)
                callback(false)
            }
    }


    fun updatePost(post: Post, callback: () -> Unit) {
        post.id.let {
            db.collection(POSTS_COLLECTION_NAME).document(post.id).set(post.json)
                .addOnSuccessListener {
                    Log.d("TAG", "Post successfully updated!")
                    callback()
                }.addOnFailureListener { e ->
                    Log.w("TAG", "Error updating post", e)
                }
        }
    }


    fun getPost(id: String, callback: (Post?) -> Unit) {
        db.collection(POSTS_COLLECTION_NAME).document(id).get().addOnCompleteListener {
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
        }.addOnFailureListener {}
    }


    fun setPostDogImage(postId: String?, postImageUri: String?, callback: () -> Unit) {

        var ref = FirebaseStorage.getInstance().reference
        var imageRef: StorageReference? = null

        postId?.let {
            imageRef = ref.child("$POSTS_DOG_PICTURE_FOLDER_NAME/${postId}")
        }
        Log.d("TAG", "Before postImageUri")
        postImageUri?.let {
            var uploadTask = imageRef?.putFile(postImageUri.toUri())
            uploadTask?.addOnFailureListener {
                Log.i("TAG", "failed to save dog image")
            }?.addOnSuccessListener { taskSnapshot ->
                Log.i("TAG", "succeeded to save dog image!")
                imageRef?.downloadUrl?.addOnSuccessListener { uri ->

                    db.collection(POSTS_COLLECTION_NAME).document(postId!!).update(Post.DOG_IMAGE_URL_KEY, uri.toString())
                        .addOnSuccessListener {
                            Log.d("TAG", "Post's image uri successfully updated!")
                            callback()
                        }.addOnFailureListener { e ->
                            Log.w("TAG", "Error updating post image uri", e)
                        }
                }

            }
        }
    }

    fun getPostDogImageUri(postId: String?, callback: (Uri?) -> Unit) {
        postId?.let {
            var storageRef = storage.reference.child("$POSTS_DOG_PICTURE_FOLDER_NAME/${postId}")
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.i("TAG", "successeded to get Uri")
                callback(uri)
            }.addOnFailureListener {
                Log.i("TAG", "failed to get dog image uri!")
                callback(null)
            }
        }
    }
}