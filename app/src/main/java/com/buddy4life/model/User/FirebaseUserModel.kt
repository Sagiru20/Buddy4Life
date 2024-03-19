package com.buddy4life.model.User

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FirebaseUserModel {

    private val auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore
    val storage = Firebase.storage

    companion object {
        const val USERS_COLLECTION_NAME = "users"
        const val USER_PROFILE_PICTURE_FOLDER_NAME = "UsersProfilePictures"
    }

    fun registerUser(email: String, password: String, callback: (FirebaseUser?) -> Unit) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    callback(user)

                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    callback(null)
                }

            }

    }

    fun signInUser(email: String, password: String, callback: (FirebaseUser?) -> Unit) {


        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
                    callback(user)
                } else {
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    callback(null)
                }
            }

    }

    fun getUserInfoByUid(uid: String, callback: (User?) -> Unit) {
        db.collection(USERS_COLLECTION_NAME).document(uid).get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        Log.d("TAG", "User by uid successfully retrieved!")
                        val userJson = it.result
                        val user = userJson.data?.let { data -> User.fromJSON(data) }
                        callback(user)
                    }

                    false -> {
                        Log.w("TAG", "Error getting user's document by uid")
                        callback(null)
                    }
                }
            }
    }

    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        Log.d(
            "TAG", "User trying to update with name & photouri: " + user.name + "  " + user.photoUrl
        )
        val uid = Firebase.auth.currentUser?.uid

        uid?.let {
            db.collection(USERS_COLLECTION_NAME).document(uid).set(user.json).addOnSuccessListener {
                    Log.d("TAG", "User document updated!")
                    callback(true)
                }.addOnFailureListener { e ->
                    Log.w("TAG", "Error updating user document", e)

                    callback(false)
                }
        }
    }

    fun updateUserPassword(newPassword: String, callback: () -> Unit) {
        val user = Firebase.auth.currentUser

        user!!.updatePassword(newPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "User password updated.")
            } else {
                Log.d("TAG", "Failed to update User Password")
            }
        }
    }

    fun addUser(user: User, callback: () -> Unit) {
        // Add a new document with a generated ID
        user.uid.let {
            db.collection(USERS_COLLECTION_NAME).document(user.uid).set(user.json)
                .addOnSuccessListener { documentReference ->
                    Log.w("TAG", "user saved to FB the id is: " + documentReference)
                    callback()
                }.addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                    callback()
                }
        }
    }

    fun setUserImageProfile(user: User, callback: (String?) -> Unit) {
        var ref = FirebaseStorage.getInstance().reference
        var imageRef =
            ref.child("${USER_PROFILE_PICTURE_FOLDER_NAME}/${System.currentTimeMillis()}.jpg")
        var uploadTask = imageRef?.putFile(user.photoUrl!!.toUri())

        uploadTask?.addOnFailureListener {
            Log.i("TAG", "failed to save user photoUri")

        }?.addOnSuccessListener { taskSnapshot ->
            Log.i("TAG", "succeeded to save user photo url!")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Convert Uri to String
                callback(uri.toString())
            }

        }

    }

    fun logout(callback: () -> Unit) {
        Firebase.auth.signOut()
        callback()
    }
}