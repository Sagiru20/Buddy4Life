package com.buddy4life.model.User

import android.util.Log
import androidx.lifecycle.LiveData
import com.buddy4life.dao.AppLocalDatabase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class UserModel {

    private val firebaseUserModel = FirebaseUserModel()
    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private val user: LiveData<User>? = null

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

    fun getCurrentUserInfo(): LiveData<User>? {
        val uid = Firebase.auth.currentUser?.uid
        refreshUser()
        return user ?: uid?.let { database.userDao().getById(it) }
    }

    fun refreshUser() {
        val uid = Firebase.auth.currentUser?.uid

        uid?.let {
            firebaseUserModel.getUserInfoByUid(it) { currentUserInfo ->

                executor.execute {
                    if (currentUserInfo != null) {
                        database.userDao().insert(currentUserInfo)
                    }

                }
            }
        }
    }

    fun getUserInfo(uid: String?, callback: (User?) -> Unit) {
        uid?.let {
            firebaseUserModel.getUserInfoByUid(uid) { userInfo ->
                Log.d("TAG", "User Info retrieved")
                callback(userInfo)
            }
        }
        callback(null)
    }

    fun updateUser(user: User, callback: (Boolean) -> Unit) {

        firebaseUserModel.updateUser(user) { isUserSaved ->
            refreshUser()
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