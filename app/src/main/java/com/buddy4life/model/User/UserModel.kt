package com.buddy4life.model.User

import android.util.Log
import com.buddy4life.model.FirebaseModel
import com.buddy4life.model.Model
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserModel {

    private val firebaseUserModel = FirebaseUserModel()

    companion object {
        val instance: UserModel = UserModel()
    }

    fun currentUser(): FirebaseUser? {
        return firebaseUserModel.currentUser()
    }


    fun registerUser(user: User, password : String, callback: (FirebaseUser?) -> Unit) {

        firebaseUserModel.registerUser(user.email, password) { firebaseUser ->
            firebaseUser?.let {

                if (it?.uid != null) {

                    user.uid = it.uid

                    user.photoUrl?.let {

                        firebaseUserModel.addUserImage(user) {

                            Log.d("TAG", "finished saving user image")

                        }

                    }

                }

            }

            callback(firebaseUser)

        }

        callback(null)

    }

    fun addUser(user: User, callback: (String) -> Unit) {

        firebaseUserModel.addUser(user) {

        }

    }



    fun signInUser(email: String, password : String, callback: (FirebaseUser?) -> Unit) {

        firebaseUserModel.signInUser(email, password) {firebaseUser ->

            callback(firebaseUser)

        }

    }

    fun getCurrentUserInfo(callback: (User?) -> Unit) {

        currentUser()?.email?.let { email ->

            firebaseUserModel.getUserInfoByEmail(email) { currentUserInfo ->

                callback(currentUserInfo)

            }

        }


    }


    fun updateUser(user: User, callback: () -> Unit) {

        firebaseUserModel.updateUser(user) {

        }


    }


    fun updateUserPassword(newPassword: String, callback: () -> Unit) {

        firebaseUserModel.updateUserPassword((newPassword)) {

        }

    }

}