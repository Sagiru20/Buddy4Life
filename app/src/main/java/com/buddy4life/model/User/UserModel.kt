package com.buddy4life.model.User

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserModel {

    private val firebaseUserModel = FirebaseUserModel()

    companion object {
        val instance: UserModel = UserModel()
    }

    fun registerUser(user: User, password: String, callback: (User?) -> Unit) {
        firebaseUserModel.registerUser(user.email, password) { firebaseUser ->
            firebaseUser?.let {
                user.uid = it.uid
                if (!user.photoUrl.isNullOrEmpty()) {
                    this.updateUserProfileImage(user) {
                        Log.d("TAG", "User Successfully created")
                        callback(user)
                    }
                } else {
                    callback(user)
                }
            }
            Log.d("TAG", "Could not save user image but User created")
        }
    }

    fun addUser(user: User, callback: () -> Unit) {
        firebaseUserModel.addUser(user) {
            callback()
        }
    }

    fun signInUser(email: String, password: String, callback: (FirebaseUser?) -> Unit) {
        firebaseUserModel.signInUser(email, password) { firebaseUser ->
            callback(firebaseUser)
        }
    }

    fun getCurrentUserInfo(callback: (User?) -> Unit) {
        val email = Firebase.auth.currentUser?.email

        email?.let {
            firebaseUserModel.getUserInfoByEmail(email) { currentUserInfo ->
                Log.d("TAG", "User Info retrieved")
                callback(currentUserInfo)
            }
        }
    }

    fun getUserImageUri(imageId: String?, callback: (Uri?) -> Unit) {
        firebaseUserModel.getUserImageUri(imageId) { uri ->
            callback(uri)

        }

    }


    fun updateUser(user: User, callback: (Boolean) -> Unit) {

        firebaseUserModel.updateUser(user) { isUserSaved ->

            callback(isUserSaved)

        }

    }

    fun updateUserProfileImage(user: User, callback: () -> Unit) {
        firebaseUserModel.setUserImageProfile(user) { uri ->
            uri?.let {
                user.photoUrl = uri
                callback()
            }

        }
    }

    fun updateUserPassword(newPassword: String, callback: () -> Unit) {
        firebaseUserModel.updateUserPassword((newPassword)) {}
    }

    fun logout(callback: () -> Unit) {
        firebaseUserModel.logout() {
            callback()
        }
    }
}