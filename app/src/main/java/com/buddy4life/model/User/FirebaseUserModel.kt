package com.buddy4life.model.User

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.buddy4life.model.FirebaseModel
import com.buddy4life.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FirebaseUserModel {

    private val auth: FirebaseAuth = Firebase.auth
    private val currentUser = auth.currentUser
    val db = Firebase.firestore
    val storage = Firebase.storage


    init {
        val settings = com.google.firebase.firestore.firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
        db.firestoreSettings = settings
    }


    companion object {

        const val USERS_COLLECTION_NAME = "users"
        const val USER_PROFILE_PICTURE_FOLDER_NAME = "UsersProfilePictures"

    }

    fun currentUser(): FirebaseUser? {
        return currentUser
    }


    fun registerUser(email: String, password : String, callback: (FirebaseUser?) -> Unit) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    callback(user)

//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    updateUI(null)
                    callback(null)
                }
            }


//        db.collection(USERS_COLLECTION_NAME).document(user.id).set(user.json)
//            .addOnSuccessListener {
//                callback()
//            }

        }

    fun signInUser(email: String, password : String, callback: (FirebaseUser?) -> Unit) {


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
//                    updateUI(user)
                    callback(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    updateUI(null)
                    callback(null)
                }
            }

    }


    fun getUserInfoByEmail(email: String, callback: (User?) -> Unit) {

        db.collection(FirebaseUserModel.USERS_COLLECTION_NAME).document(email)
            .get()
            .addOnCompleteListener {

                when (it.isSuccessful) {
                    true -> {

                        Log.d("TAG", "User by email successfully retrieved!")
                        val userJson = it.result
                        val user = userJson.data?.let { data -> User.fromJSON(data) }
                        callback(user)

                    }

                    false -> {

                        Log.w("TAG", "Error getting user's document")
                        callback(null)

                    }
                }
            }
    }



    fun updateUser(user: User, callback: () -> Unit) {
        Log.d("TAG", "User trying to update with name & photouri: " + user.name +"  " + user.photoUrl)
        //TODO maybe the ID will be email

            val uid = Firebase.auth.currentUser?.uid

            uid?.let {

                db.collection(FirebaseModel.POSTS_COLLECTION_NAME).document(uid)
                    .set(user.json)
                    .addOnSuccessListener {
                        Log.d("TAG", "DocumentSnapshot successfully updated!")
                        callback()
                    }
                    .addOnFailureListener {
                            e -> Log.w("TAG", "Error updating document", e)
                    }

            }

    }


    fun updateUserPassword(newPassword: String, callback: () -> Unit) {

        val user = Firebase.auth.currentUser

        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User password updated.")
                } else {

                    Log.d("TAG", "Failed to update User Password")

                }
            }

    }

    fun addUser(user: User, callback: () -> Unit) {

        // Add a new document with a generated ID
        user.uid?.let {

            db.collection(USERS_COLLECTION_NAME)
                .document(user.email)
                .set(user.json)
                .addOnSuccessListener { documentReference ->
                    Log.w("TAG", "user saved to FB the id is: " +  documentReference)
                    callback()
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                    callback()
                }

        }

    }

    fun addUserImage(user: User, callback: () -> Unit) {


        var ref = FirebaseStorage.getInstance().reference
        var imagesRef = ref.child("${USER_PROFILE_PICTURE_FOLDER_NAME}/${user.uid}")

        var uploadTask = imagesRef?.putFile(user.photoUrl!!.toUri())

        uploadTask?.addOnFailureListener {

            Log.i("TAG", "failed to save user photoUri")
            callback()

        }?.addOnSuccessListener { taskSnapshot ->
            Log.i("TAG", "succeeded to save user photo url!")
            callback()

        }

        callback()

    }


    //TODO check that this function works
    fun restUserPassword(email: String, callback: () -> Unit) {

        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "Email sent.")
                }
            }

    }
}