package com.buddy4life.model.User

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseUser

class UserModel {

    private val firebaseUserModel = FirebaseUserModel()

    companion object {
        val instance: UserModel = UserModel()
    }

    fun currentUser(): FirebaseUser? {
        return firebaseUserModel.currentUser()
    }

    fun registerUser(user: User, password: String, callback: (User?) -> Unit) {
        firebaseUserModel.registerUser(user.email, password) { firebaseUser ->
            firebaseUser?.let {
                user.uid = it.uid
                user.photoUrl?.let {
                    this.updateUserProfileImage(user) {
                        Log.d("TAG", "User Successfully created")
                        callback(user)
                    }
                }
            }
            Log.d("TAG", "Could not save user image but User created")
            callback(user)
        }

        callback(null)
    }

    fun addUser(user: User, callback: (String) -> Unit) {
        firebaseUserModel.addUser(user) {}
    }

    fun signInUser(email: String, password: String, callback: (FirebaseUser?) -> Unit) {
        firebaseUserModel.signInUser(email, password) { firebaseUser ->
            callback(firebaseUser)
        }
    }

    fun getCurrentUserInfo(callback: (User?) -> Unit) {
        currentUser()?.email?.let { email ->
            firebaseUserModel.getUserInfoByEmail(email) { currentUserInfo ->
                Log.d("TAG", "We have user Info")
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

    fun updateUserProfileImage(user: User, callback: (Boolean) -> Unit) {
        firebaseUserModel.setUserImageProfile(user) { imageId ->
            imageId?.let {
                user.photoUrl = imageId
                callback(true)
            }

            callback(false)

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