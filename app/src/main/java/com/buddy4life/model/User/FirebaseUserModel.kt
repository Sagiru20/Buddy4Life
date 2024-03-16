package com.buddy4life.model.User

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FirebaseUserModel {

    private val auth: FirebaseAuth = Firebase.auth
    private val currentUser = auth.currentUser
    val db = Firebase.firestore
    val storage = Firebase.storage


    init {
        val settings = com.google.firebase.firestore.firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
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


    fun registerUser(email: String, password: String, callback: (FirebaseUser?) -> Unit) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
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


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
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


    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        Log.d(
            "TAG",
            "User trying to update with name & photouri: " + user.name + "  " + user.photoUrl
        )
        //TODO maybe the ID will be email

        val email = Firebase.auth.currentUser?.email

        email?.let {

            db.collection(USERS_COLLECTION_NAME).document(email)
                .set(user.json)
                .addOnSuccessListener {
                    Log.d("TAG", "User document updated!")
                    callback(true)
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error updating user document", e)

                    callback(false)
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
                    Log.w("TAG", "user saved to FB the id is: " + documentReference)
                    callback()
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                    callback()
                }

        }

    }

    fun setUserImageProfile(user: User, callback: (String?) -> Unit) {


        var ref = FirebaseStorage.getInstance().reference
        var imageRef = ref.child("${USER_PROFILE_PICTURE_FOLDER_NAME}/${System.currentTimeMillis()}.jpg")

        var uploadTask = imageRef?.putFile(user.photoUrl!!.toUri())

        uploadTask?.addOnFailureListener {

            Log.i("TAG", "failed to save user photoUri")
            callback(null)

        }?.addOnSuccessListener { taskSnapshot ->
            Log.i("TAG", "succeeded to save user photo url!")

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Convert Uri to String
                callback(uri.toString())
            }

        }

        callback(null)
    }


    fun getUserImageUri(imageId: String?, callback: (Uri?) -> Unit) {

        imageId?.let {

            var storageRef = storage.reference.child("${USER_PROFILE_PICTURE_FOLDER_NAME}/${imageId}.jpg")
            storageRef.downloadUrl.addOnSuccessListener { uri ->

                Log.i("TAG", "successeded to get Uri")
                callback(uri)

            }.addOnFailureListener {

                Log.i("TAG", "failed to get user image uri!")
                callback(null)

            }
        }

        callback(null)
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

    fun logout(callback: () -> Unit) {

        Firebase.auth.signOut()

        callback()

    }

}