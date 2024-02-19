package com.buddy4life.model.User

import android.net.Uri
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


    fun registerUser(user: User, password : String, callback: (User?) -> Unit) {

        firebaseUserModel.registerUser(user.email, password) { firebaseUser ->
            firebaseUser?.let {

                if (it?.uid != null) {


                    user.uid = it.uid

                    user.photoUrl?.let {

                        firebaseUserModel.addUserImage(user) {isImageSaved ->

                           if (isImageSaved) {
                               Log.d("TAG", "trying to getUserImageUri")
                               firebaseUserModel.getUserImageUri(user.uid) { uri ->

                                   user.photoUrl = uri.toString()
                                   Log.d("TAG", "user.photoUrl is: ${user.photoUrl}")

                               }
                           }

                        }

                    }

                }

            }

            callback(user)

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
                Log.d("TAG", "We have user Info")

                callback(currentUserInfo)

            }

        }


    }


    fun getUserImageUri(uid: String?, callback: (Uri?) -> Unit) {

        firebaseUserModel.getUserImageUri(uid) {uri ->

            callback(uri)

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